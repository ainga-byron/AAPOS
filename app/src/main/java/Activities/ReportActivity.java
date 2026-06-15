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
    LineChart lineChart;

    FirebaseFirestore db;

    double totalSales = 0;
    double totalExpenses = 0;

    long startTime = 0;
    String businessId;

    boolean salesLoaded = false;
    boolean expensesLoaded = false;

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

        if (businessId.isEmpty()) {
            Toast.makeText(this, "Business ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        btnDaily.setOnClickListener(v -> loadReport(1));
        btnWeekly.setOnClickListener(v -> loadReport(7));
        btnMonthly.setOnClickListener(v -> loadReport(30));

        loadReport(1);
    }

    private void loadReport(int days) {

        long now = System.currentTimeMillis();
        startTime = now - (days * 24L * 60 * 60 * 1000);

        salesLoaded = false;
        expensesLoaded = false;

        loadSales();
        loadExpenses();
    }

    // ================= SALES =================
    private void loadSales() {

        db.collection("businesses")
                .document(businessId)
                .collection("sales")
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalSales = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        long ts = parseLong(doc.get("timestamp"));
                        if (ts == 0 || ts < startTime) continue;

                        totalSales += parseDouble(doc.get("totalAmount"));
                    }

                    tvSales.setText("Sales: KES " + totalSales);

                    salesLoaded = true;
                    checkUpdate();
                });
    }

    // ================= EXPENSES =================
    private void loadExpenses() {

        db.collection("businesses")
                .document(businessId)
                .collection("expenses")
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalExpenses = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        long ts = parseLong(doc.get("timestamp"));
                        if (ts == 0 || ts < startTime) continue;

                        totalExpenses += parseDouble(doc.get("amount"));
                    }

                    tvExpenses.setText("Expenses: KES " + totalExpenses);

                    expensesLoaded = true;
                    checkUpdate();
                });
    }

    private void checkUpdate() {
        if (salesLoaded && expensesLoaded) {
            updateUI();
        }
    }

    private void updateUI() {

        double profit = totalSales - totalExpenses;
        tvProfit.setText("Profit: KES " + profit);

        drawChart();
    }

    private void drawChart() {

        List<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0, (float) totalSales));
        entries.add(new Entry(1, (float) totalExpenses));
        entries.add(new Entry(2, (float) (totalSales - totalExpenses)));

        LineDataSet set = new LineDataSet(entries, "Report");
        set.setColor(Color.BLUE);
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setValueTextColor(Color.BLACK);

        LineData data = new LineData(set);

        lineChart.setData(data);
        lineChart.invalidate();
    }

    // ================= SAFE PARSERS =================
    private double parseDouble(Object obj) {

        if (obj == null) return 0;

        if (obj instanceof Long) return ((Long) obj).doubleValue();
        if (obj instanceof Double) return (Double) obj;

        if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }

    private long parseLong(Object obj) {

        if (obj == null) return 0;

        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Double) return ((Double) obj).longValue();

        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }
}