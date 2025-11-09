package com.group3.application.view.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.group3.application.R;
import com.group3.application.model.entity.Product;
import com.group3.application.viewmodel.ProductListViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private OnItemClickListener listener;
    private ProductListViewModel viewModel;

    public ProductAdapter(List<Product> products, OnItemClickListener listener, ProductListViewModel viewModel) {
        this.products = products;
        this.listener = listener;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        // Set data cho view
        holder.tv_product_name.setText(product.getName());
        holder.tv_product_price.setText(String.format("%s VND", product.getPrice()));

        // QUAN TRỌNG: Remove listener trước khi set checked state
        holder.switchStatus.setOnCheckedChangeListener(null);

        // Set trạng thái switch từ data
        holder.switchStatus.setChecked(product.isActive());
        holder.switchStatus.setText(product.isActive() ? "Active" : "Inactive");


        // Set listener sau khi đã set checked state
        holder.switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Chỉ xử lý khi người dùng thực sự tương tác
            if (buttonView.isPressed()) {
                // Gọi ViewModel để update status
                viewModel.updateProductStatus(product.getId(), isChecked);
                // KHÔNG gọi notifyItemChanged ở đây
                // Data sẽ được tự động cập nhật qua LiveData
                notifyItemChanged(position);
            }
        });

        // Load image
        if (product.getImageLink() != null && !product.getImageLink().isEmpty()) {
            try {
                Picasso.get()
                        .load(product.getImageLink())
                        .placeholder(R.drawable.trends)
                        .error(R.drawable.trends)
                        .into(holder.iv_product_image);
            } catch (Exception e) {
                holder.iv_product_image.setImageResource(R.drawable.trends);
            }
        } else {
            holder.iv_product_image.setImageResource(R.drawable.trends);
        }

        holder.btnUpdate.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public void setData(List<Product> newData) {
        this.products = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private SwitchMaterial switchStatus;
        private MaterialButton btnUpdate;
        private TextView tv_product_price;
        private TextView tv_product_name;
        private ImageView iv_product_image;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            switchStatus = itemView.findViewById(R.id.switch_status);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            tv_product_price = itemView.findViewById(R.id.tv_product_price);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
        }
    }
}