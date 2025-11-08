
package com.group3.application.view.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.Table;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tableList;
    private OnTableClickListener onTableClickListener;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }

    public TableAdapter(List<Table> tableList, OnTableClickListener onTableClickListener) {
        this.tableList = tableList;
        this.onTableClickListener = onTableClickListener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);
        holder.tableNumber.setText(table.getTableNumber() + " (" + table.getCapacity() + " PAX)");

        CardView cardView = (CardView) holder.itemView;
        if ("Available".equals(table.getStatus())) {
            cardView.setCardBackgroundColor(Color.parseColor("#A5D6A7")); // Light Green
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#757575")); // Dark Gray
        }

        holder.itemView.setOnClickListener(v -> onTableClickListener.onTableClick(table));
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tableNumber = itemView.findViewById(R.id.table_number);
        }
    }
}
