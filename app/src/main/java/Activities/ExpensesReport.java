package Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Adapter.Expenses;
import models.ExpenseReport;

public class ExpensesReport extends AppCompatActivity {

    RecyclerView recyclerView;
    Expenses adapter;
    ArrayList<ExpenseReport> list;

    FirebaseFirestore db;
    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_report);

        recyclerView = findViewById(R.id.recyclerExpenses);

        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", "");

        loadExpenses();
    }

    private void loadExpenses() {

        db.collection("businesses")
                .document(businessId)
                .collection("expenses")
                .orderBy("timestamp")
                .addSnapshotListener((snapshot, e) -> {

                    if (e != null || snapshot == null) {
                        Toast.makeText(this, "Error loading expenses", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    list.clear();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        ExpenseReport ex = new ExpenseReport();

                        ex.setReason(doc.getString("reason"));
                        ex.setCategory(doc.getString("category"));

                        Object amountObj = doc.get("amount");
                        double amount = 0;

                        if (amountObj instanceof Double) {
                            amount = (Double) amountObj;
                        } else if (amountObj instanceof Long) {
                            amount = ((Long) amountObj).doubleValue();
                        }

                        ex.setAmount(amount);

                        ex.setDateTime(doc.getString("dateTime"));

                        Long ts = doc.getLong("timestamp");
                        ex.setTimestamp(ts != null ? ts : 0);

                        list.add(ex);
                    }

                    if (adapter == null) {

                        adapter = new Expenses(this, list);

                        recyclerView.setLayoutManager(
                                new LinearLayoutManager(this)
                        );

                        recyclerView.setAdapter(adapter);

                    } else {
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}