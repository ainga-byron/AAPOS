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

import java.util.ArrayList;

import database.DatabaseHelper;
import models.Cart;
import models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> productList;
    ArrayList<Product> productListFull;

    public static ArrayList<Cart> cartList = new ArrayList<>();

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
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

        holder.tvProductName.setText(product.getName());
        holder.tvCategory.setText("Category: " + product.getCategory());
        holder.tvBuyingPrice.setText("Buying: KES " + product.getBuyingPrice());
        holder.tvSellingPrice.setText("Selling: KES " + product.getSellingPrice());
        holder.tvStock.setText("Stock: " + product.getStock());

        // ADD TO CART
        holder.btnAddToCart.setOnClickListener(v -> {

            boolean exists = false;

            for (Cart item : cartList) {

                if (item.getProductId() == product.getId()) {
                    item.setQuantity(item.getQuantity() + 1);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                cartList.add(new Cart(
                        product.getId(),
                        product.getName(),
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

        // UPDATE BUTTON
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

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    categories
            );

            spinnerCategory.setAdapter(adapter);

            // SET VALUES
            etName.setText(product.getName());
            etBuying.setText(String.valueOf(product.getBuyingPrice()));
            etSelling.setText(String.valueOf(product.getSellingPrice()));
            etStock.setText(String.valueOf(product.getStock()));

            int pos = java.util.Arrays.asList(categories)
                    .indexOf(product.getCategory());

            if (pos >= 0) spinnerCategory.setSelection(pos);

            builder.setView(view);
            AlertDialog dialog = builder.create();

            btnSave.setOnClickListener(v1 -> {

                String name = etName.getText().toString().trim();
                String category = spinnerCategory.getSelectedItem().toString();

                double buying = Double.parseDouble(etBuying.getText().toString());
                double selling = Double.parseDouble(etSelling.getText().toString());
                int stock = Integer.parseInt(etStock.getText().toString());

                DatabaseHelper db = new DatabaseHelper(context);

                boolean updated = db.updateProduct(
                        product.getId(),
                        name,
                        category,
                        buying,
                        selling,
                        stock
                );

                if (updated) {

                    product.setName(name);
                    product.setCategory(category);
                    product.setBuyingPrice(buying);
                    product.setSellingPrice(selling);
                    product.setStock(stock);

                    notifyDataSetChanged();

                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                } else {
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filter(String text) {

        productList.clear();

        if (text == null || text.isEmpty()) {
            productList.addAll(productListFull);
        } else {

            String query = text.toLowerCase().trim();

            for (Product product : productListFull) {

                if (product.getName().toLowerCase().contains(query)
                        || product.getCategory().toLowerCase().contains(query)) {
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