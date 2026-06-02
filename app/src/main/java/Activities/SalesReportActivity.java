package Activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import Adapter.SalesAdapter;
import database.DatabaseHelper;
import models.Sale;

public class SalesReportActivity extends AppCompatActivity {

    TextView tvTotalSales;
    RecyclerView recyclerSales;

    DatabaseHelper db;
    ArrayList<Sale> salesList;
    SalesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        tvTotalSales = findViewById(R.id.tvTotalSales);
        recyclerSales = findViewById(R.id.recyclerSales);

        db = new DatabaseHelper(this);

        recyclerSales.setLayoutManager(new LinearLayoutManager(this));

        loadSales();
    }

    private void loadSales() {

        salesList = db.getAllSales();

        adapter = new SalesAdapter(this, salesList);

        recyclerSales.setAdapter(adapter);

        double total = db.getTotalSales();

        tvTotalSales.setText("Total Sales: KES " + total);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSales();
    }
}