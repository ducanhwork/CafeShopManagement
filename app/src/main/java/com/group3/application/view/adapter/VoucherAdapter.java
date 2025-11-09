package com.group3.application.view.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.group3.application.R;
import com.group3.application.model.bean.VoucherResponse;

import java.util.Locale;
import java.util.Objects;

public class VoucherAdapter extends ListAdapter<VoucherResponse, VoucherAdapter.VoucherViewHolder> {

    public interface OnVoucherClickListener {
        void onVoucherClick(VoucherResponse voucher);
    }


    private final OnVoucherClickListener listener;

    public VoucherAdapter(OnVoucherClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }


    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        VoucherResponse voucher = getItem(position);
        Context context = holder.itemView.getContext();

        holder.tvCode.setText(voucher.code);
        holder.chipStatus.setText(voucher.status);
        holder.tvDates.setText(String.format("%s → %s", voucher.startDate, voucher.endDate));

        if (voucher.value == null) {
            holder.tvTypeValue.setText("Giảm: N/A");
        } else {
            if ("PERCENT".equals(voucher.type)) {
                if (voucher.value == Math.floor(voucher.value)) {
                    holder.tvTypeValue.setText(String.format(Locale.US, "Giảm %d%%", voucher.value.longValue()));
                } else {
                    holder.tvTypeValue.setText(String.format(Locale.US, "Giảm %.2f%%", voucher.value));
                }
            } else {
                holder.tvTypeValue.setText(String.format(Locale.US, "Giảm %.0fK", voucher.value / 1000));
            }
        }

        if ("ACTIVE".equalsIgnoreCase(voucher.status)) {
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.coffee_primary)
            ));
            holder.chipStatus.setTextColor(ContextCompat.getColor(context, R.color.coffee_on_primary));
        } else {
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.chip_inactive_background)
            ));
            holder.chipStatus.setTextColor(ColorStateList.valueOf(
                ContextCompat.getColor(context, R.color.chip_inactive_text)
            ));
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVoucherClick(voucher);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVoucherClick(voucher);
            }
        });
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        final TextView tvCode, tvTypeValue, tvDates;
        final Chip chipStatus;
        final ImageButton btnEdit;

        VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvTypeValue = itemView.findViewById(R.id.tvTypeValue);
            tvDates = itemView.findViewById(R.id.tvDates);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    private static final DiffUtil.ItemCallback<VoucherResponse> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<VoucherResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull VoucherResponse oldItem, @NonNull VoucherResponse newItem) {
                return oldItem.id.equals(newItem.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull VoucherResponse oldItem, @NonNull VoucherResponse newItem) {
                return Objects.equals(oldItem.code, newItem.code) &&
                    Objects.equals(oldItem.status, newItem.status) &&
                    Objects.equals(oldItem.startDate, newItem.startDate) &&
                    Objects.equals(oldItem.endDate, newItem.endDate) &&
                    Objects.equals(oldItem.value, newItem.value) &&
                    Objects.equals(oldItem.type, newItem.type);
            }
        };
}
