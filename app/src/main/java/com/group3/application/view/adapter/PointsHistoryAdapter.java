package com.group3.application.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.bean.PointsHistoryItem;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class PointsHistoryAdapter extends ListAdapter<PointsHistoryItem, PointsHistoryAdapter.HistoryViewHolder> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PointsHistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item_points_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvReason;
        private final TextView tvDate;
        private final TextView tvPointsChange;
        private final Context context;
        private final int defaultTextColor;
        private final TextView tvOrderNo;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvReason = itemView.findViewById(R.id.tv_reason);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvPointsChange = itemView.findViewById(R.id.tv_points_change);
            defaultTextColor = tvPointsChange.getCurrentTextColor();
        }

        public void bind(PointsHistoryItem item) {
            String reason = "Giao dịch";
            int pointsChange = 0;
            int color = defaultTextColor;

            if (item.getPointsEarned() != null && item.getPointsEarned() > 0) {
                pointsChange = item.getPointsEarned();
                color = ContextCompat.getColor(context, R.color.positive_points);
                tvPointsChange.setText(String.format("+%d", pointsChange));
            } else if (item.getPointsSpent() != null && item.getPointsSpent() > 0) {
                pointsChange = -item.getPointsSpent(); // Thêm dấu âm
                color = ContextCompat.getColor(context, R.color.negative_points);
                tvPointsChange.setText(String.valueOf(pointsChange));
            } else {
                tvPointsChange.setText("0");
            }
            tvPointsChange.setTextColor(color);


            if (Objects.equals(item.getType(), "EARN") && item.getOrderNo() != null) {
                reason = "Tích điểm đơn hàng " + item.getOrderNo();
            } else if (Objects.equals(item.getType(), "REDEEM")) {
                reason = "Đổi thưởng";
            }
            tvReason.setText(reason);

            if (item.getOrderNo() != null && !item.getOrderNo().isEmpty()) {
                tvOrderNo.setText("Mã đơn: " + item.getOrderNo());
                tvOrderNo.setVisibility(View.VISIBLE);
            } else {
                tvOrderNo.setVisibility(View.GONE);
            }

            if (item.getTransactionDate() != null) {
                tvDate.setText(item.getTransactionDate().format(formatter));
            } else {
                tvDate.setText("");
            }
        }
    }

    private static final DiffUtil.ItemCallback<PointsHistoryItem> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<PointsHistoryItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull PointsHistoryItem oldItem, @NonNull PointsHistoryItem newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull PointsHistoryItem oldItem, @NonNull PointsHistoryItem newItem) {
                return Objects.equals(oldItem.getType(), newItem.getType()) &&
                    Objects.equals(oldItem.getPointsEarned(), newItem.getPointsEarned()) &&
                    Objects.equals(oldItem.getPointsSpent(), newItem.getPointsSpent());
            }
        };
}
