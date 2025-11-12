package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;

import java.util.List;

public class TableManagementAdapter extends RecyclerView.Adapter<TableManagementAdapter.TableViewHolder> {

    private List<TableInfo> tables;
    private final OnTableClickListener onClickListener;
    private final OnTableEditListener onEditListener;
    private final OnTableDeleteListener onDeleteListener;

    public interface OnTableClickListener {
        void onTableClick(TableInfo table);
    }

    public interface OnTableEditListener {
        void onTableEdit(TableInfo table);
    }

    public interface OnTableDeleteListener {
        void onTableDelete(TableInfo table);
    }

    public TableManagementAdapter(List<TableInfo> tables, 
                       OnTableClickListener onClickListener,
                       OnTableEditListener onEditListener,
                       OnTableDeleteListener onDeleteListener) {
        this.tables = tables;
        this.onClickListener = onClickListener;
        this.onEditListener = onEditListener;
        this.onDeleteListener = onDeleteListener;
    }

    public void updateData(List<TableInfo> newTables) {
        this.tables = newTables;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table_management, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        TableInfo table = tables.get(position);
        holder.bind(table);
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    class TableViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvTableName;
        private final TextView tvLocation;
        private final TextView tvSeatCount;
        private final TextView tvStatus;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTableName = itemView.findViewById(R.id.tvTableName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvSeatCount = itemView.findViewById(R.id.tvSeatCount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(TableInfo table) {
            tvTableName.setText(table.getName());
            tvLocation.setText(table.getLocation());
            tvSeatCount.setText(String.valueOf(table.getSeatCount()) + " seats");
            tvStatus.setText(table.getStatus());

            // Set status color
            int statusColor;
            switch (table.getStatus().toLowerCase()) {
                case "available":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark);
                    break;
                case "occupied":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark);
                    break;
                case "reserved":
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.holo_orange_dark);
                    break;
                default:
                    statusColor = ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray);
            }
            tvStatus.setTextColor(statusColor);

            cardView.setOnClickListener(v -> onClickListener.onTableClick(table));
            btnEdit.setOnClickListener(v -> onEditListener.onTableEdit(table));
            btnDelete.setOnClickListener(v -> onDeleteListener.onTableDelete(table));
        }
    }
}
