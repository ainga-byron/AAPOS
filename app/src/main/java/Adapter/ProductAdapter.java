package Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import models.Cart;
import models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> productList;
    ArrayList<Product> productListFull;

    public static ArrayList<Cart> cartList = new ArrayList<>();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    String businessId;

    public ProductAdapter(Context context, ArrayList<Product> productList, String businessId) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
        this.businessId = businessId;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.product_item, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.tvProductName.setText(product.getProductName() != null ? product.getProductName() : "");
        holder.tvCategory.setText("Category: " + (product.getCategory() != null ? product.getCategory() : ""));
        holder.tvBuyingPrice.setText("Buying: KES " + product.getBuyingPrice());
        holder.tvSellingPrice.setText("Selling: KES " + product.getSellingPrice());
        holder.tvStock.setText("Stock: " + product.getStock());

        // ADD TO CART (SAFE)
        holder.btnAddToCart.setOnClickListener(v -> {

            if (product.getProductId() == null) {
                Toast.makeText(context, "Invalid product", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean exists = false;

            for (Cart item : cartList) {

                if (item.getProductId() != null &&
                        item.getProductId().equals(product.getProductId())) {

                    item.setQuantity(item.getQuantity() + 1);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                cartList.add(new Cart(
                        product.getProductId(),
                        product.getProductName(),
                        product.getSellingPrice(),
                        1
                ));
            }

            Toast.makeText(context, "Added To Cart", Toast.LENGTH_SHORT).show();
        });

        // STOCK COLOR
        if (product.getStock() <= 0) {
            holder.itemView.setBackgroundColor(0xFFFFCDD2);
        } else if (product.getStock() <= 5) {
            holder.itemView.setBackgroundColor(0xFFFFF59D);
        } else {
            holder.itemView.setBackgroundColor(0xFFFFFFFF);
        }

        // UPDATE PRODUCT
        holder.btnUpdate.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_edit_product, null);

            EditText etName = view.findViewById(R.id.etName);
            EditText etBuying = view.findViewById(R.id.etBuying);
            EditText etSelling = view.findViewById(R.id.etSelling);
            EditText etStock = view.findViewById(R.id.etStock);
            Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
            Button btnSave = view.findViewById(R.id.btnUpdate);

            String[] categories = {
                    "Beer", "Wine", "Whisky", "Vodka",
                    "Rum", "Soft Drinks", "Energy Drinks", "Snacks"
            };

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    categories
            );

            spinnerCategory.setAdapter(spinnerAdapter);

            etName.setText(product.getProductName());
            etBuying.setText(String.valueOf(product.getBuyingPrice()));
            etSelling.setText(String.valueOf(product.getSellingPrice()));
            etStock.setText(String.valueOf(product.getStock()));

            int pos = java.util.Arrays.asList(categories)
                    .indexOf(product.getCategory());

            if (pos >= 0) spinnerCategory.setSelection(pos);

            AlertDialog dialog = builder.setView(view).create();

            btnSave.setOnClickListener(v1 -> {

                firestore.collection("businesses")
                        .document(businessId)
                        .collection("products")
                        .document(product.getProductId())
                        .update(
                                "productName", etName.getText().toString(),
                                "category", spinnerCategory.getSelectedItem().toString(),
                                "buyingPrice", Double.parseDouble(etBuying.getText().toString()),
                                "sellingPrice", Double.parseDouble(etSelling.getText().toString()),
                                "stock", Integer.parseInt(etStock.getText().toString())
                        )
                        .addOnSuccessListener(unused -> {

                            // UPDATE LOCAL OBJECT (IMPORTANT FIX)
                            product.setProductName(etName.getText().toString());
                            product.setCategory(spinnerCategory.getSelectedItem().toString());
                            product.setBuyingPrice(Double.parseDouble(etBuying.getText().toString()));
                            product.setSellingPrice(Double.parseDouble(etSelling.getText().toString()));
                            product.setStock(Integer.parseInt(etStock.getText().toString()));

                            notifyDataSetChanged();

                            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                        );
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // FILTER FIXED
    public void filter(String text) {

        productList.clear();

        if (text == null || text.isEmpty()) {
            productList.addAll(productListFull);
        } else {

            String query = text.toLowerCase().trim();

            for (Product product : productListFull) {

                if ((product.getProductName() != null &&
                        product.getProductName().toLowerCase().contains(query))
                        ||
                        (product.getCategory() != null &&
                                product.getCategory().toLowerCase().contains(query))) {

                    productList.add(product);
                }
            }
        }

        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvCategory, tvBuyingPrice, tvSellingPrice, tvStock;
        Button btnAddToCart, btnUpdate;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvBuyingPrice = itemView.findViewById(R.id.tvBuyingPrice);
            tvSellingPrice = itemView.findViewById(R.id.tvSellingPrice);
            tvStock = itemView.findViewById(R.id.tvStock);

            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnUpdate = itemView.findViewById(R.id.btnEdit);
        }
    }
}