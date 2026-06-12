import domain.Terminal;
import ports.TerminalPort;
import ports.ClientPort;
import services.TechService;
import storage.json.JsonTerminalStore;
import storage.json.JsonClientStore;

import java.util.Scanner;

public class TechnicianTest {
    public static void main(String[] args) {
        TerminalPort kioskPort = new JsonTerminalStore();
        ClientPort memberPort = new JsonClientStore();
        Terminal kiosk = kioskPort.loadTerminal();

        TechService service = new TechService(kiosk, kioskPort, memberPort);

        Scanner scanner = new Scanner("tech\n9999\n");
        boolean ok = service.login(scanner);

        assertTrue(ok, "Technician should login with correct credentials");
        System.out.println("TechnicianTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
