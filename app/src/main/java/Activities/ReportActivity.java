package Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {

    TextView tvSales, tvExpenses, tvProfit;
    Button btnDaily, btnWeekly, btnMonthly;

    FirebaseFirestore db;
    LineChart lineChart;

    double totalSales = 0;
    double totalExpenses = 0;

    long currentStartTime = 0;

    String businessId; // ✅ FIX HERE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tvSales = findViewById(R.id.tvSales);
        tvExpenses = findViewById(R.id.tvExpenses);
        tvProfit = findViewById(R.id.tvProfit);

        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);

        lineChart = findViewById(R.id.lineChart);

        db = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getSharedPreferences("APP", MODE_PRIVATE);
        businessId = prefs.getString("businessId", "");

        if (businessId == null || businessId.isEmpty()) {
            Toast.makeText(this, "Business ID missing", Toast.LENGTH_LONG).show();
            return;
        }

        btnDaily.setOnClickListener(v -> applyFilter("daily"));
        btnWeekly.setOnClickListener(v -> applyFilter("weekly"));
        btnMonthly.setOnClickListener(v -> applyFilter("monthly"));

        applyFilter("daily");
    }

    private void applyFilter(String type) {

        long now = System.currentTimeMillis();

        switch (type) {
            case "daily":
                currentStartTime = now - (24L * 60 * 60 * 1000);
                break;
            case "weekly":
                currentStartTime = now - (7L * 24 * 60 * 60 * 1000);
                break;
            case "monthly":
                currentStartTime = now - (30L * 24 * 60 * 60 * 1000);
                break;
        }

        loadSales();
        loadExpenses();
    }

    private void loadSales() {

        db.collection("businesses")
                .document(businessId)
                .collection("sales")
                .whereGreaterThan("timestamp", currentStartTime)
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalSales = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Object amountObj = doc.get("totalAmount");

                        double amount = 0;

                        if (amountObj instanceof Long) {
                            amount = ((Long) amountObj).doubleValue();
                        } else if (amountObj instanceof Double) {
                            amount = (Double) amountObj;
                        }

                        totalSales += amount;
                    }

                    tvSales.setText("Sales: KES " + totalSales);
                    updateUI();
                });
    }

    private void loadExpenses() {

        db.collection("businesses")
                .document(businessId)
                .collection("expenses")
                .whereGreaterThan("timestamp", currentStartTime)
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalExpenses = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Object amountObj = doc.get("amount");

                        double amount = 0;

                        if (amountObj instanceof Long) {
                            amount = ((Long) amountObj).doubleValue();
                        } else if (amountObj instanceof Double) {
                            amount = (Double) amountObj;
                        }

                        totalExpenses += amount;
                    }

                    tvExpenses.setText("Expenses: KES " + totalExpenses);
                    updateUI();
                });
    }

    private void updateUI() {

        double profit = totalSales - totalExpenses;
        tvProfit.setText("Profit: KES " + profit);

        drawChart();
    }

    private void drawChart() {

        List<Entry> salesEntries = new ArrayList<>();
        List<Entry> expenseEntries = new ArrayList<>();
        List<Entry> profitEntries = new ArrayList<>();

        salesEntries.add(new Entry(0, (float) totalSales));
        expenseEntries.add(new Entry(1, (float) totalExpenses));
        profitEntries.add(new Entry(2, (float) (totalSales - totalExpenses)));

        LineDataSet salesSet = new LineDataSet(salesEntries, "Sales");
        salesSet.setColor(Color.GREEN);

        LineDataSet expenseSet = new LineDataSet(expenseEntries, "Expenses");
        expenseSet.setColor(Color.RED);

        LineDataSet profitSet = new LineDataSet(profitEntries, "Profit");
        profitSet.setColor(Color.BLUE);

        LineData data = new LineData(salesSet, expenseSet, profitSet);

        lineChart.setData(data);
        lineChart.invalidate();
    }
}