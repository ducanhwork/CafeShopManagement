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
import com.group3.application.model.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    // 1. Interface mới để gửi sự kiện (Product, Quantity mới) lên ViewModel
    public interface Listener {
        void onQuantityChanged(Product product, int newQuantity);
    }

    // 2. Interface mới để lấy số lượng hiện tại từ ViewModel
    public interface QuantityFetcher {
        int getQuantity(String productId);
    }

    private final List<Product> data = new ArrayList<>();
    private final Listener listener;
    private final QuantityFetcher quantityFetcher; // Biến mới để lấy dữ liệu

    // 3. Cập nhật Constructor
    public ProductAdapter(Listener listener, QuantityFetcher fetcher) {
        this.listener = listener;
        this.quantityFetcher = fetcher;
    }

    public void submit(List<Product> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_item_for_order, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder h, int pos) {
        Product p = data.get(pos);

        h.tvName.setText(p.getName()); // Nên dùng getName()
        h.tvPrice.setText(String.format("%,.0f đ", p.getPrice())); // Nên dùng getPrice()
        Glide.with(h.img).load(p.getImageLink()).into(h.img);

        // 4. Lấy số lượng từ ViewModel thông qua QuantityFetcher
        int currentQty = quantityFetcher.getQuantity(p.getId()); // Giả sử Product có getId()
        h.tvQty.setText(String.valueOf(currentQty));

        // --- Nút Giảm (-) ---
        h.btnMinus.setOnClickListener(v -> {
            int q = quantityFetcher.getQuantity(p.getId());
            if (q > 0) {
                int newQty = q - 1;
                if (listener != null) listener.onQuantityChanged(p, newQty); // Gửi sự kiện
            }
        });

        // --- Nút Tăng (+) ---
        h.btnPlus.setOnClickListener(v -> {
            int q = quantityFetcher.getQuantity(p.getId());
            int newQty = q + 1;
            if (listener != null) listener.onQuantityChanged(p, newQty); // Gửi sự kiện
            // KHÔNG TỰ CẬP NHẬT TVQTY ở đây. ViewModel sẽ làm và Activity sẽ notify adapter.
        });

        h.itemView.setOnClickListener(v -> h.btnPlus.performClick());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        // ... (Giữ nguyên)
        ImageView img;
        TextView tvName, tvPrice, tvQty;
        ImageButton btnMinus, btnPlus;

        ProductViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.img);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvQty = v.findViewById(R.id.tvQty);
            btnMinus = v.findViewById(R.id.btnMinus);
            btnPlus = v.findViewById(R.id.btnPlus);
        }
    }
}