package Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.a10.R;

public class SettingsActivity extends AppCompatActivity {

    EditText etUsername, etOldPassword, etNewPassword;
    Switch switchDarkMode;
    Button btnSaveUsername, btnChangePassword;

    TextView tvUsername;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // APPLY THEME BEFORE UI LOADS
        prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        applyDarkMode(prefs.getBoolean("darkMode", false));

        setContentView(R.layout.activity_settings);

        // INIT VIEWS
        tvUsername = findViewById(R.id.tvUsername);
        etUsername = findViewById(R.id.etUsername);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnSaveUsername = findViewById(R.id.btnSaveUsername);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        loadSettings();

        // SAVE USERNAME
        btnSaveUsername.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();

            prefs.edit().putString("username", username).apply();

            tvUsername.setText(username);

            Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show();
        });

        // PASSWORD UPDATE
        btnChangePassword.setOnClickListener(v -> {

            String oldPass = etOldPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();

            String savedPassword = prefs.getString("password", "");

            if (savedPassword.isEmpty()) {
                Toast.makeText(this, "No password set yet", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!oldPass.equals(savedPassword)) {
                Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 4) {
                Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit().putString("password", newPass).apply();

            etOldPassword.setText("");
            etNewPassword.setText("");

            Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
        });

        // DARK MODE SWITCH
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

            prefs.edit().putBoolean("darkMode", isChecked).apply();

            applyDarkMode(isChecked);
        });
    }

    // LOAD SAVED SETTINGS
    private void loadSettings() {

        String username = prefs.getString("username", "User");
        tvUsername.setText(username);
        etUsername.setText(username);

        boolean darkMode = prefs.getBoolean("darkMode", false);
        switchDarkMode.setChecked(darkMode);

        // ensure password exists
        if (!prefs.contains("password")) {
            prefs.edit().putString("password", "1234").apply();
        }
    }

    // APPLY DARK MODE GLOBALLY
    private void applyDarkMode(boolean enabled) {

        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}