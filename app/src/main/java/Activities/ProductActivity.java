package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
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

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerProducts;
    ProductAdapter adapter;
    ArrayList<Product> productList;

    FirebaseFirestore db;

    SearchView searchView;
    Button btnAdd;

    String businessId;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recyclerProducts = findViewById(R.id.recyclerProducts);
        searchView = findViewById(R.id.searchView);
        btnAdd = findViewById(R.id.btnAddProduct);

        db = FirebaseFirestore.getInstance();

        productList = new ArrayList<>();

        role = getIntent().getStringExtra("role");

        // CASHIER RESTRICTION
        if ("Cashier".equals(role)) {
            btnAdd.setVisibility(View.GONE);
        }

        // GET BUSINESS ID (VERY IMPORTANT)
        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", null);

        if (businessId == null) {
            Toast.makeText(this, "Business not found", Toast.LENGTH_SHORT).show();
            return;
        }

        loadProductsRealtime();

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class))
        );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return true;
            }
        });
    }

    private void loadProductsRealtime() {

        db.collection("businesses")
                .document(businessId)
                .collection("products")
                .addSnapshotListener((snapshot, e) -> {

                    if (e != null) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshot == null) return;

                    productList.clear();

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

                        Object priceObj = doc.get("sellingPrice");
                        double price = (priceObj instanceof Double)
                                ? (Double) priceObj
                                : 0;

                        p.setStock(stock);
                        p.setSellingPrice(price);

                        productList.add(p);
                    }

                    if (adapter == null) {

                        adapter = new ProductAdapter(
                                this,
                                productList,
                                businessId
                        );

                        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
                        recyclerProducts.setAdapter(adapter);

                    } else {
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}