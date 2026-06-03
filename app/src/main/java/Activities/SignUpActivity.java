package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etBusinessName;
    Button btnSignUp;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etBusinessName = findViewById(R.id.etBusinessName);

        btnSignUp = findViewById(R.id.btnSignUp);

        auth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String businessName = etBusinessName.getText().toString().trim();

        // VALIDATION
        if (businessName.isEmpty()) {
            etBusinessName.setError("Business name required");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Minimum 6 characters");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = auth.getCurrentUser();

                        if (user == null) return;

                        String uid = user.getUid();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // 🔥 CREATE BUSINESS ID
                        String businessId = db.collection("businesses")
                                .document()
                                .getId();

                        // 🔥 CREATE BUSINESS
                        Map<String, Object> business = new HashMap<>();
                        business.put("name", businessName);
                        business.put("ownerId", uid);
                        business.put("createdAt", System.currentTimeMillis());

                        db.collection("businesses")
                                .document(businessId)
                                .set(business);

                        // 🔥 CREATE ADMIN USER INSIDE BUSINESS
                        Map<String, Object> admin = new HashMap<>();
                        admin.put("email", email);
                        admin.put("role", "admin");
                        admin.put("businessId", businessId);

                        db.collection("businesses")
                                .document(businessId)
                                .collection("users")
                                .document(uid)
                                .set(admin);

                        // 🔥 STORE LOCALLY
                        getSharedPreferences("APP", MODE_PRIVATE)
                                .edit()
                                .putString("businessId", businessId)
                                .putString("role", "admin")
                                .apply();

                        // 🔥 EMAIL VERIFICATION
                        user.sendEmailVerification()
                                .addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {

                                        Toast.makeText(
                                                this,
                                                "Account created. Verify your email.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        auth.signOut();

                                        startActivity(new Intent(
                                                SignUpActivity.this,
                                                LoginActivity.class
                                        ));

                                        finish();

                                    } else {

                                        Toast.makeText(
                                                this,
                                                "Verification email failed",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });

                    } else {

                        Toast.makeText(
                                this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}