package Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Adapter.CartAdapter;
import Adapter.ProductAdapter;
import database.DatabaseHelper;
import models.Cart;

public class CartActivity extends AppCompatActivity {

    TextView tvTotal, tvCartItems, tvCartTotal;
    RecyclerView recyclerCart;
    CartAdapter adapter;
    ArrayList<Cart> cartList;
    Button btnCheckout;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // INIT VIEWS
        tvTotal = findViewById(R.id.tvTotal);
        tvCartItems = findViewById(R.id.tvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        recyclerCart = findViewById(R.id.recyclerCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        db = new DatabaseHelper(this);

        recyclerCart.setLayoutManager(new LinearLayoutManager(this));

        // SAFE CART INIT
        if (ProductAdapter.cartList == null) {
            ProductAdapter.cartList = new ArrayList<>();
        }

        cartList = ProductAdapter.cartList;

        adapter = new CartAdapter(this, cartList);
        recyclerCart.setAdapter(adapter);

        updateCartUI();

        // CHECKOUT
        btnCheckout.setOnClickListener(v -> {

            if (cartList.isEmpty()) {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            double grandTotal = 0;

            String currentDateTime = new SimpleDateFormat(
                    "dd MMM yyyy HH:mm:ss",
                    Locale.getDefault()
            ).format(new Date());

            for (Cart item : cartList) {

                double total = item.getPrice() * item.getQuantity();
                grandTotal += total;

                // ✅ SAVE SALE (FIXED - includes productId)
                db.saveSale(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        total,
                        currentDateTime
                );

                // REDUCE STOCK
                db.reduceStock(
                        item.getProductId(),
                        item.getQuantity()
                );
            }

            // CLEAR CART
            cartList.clear();
            adapter.notifyDataSetChanged();

            updateCartUI();

            Toast.makeText(
                    this,
                    "Checkout Successful\nTotal: KES " + grandTotal,
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartUI();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private double calculateTotal() {
        double total = 0;

        for (Cart item : cartList) {
            total += item.getTotal();
        }

        return total;
    }

    private void updateCartUI() {

        double total = calculateTotal();
        int items = cartList.size();

        tvTotal.setText("Total: KES " + total);
        tvCartItems.setText("Items: " + items);
        tvCartTotal.setText("Total: KES " + total);
    }
}