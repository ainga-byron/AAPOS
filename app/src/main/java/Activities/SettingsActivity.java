package Activities;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;

public class SettingsActivity extends AppCompatActivity {

    Spinner spinnerFont;
    Button btnApplyFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerFont = findViewById(R.id.spinnerFont);

        String[] fonts = {
                "Default",
                "Roboto Condensed",
                "Open Sans",
                "Monospace",
                "Serif",
                "Courier New"
        };

        SharedPreferences prefs =
                getSharedPreferences("settings", MODE_PRIVATE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                fonts
        );

        spinnerFont.setAdapter(adapter);
    }

    private Typeface getFont(String fontName) {

        switch (fontName) {

            case "Monospace":
                return Typeface.MONOSPACE;

            case "Serif":
                return Typeface.SERIF;

            case "Courier New":
                return Typeface.create("courier", Typeface.NORMAL);

            case "Roboto Condensed":
            case "Open Sans":
                return Typeface.SANS_SERIF;

            default:
                return Typeface.DEFAULT;
        }
    }
}