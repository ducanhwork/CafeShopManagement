package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.dto.OrderDetailItemDTO; // SỬA: Import đúng DTO

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder> {

    private List<OrderDetailItemDTO> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<OrderDetailItemDTO> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView tvItemQuantity;
        private final TextView tvItemSubtotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemQuantity = itemView.findViewById(R.id.tv_item_quantity);
            tvItemSubtotal = itemView.findViewById(R.id.tv_item_subtotal);
        }

        void bind(OrderDetailItemDTO item) {
            tvItemName.setText(item.getProductName());
            tvItemQuantity.setText("x " + item.getQuantity());
            double subtotal = item.getPrice() * item.getQuantity();
            tvItemSubtotal.setText(formatCurrency(subtotal));
        }

        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###,### đ");
            return formatter.format(amount);
        }
    }
}
