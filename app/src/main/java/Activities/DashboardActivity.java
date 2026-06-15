package Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;

import Adapter.ProductAdapter;
import models.Cart;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    TextView tvUsername, tvCartCount;
    ImageView settingsIcon;

    View sales, product, report, expenses;

    Button btnLogOut, btnLowStock, btnExpense;

    LinearLayout cardCart;

    String role;
    String businessId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // INIT VIEWS
        tvUsername = findViewById(R.id.tvUsername);
        settingsIcon = findViewById(R.id.imgSettings);

        sales = findViewById(R.id.cardSales);
        product = findViewById(R.id.cardProducts);
        report = findViewById(R.id.cardReports);
        expenses = findViewById(R.id.cardExpenses);

        btnLogOut = findViewById(R.id.btnLogout);
        btnLowStock = findViewById(R.id.btnLowStock);
        btnExpense = findViewById(R.id.btnExpense);

        tvCartCount = findViewById(R.id.tvCartCount);
        cardCart = findViewById(R.id.cardCart);

        // DATA
        role = getIntent().getStringExtra("role");
        String username = getIntent().getStringExtra("username");

        tvUsername.setText(username != null ? username : "User");

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", null);

        if (businessId == null) {
            Toast.makeText(this, "Business not found", Toast.LENGTH_SHORT).show();
        }

        // CASHIER RESTRICTION
        if ("Cashier".equals(role)) {
            report.setVisibility(View.GONE);
            expenses.setVisibility(View.GONE);
        }

        // =========================
        // CLICK EVENTS (ALL CARDS)
        // =========================

        sales.setOnClickListener(v ->
                startActivity(new Intent(this, SalesReportActivity.class))
        );

        product.setOnClickListener(v ->
                startActivity(new Intent(this, ProductActivity.class))
        );

        report.setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class))
        );

        expenses.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class))
        );

        cardCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class))
        );

        btnLowStock.setOnClickListener(v ->
                startActivity(new Intent(this, LowStockActivity.class))
        );

        btnLogOut.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        settingsIcon.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );
        btnExpense.setOnClickListener(v ->{
            startActivity(new Intent(this, ExpensesReport.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }

    // SAFE CART COUNT (NO CRASH)
    private void updateCartCount() {

        ArrayList<Cart> cart;

        if (ProductAdapter.cartList == null) {
            cart = new ArrayList<>();
        } else {
            cart = ProductAdapter.cartList;
        }

        tvCartCount.setText(String.valueOf(cart.size()));
    }
}