package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import models.Cart;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Cart> cartList;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    OnCartChangeListener listener;

    public CartAdapter(Context context,
                       ArrayList<Cart> cartList,
                       OnCartChangeListener listener) {

        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.cart_item, parent, false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        Cart cart = cartList.get(position);

        holder.tvName.setText(cart.getProductName());

        holder.tvQty.setText("Qty: " + cart.getQuantity());

        // ✔ FIX 1: show unit price correctly
        holder.tvPrice.setText("Price: KES " + cart.getPrice());

        holder.tvTotal.setText("Total: KES " + cart.getTotal());

        // ➕ PLUS
        holder.btnPlus.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Cart item = cartList.get(pos);

            item.setQuantity(item.getQuantity() + 1);

            notifyItemChanged(pos);
            listener.onCartChanged();
        });

        // ➖ MINUS
        holder.btnMinus.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Cart item = cartList.get(pos);

            if (item.getQuantity() > 1) {

                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(pos);

            } else {

                cartList.remove(pos);
                notifyItemRemoved(pos);
            }

            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvQty, tvPrice, tvTotal;
        Button btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvCartName);
            tvQty = itemView.findViewById(R.id.tvCartQty);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvTotal = itemView.findViewById(R.id.tvCartTotal);

            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}