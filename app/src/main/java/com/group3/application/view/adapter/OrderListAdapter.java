package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.Order;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private List<Order> orderList = new ArrayList<>();
    private final OnOrderClickListener clickListener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderListAdapter(OnOrderClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order, clickListener);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setData(List<Order> newOrders) {
        this.orderList.clear();
        if (newOrders != null) {
            this.orderList.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTableNames;
        private final TextView tvStatus;
        private final TextView tvStaffName;
        private final TextView tvOrderDate;
        private final TextView tvTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNames = itemView.findViewById(R.id.tv_table_names);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvStaffName = itemView.findViewById(R.id.tv_staff_name);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
        }

        public void bind(final Order order, final OnOrderClickListener listener) {
            tvTableNames.setText("Table: " + String.join(", ", order.getTableNames()));
            tvStatus.setText(order.getStatus());
            tvStaffName.setText("Server: " + order.getStaffName());
            tvOrderDate.setText("Date: " + formatDate(order.getOrderDate()));
            tvTotalAmount.setText(formatCurrency(order.getTotalAmount()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
        }

        private String formatDate(String isoDate) {
            if (isoDate == null) return "";
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(isoDate);
                SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                return newFormat.format(date);
            } catch (ParseException e) {
                return isoDate;
            }
        }

        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###,### đ");
            return formatter.format(amount);
        }
    }
}
