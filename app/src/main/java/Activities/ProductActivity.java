package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import Adapter.ProductAdapter;
import database.DatabaseHelper;
import models.Product;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerProducts;

    ProductAdapter adapter;

    ArrayList<Product> productList;

    DatabaseHelper db;

    SearchView searchView;

    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product);

        recyclerProducts = findViewById(R.id.recyclerProducts);

        searchView = findViewById(R.id.searchView);

        btnAdd = findViewById(R.id.btnAddProduct);

        String role = getIntent().getStringExtra("role");
        if ("Cashier".equals(role)) {

            btnAdd.setVisibility(View.GONE);
        }
        if ("Cashier".equals(role)) {

            btnAdd.setEnabled(false);
        }

        db = new DatabaseHelper(this);

        loadProducts();

        btnAdd.setOnClickListener(v -> {

            startActivity(
                    new Intent(this, AddProductActivity.class)
            );
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.filter(newText);

                return true;
            }
        });
    }

    // LOAD PRODUCTS FROM DATABASE
    private void loadProducts() {

        productList = db.getAllProducts();

        adapter = new ProductAdapter(this, productList);

        recyclerProducts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerProducts.setAdapter(adapter);
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadProducts();
    }
}