package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = authResult.getUser().getUid();

                    // STEP 1: FIND USER IN ALL BUSINESSES
                    db.collectionGroup("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener(snapshot -> {

                                if (snapshot.isEmpty()) {
                                    Toast.makeText(this,
                                            "User not registered in any business",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }

                                DocumentSnapshot userDoc = snapshot.getDocuments().get(0);

                                String role = userDoc.getString("role");
                                String businessId = userDoc.getReference()
                                        .getParent()
                                        .getParent()
                                        .getId();

                                saveSession(businessId, role);

                                openDashboard(role);
                            });

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void saveSession(String businessId, String role) {
        getSharedPreferences("APP", MODE_PRIVATE)
                .edit()
                .putString("businessId", businessId)
                .putString("role", role)
                .apply();
    }

    private void openDashboard(String role) {

        if ("admin".equalsIgnoreCase(role)) {

            startActivity(new Intent(this, AdminDashboardActivity.class));

        } else if ("cashier".equalsIgnoreCase(role)) {

            startActivity(new Intent(this, DashboardActivity.class));

        } else {

            Toast.makeText(this,
                    "Unknown role: " + role,
                    Toast.LENGTH_LONG).show();
            return;
        }

        finish();
    }
}