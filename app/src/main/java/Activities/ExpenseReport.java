package Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Adapter.ExpenseAdapter;
import models.Expense;

public class ExpenseReport extends AppCompatActivity {

    TextView tvTotalExpenses;
    RecyclerView recyclerExpenses;

    Button btnDaily, btnWeekly, btnMonthly;

    FirebaseFirestore db;

    ArrayList<Expense> list;
    ExpenseAdapter adapter;

    String businessId;

    double totalExpenses = 0;
    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        recyclerExpenses = findViewById(R.id.recyclerExpenses);

        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);

        db = FirebaseFirestore.getInstance();

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", "");

        if (businessId == null || businessId.isEmpty()) {
            Toast.makeText(this, "Business ID missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));

        // 🔥 CRITICAL FIX: initialize list here
        list = new ArrayList<>();

        btnDaily.setOnClickListener(v -> setFilter(1));
        btnWeekly.setOnClickListener(v -> setFilter(7));
        btnMonthly.setOnClickListener(v -> setFilter(30));

        setFilter(7);
    }

    // ================= FILTER =================
    private void setFilter(int days) {
        long now = System.currentTimeMillis();
        startTime = now - (days * 24L * 60 * 60 * 1000);
        loadExpenses();
    }

    // ================= LOAD =================
    private void loadExpenses() {

        db.collection("businesses")
                .document(businessId)
                .collection("expenses")
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (list == null) list = new ArrayList<>();
                    list.clear();

                    totalExpenses = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        long ts = parseLong(doc.get("timestamp"));

                        if (ts < startTime) continue;

                        Expense ex = new Expense();

                        ex.setTitle(doc.getString("reason"));
                        ex.setCategory(doc.getString("category"));
                        ex.setDateTime(doc.getString("dateTime"));

                        double amount = parseDouble(doc.get("amount"));
                        ex.setAmount(amount);

                        totalExpenses += amount;

                        list.add(ex);
                    }

                    tvTotalExpenses.setText("Total Expenses: KES " + totalExpenses);

                    if (adapter == null) {
                        adapter = new ExpenseAdapter(this, list);
                        recyclerExpenses.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // ================= SAFE PARSERS =================
    private double parseDouble(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Long) return ((Long) obj).doubleValue();

        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseLong(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Double) return ((Double) obj).longValue();

        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return 0;
        }
    }
}