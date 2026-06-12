package services;

import domain.Terminal;
import domain.Client;
import domain.Movement;
import domain.MovementKind;
import ports.TerminalPort;
import ports.ClientPort;
import ports.MovementPort;

import java.time.LocalDateTime;

public class TellerService {

    private final Terminal kiosk;
    private final TerminalPort kioskPort;
    private final ClientPort memberPort;
    private final MovementPort recordPort;

    public TellerService(
            Terminal kiosk,
            TerminalPort kioskPort,
            ClientPort memberPort,
            MovementPort recordPort
    ) {
        this.kiosk = kiosk;
        this.kioskPort = kioskPort;
        this.memberPort = memberPort;
        this.recordPort = recordPort;
    }

    public void deposit(Client member, double amount, boolean wantReceipt) {

        member.setBalance(member.getBalance() + amount);
        memberPort.update(member);

        boolean receiptPrinted = false;

        if (wantReceipt) {
            receiptPrinted = tryPrintReceipt(member, MovementKind.DEPOSIT, (int) amount);
        }

        recordPort.save(new Movement(
                member.getUsername(),
                (int) amount,
                MovementKind.DEPOSIT,
                receiptPrinted,
                LocalDateTime.now()
        ));

        printReceiptSummary("DEPOSIT", (int) amount, receiptPrinted);
        kioskPort.saveTerminal(kiosk);
        System.out.println("Deposit successful");
    }

    public void withdraw(Client member, double amount, boolean wantReceipt) {

        if (member.getBalance() < amount) {
            System.out.println("Insufficient balance");
            return;
        }

        if (!kiosk.canDispense((int) amount)) {
            System.out.println("ATM cannot dispense this amount");
            return;
        }

        member.setBalance(member.getBalance() - amount);
        kiosk.dispenseCash((int) amount);

        memberPort.update(member);

        boolean receiptPrinted = false;

        if (wantReceipt) {
            receiptPrinted = tryPrintReceipt(member, MovementKind.WITHDRAW, (int) amount);
        }

        recordPort.save(new Movement(
                member.getUsername(),
                (int) amount,
                MovementKind.WITHDRAW,
                receiptPrinted,
                LocalDateTime.now()
        ));

        printReceiptSummary("WITHDRAW", (int) amount, receiptPrinted);
        kioskPort.saveTerminal(kiosk);
        System.out.println("Withdrawal successful");
    }

    public void withdrawWithDenomination(Client member, double amount, boolean wantReceipt, int[] preferredDenoms) {

        if (member.getBalance() < amount) {
            System.out.println("Insufficient balance");
            return;
        }

        if (!kiosk.canDispense((int) amount)) {
            System.out.println("ATM cannot dispense this amount");
            return;
        }

        member.setBalance(member.getBalance() - amount);
        kiosk.dispenseCashWithPreference((int) amount, preferredDenoms);

        memberPort.update(member);

        boolean receiptPrinted = false;

        if (wantReceipt) {
            receiptPrinted = tryPrintReceipt(member, MovementKind.WITHDRAW, (int) amount);
        }

        recordPort.save(new Movement(
                member.getUsername(),
                (int) amount,
                MovementKind.WITHDRAW,
                receiptPrinted,
                LocalDateTime.now()
        ));

        printReceiptSummary("WITHDRAW", (int) amount, receiptPrinted);
        kioskPort.saveTerminal(kiosk);
        System.out.println("Withdrawal successful");
    }

    public void transfer(Client sender, String receiverUsername, double amount) {

        Client receiver = memberPort.findByUsername(receiverUsername);

        if (receiver == null) {
            System.out.println("Receiver not found");
            return;
        }

        if (sender.getBalance() < amount) {
            System.out.println("Insufficient balance");
            return;
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        memberPort.update(sender);
        memberPort.update(receiver);

        recordPort.save(new Movement(
                sender.getUsername(),
                (int) amount,
                MovementKind.TRANSFER,
                false,
                LocalDateTime.now()
        ));

        printReceiptSummary("TRANSFER", (int) amount, false);
        System.out.println("Transfer successful");
    }

    private boolean tryPrintReceipt(Client member, MovementKind type, int amount) {
        if (kiosk.getReceiptPaper() <= 0) {
            System.out.println("⚠ No receipt paper");
            return false;
        }

        if (kiosk.getInk() <= 0) {
            System.out.println("⚠ No ink");
            return false;
        }

        kiosk.useReceiptPaper();
        kiosk.useInk();

        printReceipt(member, type, amount);
        return true;
    }

    private void printReceipt(Client member, MovementKind type, int amount) {
        System.out.println("\n====== RECEIPT ======");
        System.out.println("User: " + member.getUsername());
        System.out.println("Transaction: " + type);
        System.out.println("Amount: " + amount);
        System.out.println("Balance: " + member.getBalance());
        System.out.println("Date: " + LocalDateTime.now());
        System.out.println("=====================\n");
    }

    private void printReceiptSummary(String type, int amount, boolean printed) {
        System.out.println("----- RECEIPT -----");
        System.out.println("Transaction: " + type);
        System.out.println("Amount: " + amount);
        System.out.println("-------------------");
    }
}
