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

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;

    Button btnSignUp;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnSignUp = findViewById(R.id.btnSignUp);

        auth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String email =
                etEmail.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        if(email.isEmpty()) {

            etEmail.setError("Email required");
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Enter valid email");
            return;
        }

        if(password.length() < 6) {

            etPassword.setError("Minimum 6 characters");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        FirebaseUser user =
                                auth.getCurrentUser();

                        if(user != null) {

                            user.sendEmailVerification()
                                    .addOnCompleteListener(task1 -> {

                                        if(task1.isSuccessful()) {

                                            Toast.makeText(
                                                    this,
                                                    "Verification email sent",
                                                    Toast.LENGTH_LONG
                                            ).show();

                                            auth.signOut();

                                            startActivity(
                                                    new Intent(
                                                                SignUpActivity.this,
                                                            LoginActivity.class
                                                    )
                                            );

                                            finish();

                                        } else {

                                            Toast.makeText(
                                                    this,
                                                    "Failed to send verification email",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    });
                        }

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