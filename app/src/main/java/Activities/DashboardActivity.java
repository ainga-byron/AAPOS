package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.a10.R;

import Adapter.ProductAdapter;

public class DashboardActivity extends AppCompatActivity {

    TextView tvUsername, tvCartCount;
    ImageView settingsIcon;

    CardView sales, product, report, expenses;
    Button btnLogOut, btnLowStock;

    String role;
    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvUsername = findViewById(R.id.tvUsername);
        settingsIcon = findViewById(R.id.imgSettings);

        sales = findViewById(R.id.cardSales);
        product = findViewById(R.id.cardProducts);
        report = findViewById(R.id.cardReports);
        expenses = findViewById(R.id.cardExpenses);

        btnLogOut = findViewById(R.id.btnLogout);
        btnLowStock = findViewById(R.id.btnLowStock);

        tvCartCount = findViewById(R.id.tvCartCount);

        LinearLayout cardCart = findViewById(R.id.cardCart);

        // ✅ FIX: get role properly
        role = getIntent().getStringExtra("role");
        String username = getIntent().getStringExtra("username");

        tvUsername.setText(username != null ? username : "User");

        // ✅ BUSINESS ID (important for Firestore system)
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

        // NAVIGATION
        sales.setOnClickListener(v ->
                startActivity(new Intent(this, SalesReportActivity.class))
        );

        product.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
        });

        report.setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class))
        );

        expenses.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class))
        );

        btnLowStock.setOnClickListener(v ->
                startActivity(new Intent(this, LowStockActivity.class))
        );

        cardCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class))
        );

        btnLogOut.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        settingsIcon.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }

    private void updateCartCount() {
        int count = ProductAdapter.cartList.size();
        tvCartCount.setText(String.valueOf(count));
    }
}