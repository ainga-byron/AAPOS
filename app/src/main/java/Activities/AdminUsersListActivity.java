package Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import Adapter.UserAdapter;
import models.User;

public class AdminUsersListActivity extends AppCompatActivity {

    RecyclerView recyclerUsers;
    UserAdapter adapter;
    List<User> userList;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_item);

        recyclerUsers = findViewById(R.id.recyclerUsers);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        recyclerUsers.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadUsers();
    }

    private void loadUsers() {

        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    userList.clear();

                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        User user = doc.toObject(User.class);
                        userList.add(user);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}