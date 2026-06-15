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

import models.ExpenseReport;

public class Expenses extends RecyclerView.Adapter<Expenses.ExpenseViewHolder> {

    Context context;
    ArrayList<ExpenseReport> list;

    public Expenses(Context context, ArrayList<ExpenseReport> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.expense_items, parent, false);

        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {

        ExpenseReport e = list.get(position);

        holder.tvReason.setText(e.getReason());
        holder.tvCategory.setText("Category: " + e.getCategory());
        holder.tvAmount.setText("KES " + e.getAmount());
        holder.tvTime.setText(e.getDateTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView tvReason, tvCategory, tvAmount, tvTime;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReason = itemView.findViewById(R.id.tvReason);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}