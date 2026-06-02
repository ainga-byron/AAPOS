import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class fontHelper {

    public static Typeface getFont(Context context, String fontName) {
        switch (fontName) {

            case "Roboto":
                return Typeface.create("sans-serif", Typeface.NORMAL);

            case "Roboto Condensed":
                return Typeface.create("sans-serif-condensed", Typeface.NORMAL);

            case "Open Sans":
                return Typeface.create("sans-serif", Typeface.NORMAL);

            case "Monospace":
                return Typeface.MONOSPACE;

            case "Serif":
                return Typeface.SERIF;

            case "Courier New":
                return Typeface.create("monospace", Typeface.NORMAL);

            default:
                return Typeface.DEFAULT;
        }
    }

    public static void applyFontToTextView(TextView tv, Typeface tf) {
        tv.setTypeface(tf);
    }
}