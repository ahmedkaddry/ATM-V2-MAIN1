package storage.json;

import domain.Movement;
import domain.MovementKind;
import ports.MovementPort;
import utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonMovementStore implements MovementPort {

    private static final String FILE = "data/transactions.json";

    @Override
    public void save(Movement record) {
        JsonUtils.JsonArray arr;
        String content = JsonUtils.readFile(FILE);
        if (content == null || content.isEmpty()) {
            arr = new JsonUtils.JsonArray();
        } else {
            arr = JsonUtils.readArray(FILE);
            if (arr == null) {
                arr = new JsonUtils.JsonArray();
            }
        }

        JsonUtils.JsonObject obj = new JsonUtils.JsonObject();
        obj.put("username", record.getUsername());
        obj.put("amount", record.getAmount());
        obj.put("type", record.getType().name());
        obj.put("receipt", record.isReceiptPrinted());
        obj.put("time", record.getTime().toString());

        arr.put(obj);
        JsonUtils.writeFile(FILE, arr.toString(2));
    }

    @Override
    public List<Movement> findAll() {
        List<Movement> result = new ArrayList<>();

        JsonUtils.JsonArray arr = JsonUtils.readArray(FILE);
        if (arr == null) return result;

        for (int i = 0; i < arr.length(); i++) {
            JsonUtils.JsonObject o = arr.getJSONObject(i);
            result.add(new Movement(
                    o.getString("username"),
                    o.getInt("amount"),
                    MovementKind.valueOf(o.getString("type")),
                    o.getBoolean("receipt"),
                    LocalDateTime.parse(o.getString("time"))
            ));
        }

        return result;
    }
}
