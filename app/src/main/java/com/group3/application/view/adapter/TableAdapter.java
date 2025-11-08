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

import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tableList;
    private final List<TableInfo> data = new ArrayList<>();
    private OnTableClickListener onTableClickListener;
    private final OnItemClick listener;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }
  
    public interface OnItemClick {
        void onClick(TableInfo item);
    }

    public TableAdapter(List<Table> tableList, OnTableClickListener onTableClickListener) {
        this.tableList = tableList;
        this.onTableClickListener = onTableClickListener;
    }

    public TableAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void setData(List<TableInfo> newData) {
        data.clear();
        if (newData != null) data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_list_item, parent, false);
        return new TableViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder h, int position) {
        TableInfo t = data.get(position);
        h.tvName.setText(t.getName());
        h.tvLocation.setText(t.getLocation() == null ? "—" : t.getLocation());
        h.tvSeat.setText("Seats: " + (t.getSeatCount() == null ? "?" : t.getSeatCount()));
        h.tvStatus.setText(t.getStatus());

        String s = t.getStatus() == null ? "" : t.getStatus().toUpperCase();
        int color = Color.GRAY;
        if (s.equals("EMPTY")) color = Color.parseColor("#2E7D32");
        else if (s.equals("SERVING")) color = Color.parseColor("#1565C0");
        else if (s.equals("WAITING_BILL")) color = Color.parseColor("#EF6C00");
        else if (s.equals("RESERVED")) color = Color.parseColor("#6A1B9A");

        h.tvStatus.getBackground().setTint(color);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(t);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, tvLocation, tvSeat;
        TableViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvLocation = v.findViewById(R.id.tvLocation);
            tvSeat = v.findViewById(R.id.tvSeat);
        }
    }
}
