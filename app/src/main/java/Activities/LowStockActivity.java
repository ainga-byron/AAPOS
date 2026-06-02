package Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import Adapter.ProductAdapter;
import database.DatabaseHelper;
import models.Product;

public class LowStockActivity extends AppCompatActivity {

    RecyclerView recyclerLowStock;

    ProductAdapter adapter;

    DatabaseHelper db;

    ArrayList<Product> lowStockList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_low_stock);

        recyclerLowStock =
                findViewById(R.id.recyclerLowStock);

        db = new DatabaseHelper(this);

        lowStockList = db.getLowStockProducts();

        adapter = new ProductAdapter(this, lowStockList);

        recyclerLowStock.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerLowStock.setAdapter(adapter);
    }
}