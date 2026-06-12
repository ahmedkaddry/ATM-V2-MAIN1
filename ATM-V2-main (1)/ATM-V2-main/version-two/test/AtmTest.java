import ports.TerminalPort;
import storage.json.JsonTerminalStore;

public class AtmTest {
    public static void main(String[] args) {
        TerminalPort kioskPort = new JsonTerminalStore();
        var kiosk = kioskPort.loadTerminal();

        assertTrue(kiosk.getTotalCash() > 0, "Total cash should be positive");
        assertTrue(kiosk.getReceiptPaper() >= 0, "Receipt paper should be non-negative");
        assertTrue(kiosk.getInk() >= 0, "Ink should be non-negative");

        System.out.println("AtmTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
