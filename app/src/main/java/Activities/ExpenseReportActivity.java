package Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Adapter.ExpenseAdapter;
import models.Expense;

public class ExpenseReportActivity extends AppCompatActivity {

    TextView tvTotalExpenses;
    RecyclerView recyclerExpenses;

    FirebaseFirestore db;

    ArrayList<Expense> expenseList;
    ExpenseAdapter adapter;

    String businessId = "YOUR_BUSINESS_ID"; // replace with session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        recyclerExpenses = findViewById(R.id.recyclerExpenses);

        db = FirebaseFirestore.getInstance();

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));

        loadExpenses();
    }

    private void loadExpenses() {

        expenseList = new ArrayList<>();

        db.collection("businesses")
                .document(businessId)
                .collection("expenses")
                .get()
                .addOnSuccessListener(snapshot -> {

                    double total = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Expense e = new Expense();

                        e.setExpenseId(doc.getId());
                        e.setTitle(doc.getString("title"));
                        e.setCategory(doc.getString("category"));

                        Object amountObj = doc.get("amount");
                        double amount = 0;

                        if (amountObj instanceof Long) {
                            amount = ((Long) amountObj).doubleValue();
                        } else if (amountObj instanceof Double) {
                            amount = (Double) amountObj;
                        } else if (amountObj instanceof String) {
                            try {
                                amount = Double.parseDouble((String) amountObj);
                            } catch (Exception ignored) {}
                        }

                        e.setAmount(amount);
                        total += amount;

                        e.setDateTime(doc.getString("dateTime"));

                        expenseList.add(e);
                    }

                    adapter = new ExpenseAdapter(this, expenseList);
                    recyclerExpenses.setAdapter(adapter);

                    tvTotalExpenses.setText("Total Expenses: KES " + total);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }
}