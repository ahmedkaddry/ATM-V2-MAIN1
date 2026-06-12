package domain;

import java.time.LocalDateTime;

public class Movement {

    private final String username;
    private final int amount;
    private final MovementKind type;
    private final boolean receiptPrinted;
    private final LocalDateTime time;

    public Movement(String username, int amount, MovementKind type,
                  boolean receiptPrinted, LocalDateTime time) {
        this.username = username;
        this.amount = amount;
        this.type = type;
        this.receiptPrinted = receiptPrinted;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public int getAmount() {
        return amount;
    }

    public MovementKind getType() {
        return type;
    }

    public boolean isReceiptPrinted() {
        return receiptPrinted;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
