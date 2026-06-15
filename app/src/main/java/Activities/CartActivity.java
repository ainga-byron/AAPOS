package Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Adapter.CartAdapter;
import Adapter.ProductAdapter;
import models.Cart;

public class CartActivity extends AppCompatActivity {

    TextView tvTotal, tvCartItems, tvCartTotal;
    RecyclerView recyclerCart;
    Button btnCheckout;

    CartAdapter adapter;
    ArrayList<Cart> cartList;

    FirebaseFirestore firestore;
    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        tvTotal = findViewById(R.id.tvTotal);
        tvCartItems = findViewById(R.id.tvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        recyclerCart = findViewById(R.id.recyclerCart);
        btnCheckout = findViewById(R.id.btnCheckout);

        firestore = FirebaseFirestore.getInstance();

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", null);

        if (businessId == null || businessId.isEmpty()) {
            Toast.makeText(this, "Business not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // SAFE CART INIT
        if (ProductAdapter.cartList == null) {
            ProductAdapter.cartList = new ArrayList<>();
        }

        cartList = ProductAdapter.cartList;

        recyclerCart.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CartAdapter(this, cartList, this::updateCartUI);
        recyclerCart.setAdapter(adapter);

        updateCartUI();

        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void checkout() {

        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        double grandTotal = 0;

        String currentDateTime =
                new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
                        .format(new Date());

        for (Cart item : cartList) {

            double total = item.getPrice() * item.getQuantity();
            grandTotal += total;

            // SALE RECORD
            Map<String, Object> sale = new HashMap<>();
            sale.put("productId", item.getProductId());
            sale.put("productName", item.getProductName());
            sale.put("quantity", item.getQuantity());
            sale.put("price", item.getPrice());
            sale.put("totalAmount", total);
            sale.put("timestamp", System.currentTimeMillis());
            sale.put("dateTime", currentDateTime);

            // IMPORTANT: store under business
            firestore.collection("businesses")
                    .document(businessId)
                    .collection("sales")
                    .add(sale);

            // STOCK UPDATE
            firestore.collection("businesses")
                    .document(businessId)
                    .collection("products")
                    .document(item.getProductId())
                    .update("stock", FieldValue.increment(-item.getQuantity()));
        }

        cartList.clear();
        adapter.notifyDataSetChanged();
        updateCartUI();

        Toast.makeText(
                this,
                "Checkout Successful\nTotal: KES " + grandTotal,
                Toast.LENGTH_LONG
        ).show();
    }

    private void updateCartUI() {

        if (cartList == null) return;

        double total = 0;

        for (Cart item : cartList) {
            total += item.getTotal();
        }

        tvTotal.setText("Total: KES " + total);
        tvCartItems.setText("Items: " + cartList.size());
        tvCartTotal.setText("Total: KES " + total);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCartUI();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}