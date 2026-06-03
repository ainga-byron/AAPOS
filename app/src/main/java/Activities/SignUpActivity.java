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
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etBusinessName = findViewById(R.id.etBusinessName);
        btnSignUp = findViewById(R.id.btnSignUp);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String businessName = etBusinessName.getText().toString().trim();

        if (businessName.isEmpty()) {
            etBusinessName.setError("Business name required");
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Min 6 characters");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) return;

                    String uid = user.getUid();

                    // CREATE BUSINESS ID = SAME AS UID LOGIC SAFER
                    String businessId = db.collection("businesses").document().getId();

                    Map<String, Object> business = new HashMap<>();
                    business.put("name", businessName);
                    business.put("ownerUid", uid);
                    business.put("email", email);
                    business.put("createdAt", System.currentTimeMillis());

                    db.collection("businesses")
                            .document(businessId)
                            .set(business)
                            .addOnSuccessListener(unused -> {

                                // SAVE LOCALLY
                                getSharedPreferences("APP", MODE_PRIVATE)
                                        .edit()
                                        .putString("businessId", businessId)
                                        .putString("role", "admin")
                                        .apply();

                                user.sendEmailVerification();

                                Toast.makeText(this,
                                        "Account created. Please verify email.",
                                        Toast.LENGTH_LONG).show();

                                auth.signOut();

                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}