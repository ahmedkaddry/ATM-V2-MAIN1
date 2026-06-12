import domain.Client;
import ports.TerminalPort;
import ports.ClientPort;
import ports.MovementPort;
import services.TellerService;
import storage.json.JsonTerminalStore;
import storage.json.JsonClientStore;
import storage.json.JsonMovementStore;

public class TellerServiceTest {
    public static void main(String[] args) {
        TerminalPort kioskPort = new JsonTerminalStore();
        ClientPort memberPort = new JsonClientStore();
        MovementPort recordPort = new JsonMovementStore();

        TellerService service = new TellerService(
                kioskPort.loadTerminal(),
                kioskPort,
                memberPort,
                recordPort
        );

        Client member = memberPort.findByUsername("alice");
        double start = member.getBalance();

        service.deposit(member, 100, false);
        assertTrue(member.getBalance() == start + 100, "Deposit should add balance");

        service.withdraw(member, 50, false);
        assertTrue(member.getBalance() == start + 50, "Withdraw should reduce balance");

        System.out.println("TellerServiceTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
