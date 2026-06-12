import ports.ClientPort;
import storage.json.JsonClientStore;

public class UserTest {
    public static void main(String[] args) {
        ClientPort memberPort = new JsonClientStore();

        var member = memberPort.findByUsername("alice");
        assertTrue(member != null, "User should exist");
        assertTrue(member.getBalance() >= 0, "Balance should be non-negative");

        System.out.println("UserTest passed");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
