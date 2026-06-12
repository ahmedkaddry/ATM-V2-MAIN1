package app;

import domain.Terminal;
import domain.Client;
import ports.TerminalPort;
import ports.ClientPort;
import ports.MovementPort;
import services.TechService;
import services.TellerService;
import services.AccessService;
import storage.json.JsonTerminalStore;
import storage.json.JsonClientStore;
import storage.json.JsonMovementStore;

import java.util.Scanner;

public class Launcher {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        JsonTerminalStore terminalStore = new JsonTerminalStore();
        JsonClientStore clientStore = new JsonClientStore();
        JsonMovementStore movementStore = new JsonMovementStore();

        Terminal terminal = terminalStore.loadTerminal();

        TellerService operationService =
                new TellerService(terminal, terminalStore, clientStore, movementStore);

        AccessService sessionService = new AccessService(clientStore);

        TechService engineerService =
                new TechService(terminal, terminalStore, clientStore);

        while (true) {
            System.out.println("\n===== ATM SYSTEM =====");
            System.out.println("1. User Login");
            System.out.println("2. Technician Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> {
                    Client client = sessionService.authenticate(scanner);
                    if (client != null) {
                        userMenu(client, operationService);
                    }
                }

                case "2" -> {
                    if (engineerService.login(scanner)) {
                        technicianMenu(engineerService);
                    }
                }

                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }

                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void userMenu(Client member, TellerService operationService) {

        while (true) {
            System.out.println("\n===== USER MENU =====");
            System.out.println("1. Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> System.out.println("Balance: " + member.getBalance());

                case "2" -> {
                    System.out.print("Amount: ");
                    double amount = readDouble();
                    boolean receipt = askReceipt();
                    operationService.deposit(member, amount, receipt);
                }

                case "3" -> {
                    System.out.print("Amount: ");
                    double amount = readDouble();
                    boolean receipt = askReceipt();
                    System.out.println("\n===== DENOMINATION PREFERENCE =====");
                    System.out.println("1. Standard (machine decides)");
                    System.out.println("2. Choose specific denominations (20, 50, 100, 200)");
                    System.out.print("Choose option: ");
                    String denomChoice = scanner.nextLine();

                    if (denomChoice.equals("2")) {
                        System.out.println("\nSelect which denominations you want (y/n):");
                        System.out.print("20? (y/n): ");
                        boolean want20 = scanner.nextLine().equalsIgnoreCase("y");
                        System.out.print("50? (y/n): ");
                        boolean want50 = scanner.nextLine().equalsIgnoreCase("y");
                        System.out.print("100? (y/n): ");
                        boolean want100 = scanner.nextLine().equalsIgnoreCase("y");
                        System.out.print("200? (y/n): ");
                        boolean want200 = scanner.nextLine().equalsIgnoreCase("y");

                        java.util.List<Integer> selected = new java.util.ArrayList<>();
                        if (want20) selected.add(20);
                        if (want50) selected.add(50);
                        if (want100) selected.add(100);
                        if (want200) selected.add(200);

                        if (selected.isEmpty()) {
                            System.out.println("No denominations selected. Using standard withdrawal.");
                            operationService.withdraw(member, amount, receipt);
                        } else {
                            int[] denoms = selected.stream().mapToInt(Integer::intValue).toArray();
                            operationService.withdrawWithDenomination(member, amount, receipt, denoms);
                        }
                    } else {
                        operationService.withdraw(member, amount, receipt);
                    }
                }

                case "4" -> {
                    System.out.print("Receiver username: ");
                    String receiver = scanner.nextLine();
                    System.out.print("Amount: ");
                    double amount = readDouble();
                    operationService.transfer(member, receiver, amount);
                }

                case "5" -> {
                    System.out.println("Returning to ATM menu");
                    return;
                }

                default -> System.out.println("Invalid option");
            }
        }
    }

    private static void technicianMenu(TechService tech) {

        while (true) {
            System.out.println("\n===== TECHNICIAN MENU =====");
            System.out.println("1. View ATM Status");
            System.out.println("2. Refill Cash");
            System.out.println("3. Refill Receipt Paper");
            System.out.println("4. Refill Ink");
            System.out.println("5. Reset User Attempts");
            System.out.println("6. Upgrade Hardware");
            System.out.println("7. Upgrade Firmware");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> tech.viewATMStatus();

                case "2" -> {
                    System.out.println("\n===== REFILL CASH OPTIONS =====");
                    System.out.println("1. Add total amount for a denomination");
                    System.out.println("2. Add amount for each slot (10, 20, 50, 100, 200)");
                    System.out.print("Choose option: ");
                    String cashOption = scanner.nextLine();

                    if (cashOption.equals("1")) {
                        System.out.print("Denomination: ");
                        int denom = Integer.parseInt(scanner.nextLine());
                        System.out.print("Quantity: ");
                        int qty = Integer.parseInt(scanner.nextLine());
                        tech.refillCash(denom, qty);
                    } else if (cashOption.equals("2")) {
                        System.out.print("Count for 10: ");
                        int count10 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Count for 20: ");
                        int count20 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Count for 50: ");
                        int count50 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Count for 100: ");
                        int count100 = Integer.parseInt(scanner.nextLine());
                        System.out.print("Count for 200: ");
                        int count200 = Integer.parseInt(scanner.nextLine());
                        tech.refillCashBySlots(count10, count20, count50, count100, count200);
                    } else {
                        System.out.println("Invalid option");
                    }
                }

                case "3" -> {
                    System.out.print("Amount: ");
                    tech.refillPaper(Integer.parseInt(scanner.nextLine()));
                }

                case "4" -> {
                    System.out.print("Amount: ");
                    tech.refillInk(Integer.parseInt(scanner.nextLine()));
                }

                case "5" -> {
                    System.out.print("Username: ");
                    tech.resetUserAttempts(scanner.nextLine());
                }

                case "6" -> tech.upgradeHardware();
                case "7" -> tech.upgradeFirmware();

                case "8" -> {
                    System.out.println("Returning to ATM menu");
                    return;
                }

                default -> System.out.println("Invalid option");
            }
        }
    }

    private static double readDouble() {
        return Double.parseDouble(scanner.nextLine());
    }

    private static boolean askReceipt() {
        System.out.print("Print receipt? (true/false): ");
        return Boolean.parseBoolean(scanner.nextLine());
    }
}
