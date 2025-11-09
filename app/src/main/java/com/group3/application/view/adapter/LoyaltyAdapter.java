package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.group3.application.R;
import com.group3.application.model.bean.LoyaltyMemberListItem;
import java.util.Objects;

public class LoyaltyAdapter extends ListAdapter<LoyaltyMemberListItem, LoyaltyAdapter.MemberViewHolder> {

    public interface OnLoyaltyMemberClickListener {
        void onEditClick(LoyaltyMemberListItem member);
        void onItemClick(LoyaltyMemberListItem member);
    }

    private final OnLoyaltyMemberClickListener listener;

    public LoyaltyAdapter(OnLoyaltyMemberClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item_loyalty_member, parent, false);
        return new MemberViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        LoyaltyMemberListItem item = getItem(position);
        holder.bind(item);
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFullName;
        private final TextView tvPhone;
        private final TextView tvPoints;
        private final Chip chipTier;
        private final ImageButton btnEdit;

        private final OnLoyaltyMemberClickListener listener;

        public MemberViewHolder(@NonNull View itemView, OnLoyaltyMemberClickListener listener) {
            super(itemView);
            this.listener = listener;

            tvFullName = itemView.findViewById(R.id.tv_full_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvPoints = itemView.findViewById(R.id.tv_points);
            chipTier = itemView.findViewById(R.id.chip_tier);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }

        public void bind(LoyaltyMemberListItem item) {
            tvFullName.setText(item.getFullName());
            tvPhone.setText(item.getPhone());

            // Format lại text điểm
            if (item.getPoints() != null) {
                // Thêm " Điểm" vào sau số cho rõ ràng
                tvPoints.setText(String.format("%,d Điểm", item.getPoints()));
            } else {
                tvPoints.setText("0 Điểm");
            }

            if (item.getTier() != null && !item.getTier().isEmpty()) {
                chipTier.setText(item.getTier().toUpperCase());
                chipTier.setVisibility(View.VISIBLE);
            } else {
                chipTier.setVisibility(View.GONE);
            }

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(item);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    private static final DiffUtil.ItemCallback<LoyaltyMemberListItem> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<LoyaltyMemberListItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull LoyaltyMemberListItem oldItem, @NonNull LoyaltyMemberListItem newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull LoyaltyMemberListItem oldItem, @NonNull LoyaltyMemberListItem newItem) {
                return Objects.equals(oldItem.getFullName(), newItem.getFullName()) &&
                    Objects.equals(oldItem.getPhone(), newItem.getPhone()) &&
                    Objects.equals(oldItem.getPoints(), newItem.getPoints()) &&
                    Objects.equals(oldItem.getEmail(), newItem.getEmail()) &&
                    Objects.equals(oldItem.getTier(), newItem.getTier());
            }
        };
}
