package Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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

    double totalSales = 0;
    double totalExpenses = 0;

    LineChart lineChart;

    // ✅ FIXED: correct chart data lists
    List<Entry> salesEntries = new ArrayList<>();
    List<Entry> expenseEntries = new ArrayList<>();
    List<Entry> profitEntries = new ArrayList<>();

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

        applyFilter("daily");

        btnDaily.setOnClickListener(v -> applyFilter("daily"));
        btnWeekly.setOnClickListener(v -> applyFilter("weekly"));
        btnMonthly.setOnClickListener(v -> applyFilter("monthly"));
    }

    // FILTER
    private void applyFilter(String type) {

        long now = System.currentTimeMillis();
        long startTime = 0;

        switch (type) {

            case "daily":
                startTime = now - (24 * 60 * 60 * 1000);
                break;

            case "weekly":
                startTime = now - (7L * 24 * 60 * 60 * 1000);
                break;

            case "monthly":
                startTime = now - (30L * 24 * 60 * 60 * 1000);
                break;
        }

        loadSales(startTime);
        loadExpenses(startTime);
    }

    // SALES
    private void loadSales(long startTime) {

        db.collection("sales")
                .whereGreaterThan("timestamp", startTime)
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalSales = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Double amount = doc.getDouble("totalAmount");

                        if (amount != null) {
                            totalSales += amount;
                        }
                    }

                    tvSales.setText("Sales: KES " + totalSales);

                    updateProfit();
                    updateChart(); // ✅ IMPORTANT FIX
                });
    }

    // EXPENSES
    private void loadExpenses(long startTime) {

        db.collection("expenses")
                .whereGreaterThan("timestamp", startTime)
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalExpenses = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Double amount = doc.getDouble("amount");

                        if (amount != null) {
                            totalExpenses += amount;
                        }
                    }

                    tvExpenses.setText("Expenses: KES " + totalExpenses);

                    updateProfit();
                    updateChart(); // ✅ IMPORTANT FIX
                });
    }

    // PROFIT
    private void updateProfit() {

        double profit = totalSales - totalExpenses;

        tvProfit.setText("Profit: KES " + profit);
    }

    // CHART
    private void updateChart() {

        salesEntries.clear();
        expenseEntries.clear();
        profitEntries.clear();

        salesEntries.add(new Entry(0, (float) totalSales));
        expenseEntries.add(new Entry(1, (float) totalExpenses));
        profitEntries.add(new Entry(2, (float) (totalSales - totalExpenses)));

        LineDataSet salesSet = new LineDataSet(salesEntries, "Sales");
        salesSet.setColor(android.graphics.Color.GREEN);

        LineDataSet expenseSet = new LineDataSet(expenseEntries, "Expenses");
        expenseSet.setColor(android.graphics.Color.RED);

        LineDataSet profitSet = new LineDataSet(profitEntries, "Profit");
        profitSet.setColor(android.graphics.Color.BLUE);

        LineData data = new LineData(salesSet, expenseSet, profitSet);

        lineChart.setData(data);
        lineChart.invalidate();
    }
}