package models;

public class Sale {

    private int id;
    private String productName;
    private int productId;
    private int quantity;
    private double price;
    private double total;
    private String date;

    // EMPTY CONSTRUCTOR
    public Sale() {}

    // FULL CONSTRUCTOR
    public Sale(int id, int productId, String productName,
                int quantity, double price, double total,
                String date) {

        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.date = date;
    }

    // GETTERS
    public int getId() { return id; }

    public int getProductId() { return productId; }

    public String getProductName() { return productName; }

    public int getQuantity() { return quantity; }

    public double getPrice() { return price; }

    public double getTotal() { return total; }

    public String getDate() { return date; }

    // SETTERS (🔥 THIS FIXES YOUR ERROR)
    public void setId(int id) { this.id = id; }

    public void setProductId(int productId) { this.productId = productId; }

    public void setProductName(String productName) { this.productName = productName; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setPrice(double price) { this.price = price; }

    public void setTotal(double total) { this.total = total; }

    public void setDate(String date) { this.date = date; }
}