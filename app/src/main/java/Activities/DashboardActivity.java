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
import database.DatabaseHelper;

public class DashboardActivity extends AppCompatActivity {
    TextView tvUsername;
    ImageView settingsIcon;
    CardView sales, product,report,expenses;
    Button btnLogOut, btnLowStock;
    TextView tvCartCount;
    String role;



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
        tvCartCount = findViewById(R.id.tvCartCount);
        btnLowStock = findViewById(R.id.btnLowStock);


        btnLowStock.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            LowStockActivity.class
                    )
            );
        });

        LinearLayout cardCart = findViewById(R.id.cardCart);

        cardCart.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    CartActivity.class
            );

            startActivity(intent);
        });
        DatabaseHelper db = new DatabaseHelper(this);

        int lowStockCount =
                db.getLowStockProducts().size();

        if (lowStockCount > 0) {

            Toast.makeText(
                    this,
                    "⚠ " + lowStockCount + " items low in stock!",
                    Toast.LENGTH_LONG
            ).show();
        }

        if ("Cashier".equals(role)) {

            report.setVisibility(View.GONE);
        }


        sales.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this,SalesReportActivity.class);
            startActivity(intent);
        });
        product.setOnClickListener(v ->{
            Intent intent = new Intent(this, ProductActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
        });
        report.setOnClickListener(v ->{
            Intent intent = new Intent(DashboardActivity.this, ExpenseReportActivity.class);
            startActivity(intent);
        });
        expenses.setOnClickListener(v ->{
            Intent intent = new Intent(DashboardActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });
        btnLogOut.setOnClickListener(v ->{
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });
        settingsIcon.setOnClickListener(v ->{
            Intent intent = new Intent(DashboardActivity.this,SettingsActivity.class);
            startActivity(intent);
        });
        String username =
                getIntent().getStringExtra("username");

        tvUsername.setText(username);
    }
    private void updateCartCount() {

        int count = ProductAdapter.cartList.size();

        tvCartCount.setText(String.valueOf(count));
    }
    @Override
    protected void onResume() {
        super.onResume();

        updateCartCount();
    }

}