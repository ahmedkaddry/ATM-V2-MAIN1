package domain;

public class Client {

    private String username;
    private String pin;
    private double balance;
    private int failedAttempts;
    private boolean locked;

    public Client(String username, String pin, double balance,
                  int failedAttempts, boolean locked) {
        this.username = username;
        this.pin = pin;
        this.balance = balance;
        this.failedAttempts = failedAttempts;
        this.locked = locked;
    }

    public String getUsername() {
        return username;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    public void unlock() {
        this.locked = false;
    }
}
