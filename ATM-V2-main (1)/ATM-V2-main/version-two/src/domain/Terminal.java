package domain;

import java.util.HashMap;
import java.util.Map;

public class Terminal {

    private final Map<Integer, Integer> cashByDenom = new HashMap<>();
    private int receiptPaper;
    private int ink;
    private int hardwareVersion;
    private int firmwareVersion;

    public Terminal(int receiptPaper, int ink, int hardwareVersion, int firmwareVersion) {
        this.receiptPaper = receiptPaper;
        this.ink = ink;
        this.hardwareVersion = hardwareVersion;
        this.firmwareVersion = firmwareVersion;
    }

    public void addCash(int denomination, int quantity) {
        cashByDenom.put(denomination, cashByDenom.getOrDefault(denomination, 0) + quantity);
    }

    public void setCashCount(int denomination, int quantity) {
        cashByDenom.put(denomination, Math.max(0, quantity));
    }

    public int getCashCount(int denomination) {
        return cashByDenom.getOrDefault(denomination, 0);
    }

    public int getTotalCash() {
        int total = 0;
        for (var entry : cashByDenom.entrySet()) {
            total += entry.getKey() * entry.getValue();
        }
        return total;
    }

    public boolean canDispense(int amount) {
        return amount > 0 && amount % 10 == 0 && getTotalCash() >= amount;
    }

    public void dispenseCash(int amount) {
        int remaining = amount;
        int[] denoms = {200, 100, 50, 20, 10};

        for (int denom : denoms) {
            int available = cashByDenom.getOrDefault(denom, 0);
            int needed = remaining / denom;
            int used = Math.min(needed, available);
            if (used > 0) {
                cashByDenom.put(denom, available - used);
                remaining -= used * denom;
            }
        }
    }

    public void dispenseCashWithPreference(int amount, int[] preferredDenoms) {
        int remaining = amount;

        for (int denom : preferredDenoms) {
            int available = cashByDenom.getOrDefault(denom, 0);
            int needed = remaining / denom;
            int used = Math.min(needed, available);
            if (used > 0) {
                cashByDenom.put(denom, available - used);
                remaining -= used * denom;
            }
        }

        if (remaining > 0) {
            int[] fallback = {200, 100, 50, 20, 10};
            for (int denom : fallback) {
                if (java.util.Arrays.binarySearch(preferredDenoms, denom) < 0) {
                    int available = cashByDenom.getOrDefault(denom, 0);
                    int needed = remaining / denom;
                    int used = Math.min(needed, available);
                    if (used > 0) {
                        cashByDenom.put(denom, available - used);
                        remaining -= used * denom;
                    }
                }
            }
        }
    }

    public int getReceiptPaper() {
        return receiptPaper;
    }

    public int getInk() {
        return ink;
    }

    public void refillPaper(int amount) {
        receiptPaper += amount;
    }

    public void refillInk(int amount) {
        ink += amount;
    }

    public void useReceiptPaper() {
        receiptPaper = Math.max(0, receiptPaper - 1);
    }

    public void useInk() {
        ink = Math.max(0, ink - 1);
    }

    public int getHardwareVersion() {
        return hardwareVersion;
    }

    public int getFirmwareVersion() {
        return firmwareVersion;
    }

    public void upgradeHardware() {
        hardwareVersion += 1;
    }

    public void upgradeFirmware() {
        firmwareVersion += 1;
    }
}
