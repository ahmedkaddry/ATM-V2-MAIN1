package storage.json;

import domain.Client;
import ports.ClientPort;
import utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class JsonClientStore implements ClientPort {

    private static final String FILE = "data/users.json";

    @Override
    public Client findByUsername(String username) {

        JsonUtils.JsonArray users = JsonUtils.readArray(FILE);
        if (users == null) return null;

        for (int i = 0; i < users.length(); i++) {
            JsonUtils.JsonObject o = users.getJSONObject(i);

            if (o.getString("username").equalsIgnoreCase(username.trim())) {
                return new Client(
                        o.getString("username"),
                        o.getString("pin"),
                        o.getDouble("balance"),
                        o.getInt("failedAttempts"),
                        o.getBoolean("locked")
                );
            }
        }
        return null;
    }

    @Override
    public void update(Client member) {

        JsonUtils.JsonArray users = JsonUtils.readArray(FILE);
        if (users == null) {
            return;
        }

        for (int i = 0; i < users.length(); i++) {
            JsonUtils.JsonObject o = users.getJSONObject(i);

            if (o.getString("username").equalsIgnoreCase(member.getUsername())) {
                o.put("balance", member.getBalance());
                o.put("failedAttempts", member.getFailedAttempts());
                o.put("locked", member.isLocked());
                break;
            }
        }

        JsonUtils.writeFile(FILE, users.toString(2));
    }

    @Override
    public List<Client> findAll() {

        List<Client> result = new ArrayList<>();

        JsonUtils.JsonArray users = JsonUtils.readArray(FILE);
        if (users == null) return result;

        for (int i = 0; i < users.length(); i++) {
            JsonUtils.JsonObject o = users.getJSONObject(i);

            result.add(new Client(
                    o.getString("username"),
                    o.getString("pin"),
                    o.getDouble("balance"),
                    o.getInt("failedAttempts"),
                    o.getBoolean("locked")
            ));
        }

        return result;
    }
}
