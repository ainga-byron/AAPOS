package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

import models.Cart;
import models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> productList;
    ArrayList<Product> productListFull;

    public static ArrayList<Cart> cartList;

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String businessId;

    public ProductAdapter(Context context, ArrayList<Product> productList, String businessId) {
        this.context = context;
        this.productList = productList;
        this.productListFull = productList; // IMPORTANT FIX
        this.businessId = businessId;

        if (cartList == null) {
            cartList = new ArrayList<>();
        }
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

        // SAFE NAME FIX (prevents blank/unknown)
        holder.tvProductName.setText(
                product.getProductName() != null && !product.getProductName().isEmpty()
                        ? product.getProductName()
                        : "Unknown Product"
        );

        holder.tvCategory.setText(
                "Category: " + (product.getCategory() != null ? product.getCategory() : "-")
        );

        holder.tvSellingPrice.setText("Selling: KES " + product.getSellingPrice());
        holder.tvStock.setText("Stock: " + product.getStock());

        // =========================
        // ADD TO CART
        // =========================
        holder.btnAddToCart.setOnClickListener(v -> {

            if (product.getProductId() == null) {
                Toast.makeText(context, "Invalid product", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean exists = false;

            for (Cart item : cartList) {
                if (item.getProductId().equals(product.getProductId())) {
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

            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        // =========================
        // EDIT BUTTON (POPUP)
        // =========================
        holder.btnEdit.setOnClickListener(v -> {
            showEditDialog(product, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // =========================
    // VIEW HOLDER
    // =========================
    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvCategory, tvSellingPrice, tvStock;
        Button btnAddToCart, btnEdit;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvSellingPrice = itemView.findViewById(R.id.tvSellingPrice);
            tvStock = itemView.findViewById(R.id.tvStock);

            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    // =========================
    // EDIT POPUP
    // =========================
    private void showEditDialog(Product product, int position) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_product, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etCategory = view.findViewById(R.id.etCategory);
        EditText etBuying = view.findViewById(R.id.etBuying);
        EditText etSelling = view.findViewById(R.id.etSelling);
        EditText etStock = view.findViewById(R.id.etStock);
        Button btnUpdate = view.findViewById(R.id.btnUpdate);

        // PRE-FILL DATA
        etName.setText(product.getProductName());
        etCategory.setText(product.getCategory());
        etBuying.setText(String.valueOf(product.getBuyingPrice()));
        etSelling.setText(String.valueOf(product.getSellingPrice()));
        etStock.setText(String.valueOf(product.getStock()));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        dialog.show();

        btnUpdate.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            double buying = Double.parseDouble(etBuying.getText().toString().trim());
            double selling = Double.parseDouble(etSelling.getText().toString().trim());
            int stock = Integer.parseInt(etStock.getText().toString().trim());

            firestore.collection("businesses")
                    .document(businessId)
                    .collection("products")
                    .document(product.getProductId())
                    .update(
                            "productName", name,
                            "category", category,
                            "buyingPrice", buying,
                            "sellingPrice", selling,
                            "stock", stock
                    )
                    .addOnSuccessListener(unused -> {

                        product.setProductName(name);
                        product.setCategory(category);
                        product.setBuyingPrice(buying);
                        product.setSellingPrice(selling);
                        product.setStock(stock);

                        notifyItemChanged(position);

                        Toast.makeText(context, "Product Updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }

    // =========================
    // FILTER
    // =========================
    public void filter(String text) {

        productList.clear();

        if (text == null || text.trim().isEmpty()) {
            productList.addAll(productListFull);
        } else {

            String query = text.toLowerCase(Locale.ROOT).trim();

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
}