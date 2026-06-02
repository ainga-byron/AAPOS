package models;

public class Product {

    private int id;
    private String name;
    private String category;
    private double buyingPrice;
    private double sellingPrice;
    private int stock;

    // EMPTY CONSTRUCTOR
    public Product() {}

    // FULL CONSTRUCTOR
    public Product(int id, String name, String category,
                   double buyingPrice, double sellingPrice,
                   int stock) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getStock() {
        return stock;
    }

    // SETTERS
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}