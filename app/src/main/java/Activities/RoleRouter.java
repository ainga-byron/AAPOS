package Activities;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RoleRouter {

    public static void routeUser(Context context) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(context,
                                "User profile missing",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String role = doc.getString("role");

                    if ("Admin".equals(role)) {

                        context.startActivity(
                                new Intent(context,
                                        AdminDashboardActivity.class));

                    } else if ("Cashier".equals(role)) {

                        context.startActivity(
                                new Intent(context,
                                        DashboardActivity.class));

                    } else {

                        Toast.makeText(context,
                                "Invalid role",
                                Toast.LENGTH_LONG).show();

                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}