import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    public Typeface getAppFont() {

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String fontName = prefs.getString("font", "Default");

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