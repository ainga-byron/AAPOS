package models;

public class ExpenseReport {

    private String reason;
    private String category;
    private double amount;
    private long timestamp;
    private String dateTime;

    public ExpenseReport() {}

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
}