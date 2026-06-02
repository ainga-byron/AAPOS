package Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LiquorPOSSession";

    private static final String IS_LOGIN = "isLoggedIn";
    private static final String USERNAME = "username";
    private static final String ROLE = "role";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context context) {

        this.context = context;

        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        editor = pref.edit();
    }

    public void createLoginSession(String username, String role) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(USERNAME, username);

        editor.putString(ROLE, role);

        editor.apply();
    }
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public String getUsername() {
        return pref.getString(USERNAME, "");
    }

    public String getRole() {
        return pref.getString(ROLE, "");
    }

    public void logoutUser() {

        editor.clear();
        editor.apply();
    }

}
