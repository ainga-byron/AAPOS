package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.a10.R;

import java.util.ArrayList;

import models.Sale;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    Context context;
    ArrayList<Sale> list;

    public SalesAdapter(Context context, ArrayList<Sale> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvQuantity, tvPrice, tvTotal, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_sale, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Sale sale = list.get(position);

        holder.tvProductName.setText(sale.getProductName());
        holder.tvQuantity.setText("Qty: " + sale.getQuantity());
        holder.tvPrice.setText("Price: KES " + sale.getPrice());
        holder.tvTotal.setText("Total: KES " + sale.getTotal());
        holder.tvDate.setText(sale.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}