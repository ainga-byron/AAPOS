package Utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtil {

    public static String generateSalt() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            return Base64.encodeToString(salt, Base64.NO_WRAP);

        } catch (Exception e) {
            return null;
        }
    }

    // Hash password using SHA-256 + salt
    public static String hashPassword(String password, String salt) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            String combined = salt + password;

            byte[] hash = md.digest(combined.getBytes());

            return Base64.encodeToString(hash, Base64.NO_WRAP);

        } catch (Exception e) {
            return null;
        }
    }

}
