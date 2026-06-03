package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

            Toast.makeText(
                    this,
                    "Enter all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        auth.signInWithEmailAndPassword(email, password)

                .addOnSuccessListener(authResult -> {

                    String uid = authResult.getUser().getUid();

                    db.collectionGroup("users")
                            .whereEqualTo("email", email)
                            .get()

                            .addOnSuccessListener(this::handleUserProfile)

                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            LoginActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );

                })

                .addOnFailureListener(e ->
                        Toast.makeText(
                                LoginActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void handleUserProfile(QuerySnapshot snapshot) {

        if (snapshot.isEmpty()) {

            Toast.makeText(
                    this,
                    "User profile not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String role = null;
        String businessId = null;

        for (com.google.firebase.firestore.DocumentSnapshot doc :
                snapshot.getDocuments()) {

            role = doc.getString("role");
            businessId = doc.getString("businessId");

            break;
        }

        if (businessId == null) {

            Toast.makeText(
                    this,
                    "Business ID missing",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        getSharedPreferences("APP", MODE_PRIVATE)
                .edit()
                .putString("businessId", businessId)
                .putString("role", role)
                .apply();

        if ("admin".equalsIgnoreCase(role)) {

            startActivity(
                    new Intent(
                            LoginActivity.this,
                            AdminDashboardActivity.class
                    )
            );

        } else {

            startActivity(
                    new Intent(
                            LoginActivity.this,
                            DashboardActivity.class
                    )
            );
        }

        finish();
    }
}