package models;

public class Expense {

    private int id;
    private String title;

    private double amount;
    private String date;

    // EMPTY CONSTRUCTOR
    public Expense() {}

    // FULL CONSTRUCTOR
    public Expense(int id, String title, String category, double amount, String date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    // SETTERS
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }


}