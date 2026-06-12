package services;

import domain.Terminal;
import ports.TerminalPort;
import ports.ClientPort;
import utils.JsonUtils;

import java.util.Scanner;

public class TechService {

    private final Terminal kiosk;
    private final TerminalPort kioskPort;
    private final ClientPort memberPort;

    public TechService(Terminal kiosk, TerminalPort kioskPort, ClientPort memberPort) {
        this.kiosk = kiosk;
        this.kioskPort = kioskPort;
        this.memberPort = memberPort;
    }

    public boolean login(Scanner scanner) {

        JsonUtils.JsonObject tech = JsonUtils.readJSON("data/technician.json");
        if (tech == null) {
            System.out.println("Technician file not found");
            return false;
        }

        System.out.print("Technician username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Technician password: ");
        String password = scanner.nextLine().trim();

        if (tech.getString("username").equalsIgnoreCase(username)
                && tech.getString("pin").equals(password)) {
            System.out.println("Technician login successful");
            return true;
        }

        System.out.println("Invalid technician credentials");
        return false;
    }

    public void viewATMStatus() {
        System.out.println("\n===== ATM STATUS =====");
        System.out.println("Total Cash: " + kiosk.getTotalCash());
        System.out.println("Receipt Paper: " + kiosk.getReceiptPaper());
        System.out.println("Ink Level: " + kiosk.getInk());
        System.out.println("Hardware Version: " + kiosk.getHardwareVersion());
        System.out.println("Firmware Version: " + kiosk.getFirmwareVersion());
        System.out.println("Cash by denomination:");
        System.out.println(" 200: " + kiosk.getCashCount(200));
        System.out.println(" 100: " + kiosk.getCashCount(100));
        System.out.println(" 50: " + kiosk.getCashCount(50));
        System.out.println(" 20: " + kiosk.getCashCount(20));
        System.out.println(" 10: " + kiosk.getCashCount(10));
    }

    public void refillCash(int denomination, int quantity) {
        kiosk.addCash(denomination, quantity);
        kioskPort.saveTerminal(kiosk);
        System.out.println("Cash refilled");
    }

    public void refillCashBySlots(int count10, int count20, int count50, int count100, int count200) {
        kiosk.addCash(10, count10);
        kiosk.addCash(20, count20);
        kiosk.addCash(50, count50);
        kiosk.addCash(100, count100);
        kiosk.addCash(200, count200);
        kioskPort.saveTerminal(kiosk);
        System.out.println("Cash refilled by slots");
    }

    public void refillPaper(int amount) {
        kiosk.refillPaper(amount);
        kioskPort.saveTerminal(kiosk);
        System.out.println("Receipt paper refilled");
    }

    public void refillInk(int amount) {
        kiosk.refillInk(amount);
        kioskPort.saveTerminal(kiosk);
        System.out.println("Ink refilled");
    }

    public void resetUserAttempts(String username) {
        var member = memberPort.findByUsername(username);
        if (member == null) {
            System.out.println("User not found");
            return;
        }

        member.resetFailedAttempts();
        member.unlock();
        memberPort.update(member);
        System.out.println("User unlocked & attempts reset");
    }

    public void upgradeHardware() {
        kiosk.upgradeHardware();
        kioskPort.saveTerminal(kiosk);
        System.out.println("Hardware upgraded to version " + kiosk.getHardwareVersion());
    }

    public void upgradeFirmware() {
        kiosk.upgradeFirmware();
        kioskPort.saveTerminal(kiosk);
        System.out.println("Firmware upgraded to version " + kiosk.getFirmwareVersion());
    }
}
