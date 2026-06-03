package Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etReason, etCategory, etAmount;
    Button btnSaveExpense;

    FirebaseFirestore db;

    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etReason = findViewById(R.id.etReason);
        etCategory = findViewById(R.id.etCategory);
        etAmount = findViewById(R.id.etAmount);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);

        db = FirebaseFirestore.getInstance();

        // GET BUSINESS ID FROM LOGIN
        SharedPreferences prefs = getSharedPreferences("APP", MODE_PRIVATE);
        businessId = prefs.getString("businessId", "");

        btnSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {

        String reason = etReason.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (reason.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateTime = new SimpleDateFormat(
                "dd MMM yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());

        Map<String, Object> expense = new HashMap<>();
        expense.put("reason", reason);
        expense.put("category", category);
        expense.put("amount", amount);
        expense.put("timestamp", System.currentTimeMillis());
        expense.put("dateTime", dateTime);

        db.collection("businesses")
                .document(businessId)
                .collection("expenses")
                .add(expense)
                .addOnSuccessListener(doc -> {

                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show();

                    etReason.setText("");
                    etCategory.setText("");
                    etAmount.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}