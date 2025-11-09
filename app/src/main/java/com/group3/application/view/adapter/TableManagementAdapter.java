package com.group3.application.view.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Table Management (Manager role) - Grid layout with Material Design 3
 */
public class TableManagementAdapter extends RecyclerView.Adapter<TableManagementAdapter.TableViewHolder> {
    
    public interface OnItemClickListener {
        void onItemClick(TableInfo table);
        void onItemLongClick(TableInfo table);
    }

    private final List<TableInfo> data = new ArrayList<>();
    private final OnItemClickListener listener;

    public TableManagementAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<TableInfo> newData) {
        data.clear();
        if (newData != null) {
            data.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table_management, parent, false);
        return new TableViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        TableInfo table = data.get(position);
        
        // Set table name
        holder.tvName.setText(table.getName() != null ? table.getName() : "Table " + table.getId());
        
        // Set location
        holder.tvLocation.setText(table.getLocation() != null ? table.getLocation() : "—");
        
        // Set seat count
        int seatCount = table.getSeatCount() != null ? table.getSeatCount() : 0;
        holder.tvSeat.setText(seatCount + (seatCount == 1 ? " seat" : " seats"));
        
        // Set status chip
        String status = table.getStatus() != null ? table.getStatus() : "Available";
        holder.chipStatus.setText(status);
        
        // Apply status color
        int[] colors = getStatusColor(status);
        holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(colors[0]));
        holder.chipStatus.setTextColor(colors[1]);
        
        // Click listeners
        holder.cardTable.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(table);
            }
        });
        
        holder.cardTable.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(table);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Get color for status chip based on table status
     */
    private int[] getStatusColor(String status) {
        int backgroundColor;
        int textColor;
        
        if (status == null) {
            backgroundColor = Color.parseColor("#E0E0E0");
            textColor = Color.parseColor("#424242");
            return new int[]{backgroundColor, textColor};
        }
        
        switch (status.toUpperCase()) {
            case "AVAILABLE":
                backgroundColor = Color.parseColor("#C8E6C9"); // Light green
                textColor = Color.parseColor("#2E7D32"); // Dark green
                break;
            case "OCCUPIED":
                backgroundColor = Color.parseColor("#FFCDD2"); // Light red
                textColor = Color.parseColor("#C62828"); // Dark red
                break;
            case "RESERVED":
                backgroundColor = Color.parseColor("#BBDEFB"); // Light blue
                textColor = Color.parseColor("#1565C0"); // Dark blue
                break;
            default:
                backgroundColor = Color.parseColor("#E0E0E0"); // Light gray
                textColor = Color.parseColor("#424242"); // Dark gray
                break;
        }
        
        return new int[]{backgroundColor, textColor};
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardTable;
        TextView tvName;
        TextView tvLocation;
        TextView tvSeat;
        Chip chipStatus;

        TableViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTable = (MaterialCardView) itemView;
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSeat = itemView.findViewById(R.id.tvSeat);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
