package Activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import database.DatabaseHelper;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etTitle, etAmount;
    Spinner spinnerCategory;
    Button btnSave;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSaveExpense);

        db = new DatabaseHelper(this);

        // CATEGORIES
        String[] categories = {
                "Rent",
                "Electricity",
                "Transport",
                "Salary",
                "Internet",
                "Tax",
                "Stock Purchase",
                "Miscellaneous"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        spinnerCategory.setAdapter(adapter);

        // SAVE BUTTON
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {

        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        boolean inserted = db.addExpense(
                title,
                amount,
                date
        );

        if (inserted) {
            Toast.makeText(this, "Expense Saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
        }
    }
}