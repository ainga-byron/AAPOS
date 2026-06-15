package Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a10.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {


    EditText etName,
            etCategory,
            etBuyingPrice,
            etSellingPrice,
            etStock;

    Button btnSaveProduct;

    FirebaseFirestore db;

    String businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etBuyingPrice = findViewById(R.id.etBuyingPrice);
        etSellingPrice = findViewById(R.id.etSellingPrice);
        etStock = findViewById(R.id.etStock);

        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        db = FirebaseFirestore.getInstance();

        businessId = getSharedPreferences("APP", MODE_PRIVATE)
                .getString("businessId", null);

        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {

        String name = etName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String buyingPriceText = etBuyingPrice.getText().toString().trim();
        String sellingPriceText = etSellingPrice.getText().toString().trim();
        String stockText = etStock.getText().toString().trim();

        if (name.isEmpty() ||
                category.isEmpty() ||
                buyingPriceText.isEmpty() ||
                sellingPriceText.isEmpty() ||
                stockText.isEmpty()) {

            Toast.makeText(this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (businessId == null) {
            Toast.makeText(this,
                    "Business not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double buyingPrice = Double.parseDouble(buyingPriceText);
        double sellingPrice = Double.parseDouble(sellingPriceText);
        int stock = Integer.parseInt(stockText);

        Map<String, Object> product = new HashMap<>();

        product.put("productName", name);   // ONLY THIS
        product.put("category", category);
        product.put("buyingPrice", buyingPrice);
        product.put("sellingPrice", sellingPrice);
        product.put("stock", stock);
        product.put("timestamp", System.currentTimeMillis());

        db.collection("businesses")
                .document(businessId)
                .collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(this,
                            "Product Saved",
                            Toast.LENGTH_SHORT).show();

                    clearFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }

    private void clearFields() {

        etName.setText("");
        etCategory.setText("");
        etBuyingPrice.setText("");
        etSellingPrice.setText("");
        etStock.setText("");
    }


}
