package com.group3.application.view.adapter; // (Sửa package)

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group3.application.R;
import com.group3.application.model.dto.StockItemDetailDTO;
import java.util.ArrayList;
import java.util.List;

public class StockReportAdapter extends RecyclerView.Adapter<StockReportAdapter.ViewHolder> {

    private final List<StockItemDetailDTO> details = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StockItemDetailDTO item = details.get(position);
        holder.tvName.setText(item.getItemName());
        holder.tvUnit.setText(item.getUnit());
        holder.tvStock.setText(String.valueOf(item.getStockQuantity()));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public void setData(List<StockItemDetailDTO> newDetails) {
        this.details.clear();
        if (newDetails != null) {
            this.details.addAll(newDetails);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUnit, tvStock;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_tv_name);
            tvUnit = itemView.findViewById(R.id.item_tv_unit);
            tvStock = itemView.findViewById(R.id.item_tv_stock);
        }
    }
}