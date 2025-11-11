package com.group3.application.view.adapter; // (Sửa package)

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group3.application.R;
import com.group3.application.model.dto.ItemReportDetailDTO;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemReportDetailAdapter extends RecyclerView.Adapter<ItemReportDetailAdapter.ViewHolder> {

    private final List<ItemReportDetailDTO> items = new ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("#,###,###");

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_item_report_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemReportDetailDTO item = items.get(position);
        holder.tvName.setText(item.getItemName());
        holder.tvUnit.setText(String.valueOf(item.getTotalUnit()));
        holder.tvRevenue.setText(formatCurrency(item.getTotalRevenue()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<ItemReportDetailDTO> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0";
        return formatter.format(amount);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvUnit, tvRevenue;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_tv_name);
            tvUnit = itemView.findViewById(R.id.item_tv_unit);
            tvRevenue = itemView.findViewById(R.id.item_tv_revenue);
        }
    }
}