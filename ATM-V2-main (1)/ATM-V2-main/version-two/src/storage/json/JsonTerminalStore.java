package storage.json;

import domain.Terminal;
import ports.TerminalPort;
import utils.JsonUtils;

public class JsonTerminalStore implements TerminalPort {

    private static final String FILE = "data/atm.json";

    @Override
    public Terminal loadTerminal() {
        JsonUtils.JsonObject obj = JsonUtils.readJSON(FILE);
        if (obj == null) {
            return new Terminal(0, 0, 1, 1);
        }
        int receiptPaper = obj.getInt("receiptPaper");
        int ink = obj.has("ink") ? obj.getInt("ink") : 0;
        int hardwareVersion = obj.has("hardwareVersion") ? obj.getInt("hardwareVersion") : 1;
        int firmwareVersion = obj.has("firmwareVersion") ? obj.getInt("firmwareVersion") : 1;

        Terminal kiosk = new Terminal(
                receiptPaper,
                ink,
                hardwareVersion,
                firmwareVersion
        );
        if (obj.has("cash")) {
            JsonUtils.JsonObject cash = obj.getJSONObject("cash");
            kiosk.setCashCount(20, cash.optInt("20", 0));
            kiosk.setCashCount(50, cash.optInt("50", 0));
            kiosk.setCashCount(100, cash.optInt("100", 0));
            kiosk.setCashCount(200, cash.optInt("200", 0));
            kiosk.setCashCount(10, cash.optInt("10", 0));
        } else {
            kiosk.setCashCount(20, obj.optInt("cash20", 0));
            kiosk.setCashCount(50, obj.optInt("cash50", 0));
            kiosk.setCashCount(100, obj.optInt("cash100", 0));
            kiosk.setCashCount(200, obj.optInt("cash200", 0));
        }
        return kiosk;
    }

    @Override
    public void saveTerminal(Terminal kiosk) {
        JsonUtils.JsonObject obj = new JsonUtils.JsonObject();
        JsonUtils.JsonObject cash = new JsonUtils.JsonObject();
        cash.put("20", kiosk.getCashCount(20));
        cash.put("50", kiosk.getCashCount(50));
        cash.put("100", kiosk.getCashCount(100));
        cash.put("200", kiosk.getCashCount(200));
        cash.put("10", kiosk.getCashCount(10));
        obj.put("cash", cash);
        obj.put("receiptPaper", kiosk.getReceiptPaper());
        obj.put("ink", kiosk.getInk());
        obj.put("hardwareVersion", kiosk.getHardwareVersion());
        obj.put("firmwareVersion", kiosk.getFirmwareVersion());

        JsonUtils.writeFile(FILE, obj.toString(2));
    }
}
