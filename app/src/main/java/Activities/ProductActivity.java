package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

import Adapter.ProductAdapter;
import models.Product;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerProducts;
    ProductAdapter adapter;
    ArrayList<Product> productList;

    FirebaseFirestore db;

    SearchView searchView;
    Button btnAdd;
    TextView tvStockWorth;

    String businessId;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recyclerProducts = findViewById(R.id.recyclerProducts);
        searchView = findViewById(R.id.searchView);
        btnAdd = findViewById(R.id.btnAddProduct);
        tvStockWorth = findViewById(R.id.tvStockWorth);

        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();

        role = getIntent().getStringExtra("role");

        if ("Cashier".equalsIgnoreCase(role)) {
            btnAdd.setVisibility(View.GONE);
        }

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", null);

        if (businessId == null || businessId.isEmpty()) {
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

                    if (e != null || snapshot == null) {
                        Toast.makeText(this,
                                "Error loading products",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<Product> tempList = new ArrayList<>();
                    double totalStockWorth = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {

                        Product p = new Product();
                        p.setProductId(doc.getId());

                        String name = doc.getString("productName");
                        p.setProductName(name != null ? name : "Unknown");

                        String category = doc.getString("category");
                        p.setCategory(category != null ? category : "Uncategorized");

                        int stock = doc.getLong("stock") != null
                                ? doc.getLong("stock").intValue()
                                : 0;

                        double sellingPrice = doc.getDouble("sellingPrice") != null
                                ? doc.getDouble("sellingPrice")
                                : 0;

                        double buyingPrice = doc.getDouble("buyingPrice") != null
                                ? doc.getDouble("buyingPrice")
                                : 0;

                        p.setStock(stock);
                        p.setSellingPrice(sellingPrice);
                        p.setBuyingPrice(buyingPrice);

                        tempList.add(p);

                        totalStockWorth += stock * sellingPrice;
                    }

                    tvStockWorth.setText(
                            "Total Stock Worth: KES " +
                                    String.format(Locale.getDefault(), "%,.2f", totalStockWorth)
                    );

                    productList.clear();
                    productList.addAll(tempList);

                    if (adapter == null) {

                        adapter = new ProductAdapter(this, productList, businessId);

                        recyclerProducts.setLayoutManager(
                                new LinearLayoutManager(this)
                        );

                        recyclerProducts.setAdapter(adapter);

                    } else {
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}