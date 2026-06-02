package Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import Adapter.SalesAdapter;
import database.DatabaseHelper;
import models.Sale;

public class AddProductActivity extends AppCompatActivity {

    EditText etProductName,
            etCategory,
            etBuyingPrice,
            etSellingPrice,
            etStock;

    Button btnSaveProduct;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_product);

        etProductName =
                findViewById(R.id.etProductName);

        etCategory =
                findViewById(R.id.etCategory);

        etBuyingPrice =
                findViewById(R.id.etBuyingPrice);

        etSellingPrice =
                findViewById(R.id.etSellingPrice);

        etStock =
                findViewById(R.id.etStock);

        btnSaveProduct =
                findViewById(R.id.btnSaveProduct);

        db = new DatabaseHelper(this);

        btnSaveProduct.setOnClickListener(v -> {

            String name =
                    etProductName.getText().toString().trim();

            String category =
                    etCategory.getText().toString().trim();

            String buying =
                    etBuyingPrice.getText().toString().trim();

            String selling =
                    etSellingPrice.getText().toString().trim();

            String stockQty =
                    etStock.getText().toString().trim();

            if(name.isEmpty()
                    || category.isEmpty()
                    || buying.isEmpty()
                    || selling.isEmpty()
                    || stockQty.isEmpty()) {

                Toast.makeText(this,
                        "Fill all fields",
                        Toast.LENGTH_LONG).show();

                return;
            }

            boolean success = db.addProduct(
                    name,
                    category,
                    Double.parseDouble(buying),
                    Double.parseDouble(selling),
                    Integer.parseInt(stockQty)
            );

            if(success) {

                Toast.makeText(this,
                        "Product Added",
                        Toast.LENGTH_LONG).show();

                finish();

            } else {

                Toast.makeText(this,
                        "Failed",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class SalesReportActivity extends AppCompatActivity {

        RecyclerView recyclerSales;
        TextView tvTotalRevenue;

        DatabaseHelper db;
        ArrayList<Sale> salesList;
        SalesAdapter adapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sales);

            recyclerSales = findViewById(R.id.recyclerSales);


            db = new DatabaseHelper(this);

            salesList = db.getAllSales();

            adapter = new SalesAdapter(this, salesList);

            recyclerSales.setLayoutManager(
                    new LinearLayoutManager(this)
            );

            recyclerSales.setAdapter(adapter);

            calculateTotalRevenue();
        }

        private void calculateTotalRevenue() {

            double total = 0;

            for (Sale sale : salesList) {
                total += sale.getTotal();
            }

            tvTotalRevenue.setText("Total Revenue: KES " + total);
        }
    }
}