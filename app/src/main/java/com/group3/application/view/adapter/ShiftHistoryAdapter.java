package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.Shift;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShiftHistoryAdapter extends RecyclerView.Adapter<ShiftHistoryAdapter.ShiftViewHolder> {

    private List<Shift> shifts;
    private final OnShiftClickListener onClickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnShiftClickListener {
        void onShiftClick(Shift shift);
    }

    public ShiftHistoryAdapter(List<Shift> shifts, OnShiftClickListener onClickListener) {
        this.shifts = shifts;
        this.onClickListener = onClickListener;
    }

    public void updateData(List<Shift> newShifts) {
        this.shifts = newShifts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShiftViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shift_history, parent, false);
        return new ShiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShiftViewHolder holder, int position) {
        Shift shift = shifts.get(position);
        holder.bind(shift);
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    class ShiftViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvEmployeeName;
        private final TextView tvStartTime;
        private final TextView tvEndTime;
        private final TextView tvDuration;
        private final TextView tvOpeningCash;
        private final TextView tvClosingCash;
        private final TextView tvStatus;
        private final TextView tvDiscrepancy;

        public ShiftViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvOpeningCash = itemView.findViewById(R.id.tvOpeningCash);
            tvClosingCash = itemView.findViewById(R.id.tvClosingCash);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDiscrepancy = itemView.findViewById(R.id.tvDiscrepancy);
        }

        public void bind(Shift shift) {
            tvEmployeeName.setText(shift.getUserFullName());
            tvStartTime.setText("Start: " + formatDateTime(shift.getStartTime()));
            
            if (shift.getEndTime() != null) {
                tvEndTime.setText("End: " + formatDateTime(shift.getEndTime()));
                tvEndTime.setVisibility(View.VISIBLE);
            } else {
                tvEndTime.setVisibility(View.GONE);
            }

            if (shift.getDurationMinutes() != null) {
                long hours = shift.getDurationMinutes() / 60;
                long minutes = shift.getDurationMinutes() % 60;
                tvDuration.setText(String.format(Locale.getDefault(), "Duration: %dh %dm", hours, minutes));
                tvDuration.setVisibility(View.VISIBLE);
            } else {
                tvDuration.setVisibility(View.GONE);
            }

            tvOpeningCash.setText("Opening: ₫" + shift.getOpeningCash());
            
            if (shift.getClosingCash() != null) {
                tvClosingCash.setText("Closing: ₫" + shift.getClosingCash());
                tvClosingCash.setVisibility(View.VISIBLE);
            } else {
                tvClosingCash.setVisibility(View.GONE);
            }

            tvStatus.setText(shift.getStatus());
            
            // Set status color
            int statusColor;
            if ("Active".equalsIgnoreCase(shift.getStatus())) {
                statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark);
            } else {
                statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark);
            }
            tvStatus.setTextColor(statusColor);

            // Show discrepancy if exists
            if (shift.getCashDiscrepancy() != null && shift.getCashDiscrepancy().doubleValue() != 0) {
                tvDiscrepancy.setText("Discrepancy: ₫" + shift.getCashDiscrepancy());
                tvDiscrepancy.setVisibility(View.VISIBLE);
                
                int discrepancyColor = shift.getCashDiscrepancy().doubleValue() > 0 
                    ? ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark)
                    : ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark);
                tvDiscrepancy.setTextColor(discrepancyColor);
            } else {
                tvDiscrepancy.setVisibility(View.GONE);
            }

            cardView.setOnClickListener(v -> onClickListener.onShiftClick(shift));
        }

        private String formatDateTime(LocalDateTime dateTime) {
            if (dateTime == null) return "N/A";
            
            Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            return dateFormat.format(date);
        }
    }
}
