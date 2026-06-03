package Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Adapter.ProductAdapter;
import models.Product;

public class LowStockActivity extends AppCompatActivity {

    RecyclerView recyclerLowStock;

    ProductAdapter adapter;
    ArrayList<Product> lowStockList;

    FirebaseFirestore db;
    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_stock);

        recyclerLowStock = findViewById(R.id.recyclerLowStock);

        db = FirebaseFirestore.getInstance();

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", null);

        if (businessId == null) {
            Toast.makeText(this, "Business not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadLowStock();
    }

    private void loadLowStock() {

        lowStockList = new ArrayList<>();

        db.collection("businesses")
                .document(businessId)
                .collection("products")
                .whereLessThanOrEqualTo("stock", 5)
                .addSnapshotListener((snapshot, e) -> {

                    if (e != null) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot == null) return;

                    lowStockList.clear();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Product p = new Product();

                        p.setProductId(doc.getId());
                        p.setProductName(
                                doc.getString("productName") != null
                                        ? doc.getString("productName")
                                        : "Unknown"
                        );

                        p.setCategory(
                                doc.getString("category") != null
                                        ? doc.getString("category")
                                        : "N/A"
                        );

                        Object stockObj = doc.get("stock");
                        int stock = (stockObj instanceof Long)
                                ? ((Long) stockObj).intValue()
                                : 0;

                        p.setStock(stock);

                        lowStockList.add(p);
                    }

                    if (adapter == null) {

                        adapter = new ProductAdapter(
                                this,
                                lowStockList,
                                businessId
                        );

                        recyclerLowStock.setLayoutManager(
                                new LinearLayoutManager(this)
                        );

                        recyclerLowStock.setAdapter(adapter);

                    } else {
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}