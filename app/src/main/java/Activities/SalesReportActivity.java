package Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import Adapter.SalesAdapter;
import models.Sale;

public class SalesReportActivity extends AppCompatActivity {

    TextView tvTotalSales;
    RecyclerView recyclerSales;
    Spinner spinnerFilter;

    FirebaseFirestore db;

    ArrayList<Sale> salesList;
    SalesAdapter adapter;

    String businessId;

    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        tvTotalSales = findViewById(R.id.tvTotalSales);
        recyclerSales = findViewById(R.id.recyclerSales);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        db = FirebaseFirestore.getInstance();

        salesList = new ArrayList<>();
        adapter = new SalesAdapter(this, salesList);

        recyclerSales.setLayoutManager(new LinearLayoutManager(this));
        recyclerSales.setAdapter(adapter);

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", null);

        if (businessId == null) {
            Toast.makeText(this, "Business not found", Toast.LENGTH_SHORT).show();
            return;
        }

        setupSpinner();

        loadSalesRealtime(0);
    }

    private void setupSpinner() {

        String[] filters = {"All Sales", "Daily", "Weekly", "Monthly"};

        ArrayAdapter<String> adapterSpinner =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        filters);

        adapterSpinner.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spinnerFilter.setAdapter(adapterSpinner);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                long now = System.currentTimeMillis();
                long startTime = 0;

                if (position == 1) {
                    startTime = now - (24L * 60 * 60 * 1000);
                } else if (position == 2) {
                    startTime = now - (7L * 24 * 60 * 60 * 1000);
                } else if (position == 3) {
                    startTime = now - (30L * 24 * 60 * 60 * 1000);
                }

                loadSalesRealtime(startTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // 🔥 REAL-TIME FIRESTORE LISTENER
    private void loadSalesRealtime(long startTime) {

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        Query query;

        if (startTime == 0) {
            query = db.collection("businesses")
                    .document(businessId)
                    .collection("sales");
        } else {
            query = db.collection("businesses")
                    .document(businessId)
                    .collection("sales")
                    .whereGreaterThan("timestamp", startTime);
        }

        listenerRegistration = query.addSnapshotListener((snapshot, error) -> {

            if (error != null) {
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshot == null) return;

            salesList.clear();

            double totalSales = 0;

            for (DocumentSnapshot doc : snapshot.getDocuments()) {

                Sale sale = new Sale();

                sale.setProductName(
                        doc.getString("productName") != null
                                ? doc.getString("productName")
                                : "Unknown"
                );

                // SAFE QUANTITY
                Object qtyObj = doc.get("quantity");
                int qty = (qtyObj instanceof Long)
                        ? ((Long) qtyObj).intValue()
                        : 0;

                sale.setQuantity(qty);

                // SAFE PRICE
                Object priceObj = doc.get("price");
                double price = (priceObj instanceof Double)
                        ? (Double) priceObj
                        : 0;

                sale.setPrice(price);

                // IMPORTANT FIX: your checkout uses totalAmount
                Object totalObj = doc.get("totalAmount");
                double total = (totalObj instanceof Double)
                        ? (Double) totalObj
                        : (totalObj instanceof Long)
                        ? ((Long) totalObj).doubleValue()
                        : 0;

                sale.setTotal(total);

                totalSales += total;

                sale.setDate(
                        doc.getString("dateTime") != null
                                ? doc.getString("dateTime")
                                : ""
                );

                salesList.add(sale);
            }

            adapter.notifyDataSetChanged();

            tvTotalSales.setText("Total Sales: KES " + totalSales);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}