package Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import Adapter.ExpenseAdapter;
import database.DatabaseHelper;
import models.Expense;

public class ExpenseReportActivity extends AppCompatActivity {

    TextView tvTotalExpenses;
    RecyclerView recyclerExpenses;

    DatabaseHelper db;
    ArrayList<Expense> expenseList;
    ExpenseAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        recyclerExpenses = findViewById(R.id.recyclerExpenses);

        db = new DatabaseHelper(this);

        recyclerExpenses.setLayoutManager(new LinearLayoutManager(this));

        loadExpenses();
    }

    private void loadExpenses() {

        expenseList = db.getAllExpenses();

        if (expenseList == null) {
            expenseList = new ArrayList<>();
        }

        adapter = new ExpenseAdapter(this, expenseList);
        recyclerExpenses.setAdapter(adapter);

        double total = calculateTotal();

        tvTotalExpenses.setText("Total Expenses: KES " + total);
    }

    private double calculateTotal() {

        double total = 0;

        for (Expense e : expenseList) {
            total += e.getAmount();
        }

        return total;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }
}