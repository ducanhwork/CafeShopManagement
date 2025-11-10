package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group3.application.R;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.dto.ProductForOrder;
import com.group3.application.model.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductForOrderAdapter extends RecyclerView.Adapter<ProductForOrderAdapter.ProductViewHolder> {

    public interface OnQuantityChangedListener {
        void onQuantityChanged(ProductForOrder product, int newQuantity);
    }

    private final List<ProductForOrder> productList = new ArrayList<>();
    private List<OrderItemDTO> orderItems = new ArrayList<>();
    private final OnQuantityChangedListener listener;

    public ProductForOrderAdapter(OnQuantityChangedListener listener) {
        this.listener = listener;
    }

    public void setProductList(List<ProductForOrder> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        notifyDataSetChanged();
    }

    public void setOrderItems(List<OrderItemDTO> items) {
        this.orderItems = (items == null) ? new ArrayList<>() : items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_for_order, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductForOrder product = productList.get(position);

        int currentQuantity = 0;
        if (orderItems != null) {
            for (OrderItemDTO item : orderItems) {
                if (item != null && Objects.equals(item.name, product.getName())) {
                    currentQuantity = item.quantity;
                    break;
                }
            }
        }

        holder.bind(product, currentQuantity, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvPrice, tvQty;
        ImageButton btnMinus, btnPlus;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }

        void bind(final ProductForOrder product, final int currentQuantity, final OnQuantityChangedListener listener) {
            tvName.setText(product.getName());
            tvPrice.setText(String.format("%,.0f đ", product.getPrice()));
            Glide.with(itemView.getContext()).load(product.getImageLink()).into(img);

            tvQty.setText(String.valueOf(currentQuantity));

            btnMinus.setOnClickListener(v -> {
                if (currentQuantity > 0 && listener != null) {
                    listener.onQuantityChanged(product, currentQuantity - 1);
                }
            });

            btnPlus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(product, currentQuantity + 1);
                }
            });

            itemView.setOnClickListener(v -> btnPlus.performClick());
        }
    }
}
