package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.LowStockNotification;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.LowStockViewHolder> {

    private List<LowStockNotification> notifications;
    private final OnLowStockClickListener onClickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnLowStockClickListener {
        void onLowStockClick(LowStockNotification notification);
    }

    public LowStockAdapter(List<LowStockNotification> notifications,
                          OnLowStockClickListener onClickListener) {
        this.notifications = notifications;
        this.onClickListener = onClickListener;
    }

    public void updateData(List<LowStockNotification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LowStockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_low_stock, parent, false);
        return new LowStockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LowStockViewHolder holder, int position) {
        LowStockNotification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class LowStockViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvIngredientName;
        private final TextView tvCurrentStock;
        private final TextView tvReorderLevel;
        private final TextView tvMessage;
        private final TextView tvTimestamp;

        public LowStockViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvCurrentStock = itemView.findViewById(R.id.tvCurrentStock);
            tvReorderLevel = itemView.findViewById(R.id.tvReorderLevel);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(LowStockNotification notification) {
            tvIngredientName.setText(notification.getIngredientName());
            tvCurrentStock.setText("Current: " + notification.getCurrentStock());
            tvReorderLevel.setText("Reorder at: " + notification.getReorderLevel());
            tvMessage.setText(notification.getMessage());
            
            if (notification.getTimestamp() != null) {
                tvTimestamp.setText(dateFormat.format(notification.getTimestamp()));
            }

            cardView.setOnClickListener(v -> onClickListener.onLowStockClick(notification));
        }
    }
}
