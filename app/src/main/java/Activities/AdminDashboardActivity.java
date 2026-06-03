package Activities;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.a10.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity
        extends AppCompatActivity {

    CardView cardProducts,
            cardSales,
            cardReports,
            cardUsers,
            cardExpenses,
            cardLogout;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_administrator_dashboard);

        cardProducts =
                findViewById(R.id.cardProducts);

        cardSales =
                findViewById(R.id.cardSales);

        cardReports =
                findViewById(R.id.cardReports);

        cardUsers =
                findViewById(R.id.cardUsers);

        cardExpenses =
                findViewById(R.id.cardExpenses);

        cardLogout =
                findViewById(R.id.cardLogout);
        role = getIntent().getStringExtra("role");

        cardProducts.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, ProductActivity.class)
            );
        });

        cardSales.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, SalesReportActivity.class)
            );
        });
        cardReports.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, ReportActivity.class)
            );
        });
        cardExpenses.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, ExpenseReportActivity.class)
            );
        });
        cardUsers.setOnClickListener(v -> {
            startActivity(
                    new Intent(this, AdminUsersActivity.class)
            );
        });

        cardLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            startActivity(
                    new Intent(this,
                            LoginActivity.class));

            finish();
        });
    }
}