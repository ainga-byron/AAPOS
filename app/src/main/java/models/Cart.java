package models;

public class Cart {

    int productId;

    String productName;

    double price;

    int quantity;

    public Cart(int productId,
                String productName,
                double price,
                int quantity) {

        this.productId = productId;

        this.productName = productName;

        this.price = price;

        this.quantity = quantity;
    }


    public int getProductId() {
        return productId;
    }


    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return price * quantity;
    }
}