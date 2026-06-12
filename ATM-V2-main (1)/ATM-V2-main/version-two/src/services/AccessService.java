package services;

import domain.Client;
import ports.ClientPort;

import java.util.Scanner;

public class AccessService {

    private static final int MAX_ATTEMPTS = 3;
    private final ClientPort memberPort;

    public AccessService(ClientPort memberPort) {
        this.memberPort = memberPort;
    }

    public Client authenticate(Scanner scanner) {

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        Client member = memberPort.findByUsername(username);

        if (member == null) {
            System.out.println("User not found");
            return null;
        }

        if (member.isLocked()) {
            System.out.println("Account is locked");
            return null;
        }

        System.out.print("PIN: ");
        String pin = scanner.nextLine().trim();

        if (member.getPin().equals(pin)) {
            member.setFailedAttempts(0);
            memberPort.update(member);
            System.out.println("Login successful");
            return member;
        }

        int attempts = member.getFailedAttempts() + 1;
        member.setFailedAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            member.setLocked(true);
            System.out.println("Account locked after 3 failed attempts");
        } else {
            System.out.println("Wrong PIN (" + attempts + "/3)");
        }

        memberPort.update(member);
        return null;
    }
}
