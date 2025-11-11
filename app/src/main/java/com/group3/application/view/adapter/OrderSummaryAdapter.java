package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Thêm thư viện Glide (hoặc Picasso)
import com.group3.application.R;
import com.group3.application.model.dto.OrderItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // Đổi Locale

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.VH> {

    public interface OnQuantityChangedListener {
        void onIncrease(OrderItemDTO item);
        void onDecrease(OrderItemDTO item);
    }

    private final List<OrderItemDTO> data = new ArrayList<>();
    private final OnQuantityChangedListener listener;

    public OrderSummaryAdapter(OnQuantityChangedListener listener) {
        this.listener = listener;
    }

    public void submit(List<OrderItemDTO> items) {
        data.clear();
        if (items != null) {
            data.addAll(new ArrayList<>(items));
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_summary, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        OrderItemDTO it = data.get(i);
        h.tvName.setText(it.name);
        h.tvPrice.setText(fmt(it.unitPrice) + " đ");
        h.tvQty.setText(String.valueOf(it.quantity));
        h.tvSubtotal.setText(fmt(it.getSubtotal()) + " đ");

        h.btnMinus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecrease(it);
            }
        });

        h.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncrease(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvPrice, tvQty, btnMinus, btnPlus, tvSubtotal;

        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.img);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvQty = v.findViewById(R.id.tvQty);
            btnMinus = v.findViewById(R.id.btnMinus);
            btnPlus = v.findViewById(R.id.btnPlus);
            tvSubtotal = v.findViewById(R.id.tvSubtotal);
        }
    }

    private static String fmt(double d) {
        return String.format(Locale.getDefault(), "%,.0f", d);
    }
}
