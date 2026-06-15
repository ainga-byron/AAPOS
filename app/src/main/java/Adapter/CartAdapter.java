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
        this.cartList = (cartList != null) ? cartList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        if (cartList == null || position >= cartList.size()) return;

        Cart cart = cartList.get(position);

        if (cart == null) return;

        holder.tvName.setText(
                cart.getProductName() != null ? cart.getProductName() : "Item"
        );

        holder.tvQty.setText("Qty: " + cart.getQuantity());

        holder.tvPrice.setText("Price: KES " + cart.getPrice());

        holder.tvTotal.setText("Total: KES " + cart.getTotal());

        // ➕ PLUS
        holder.btnPlus.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Cart item = cartList.get(pos);
            if (item == null) return;

            item.setQuantity(item.getQuantity() + 1);

            notifyItemChanged(pos);

            if (listener != null) listener.onCartChanged();
        });

        // ➖ MINUS
        holder.btnMinus.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Cart item = cartList.get(pos);
            if (item == null) return;

            if (item.getQuantity() > 1) {

                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(pos);

            } else {

                cartList.remove(pos);
                notifyItemRemoved(pos);
            }

            if (listener != null) listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return (cartList != null) ? cartList.size() : 0;
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