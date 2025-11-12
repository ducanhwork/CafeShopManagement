package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.common.util.MoneyUtils;
import com.group3.application.model.dto.BillItemDTO;

import java.util.ArrayList;
import java.util.List;

public class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.VH> {
    private List<BillItemDTO> items = new ArrayList<>();

    public BillItemAdapter(List<BillItemDTO> items) {
        if (items != null) this.items = new ArrayList<>(items);
    }

    public void setItems(List<BillItemDTO> newItems) {
        this.items = newItems != null ? new ArrayList<>(newItems) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_line, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BillItemDTO it = items.get(position);
        holder.tvName.setText(it.getProductName() != null ? it.getProductName() : "-");
        holder.tvQty.setText("x" + it.getQuantity());
        holder.tvPrice.setText(MoneyUtils.format(it.getPriceAtOrder()));
        holder.tvLineTotal.setText(MoneyUtils.format(it.getLineTotal()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvPrice, tvLineTotal;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_name);
            tvQty = itemView.findViewById(R.id.item_qty);
            tvPrice = itemView.findViewById(R.id.item_price);
            tvLineTotal = itemView.findViewById(R.id.item_line_total);
        }
    }
}
