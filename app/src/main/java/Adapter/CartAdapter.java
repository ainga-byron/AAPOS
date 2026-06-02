package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import models.Cart;

public class CartAdapter
        extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context context;

    ArrayList<Cart> cartList;

    public CartAdapter(Context context,
                       ArrayList<Cart> cartList){

        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.cart_item,
                                parent,
                                false
                        );

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CartViewHolder holder,
            int position) {

        Cart cart = cartList.get(position);

        holder.tvName.setText(
                cart.getProductName()
        );

        holder.tvQty.setText(
                "Qty: " + cart.getQuantity()
        );

        holder.tvPrice.setText(
                "KES " + cart.getTotal()
        );
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder
            extends RecyclerView.ViewHolder{

        TextView tvName,tvQty,tvPrice;

        public CartViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvName =
                    itemView.findViewById(R.id.tvCartName);

            tvQty =
                    itemView.findViewById(R.id.tvCartQty);

            tvPrice =
                    itemView.findViewById(R.id.tvCartPrice);
        }
    }
}