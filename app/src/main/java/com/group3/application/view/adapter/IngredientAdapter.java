package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.group3.application.R;
import com.group3.application.common.util.ImageUtils;
import com.group3.application.model.entity.Ingredient;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying ingredient items
 * Shows ingredient cards with image, details, stock status, and quick actions
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {
    
    private List<Ingredient> ingredientList;
    private final OnIngredientClickListener listener;
    
    /**
     * Interface for ingredient item click listeners
     */
    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
        void onAddStockClick(Ingredient ingredient);
        void onIngredientLongClick(Ingredient ingredient);
    }
    
    public IngredientAdapter(List<Ingredient> ingredientList, OnIngredientClickListener listener) {
        this.ingredientList = ingredientList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.bind(ingredient, listener);
    }
    
    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
    
    /**
     * Update the ingredient list and refresh the view
     */
    public void updateList(List<Ingredient> newList) {
        this.ingredientList = newList;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for ingredient items
     */
    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        
        private final MaterialCardView cardIngredient;
        private final ImageView imgIngredient;
        private final TextView txtIngredientName;
        private final TextView txtIngredientDescription;
        private final TextView txtPrice;
        private final TextView txtCurrentStock;
        private final TextView txtReorderLevel;
        private final Chip chipStockStatus;
        private final MaterialButton btnAddStock;
        private final MaterialButton btnViewDetails;
        
        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardIngredient = itemView.findViewById(R.id.card_ingredient);
            imgIngredient = itemView.findViewById(R.id.img_ingredient);
            txtIngredientName = itemView.findViewById(R.id.txt_ingredient_name);
            txtIngredientDescription = itemView.findViewById(R.id.txt_ingredient_description);
            txtPrice = itemView.findViewById(R.id.txt_price);
            txtCurrentStock = itemView.findViewById(R.id.txt_current_stock);
            txtReorderLevel = itemView.findViewById(R.id.txt_reorder_level);
            chipStockStatus = itemView.findViewById(R.id.chip_stock_status);
            btnAddStock = itemView.findViewById(R.id.btn_add_stock);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
        
        public void bind(Ingredient ingredient, OnIngredientClickListener listener) {
            // Set ingredient name
            txtIngredientName.setText(ingredient.getName());
            
            // Set description (hide if empty)
            if (ingredient.getDescription() != null && !ingredient.getDescription().isEmpty()) {
                txtIngredientDescription.setVisibility(View.VISIBLE);
                txtIngredientDescription.setText(ingredient.getDescription());
            } else {
                txtIngredientDescription.setVisibility(View.GONE);
            }
            
            // Set price
            txtPrice.setText(String.format(Locale.getDefault(), "$%.2f", ingredient.getPrice()));
            
            // Set current stock
            txtCurrentStock.setText(String.format(Locale.getDefault(), "%d units", 
                    ingredient.getQuantityInStock()));
            
            // Set reorder level
            txtReorderLevel.setText(String.format(Locale.getDefault(), "%d units", 
                    ingredient.getReorderLevel()));
            
            // Load ingredient image
            ImageUtils.loadIngredientThumbnail(itemView.getContext(), 
                    ingredient.getImageLink(), imgIngredient);
            
            // Set stock status chip
            updateStockStatusChip(ingredient);
            
            // Card click listener
            cardIngredient.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });
            
            // Card long click listener (for Manager actions)
            cardIngredient.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientLongClick(ingredient);
                }
                return true;
            });
            
            // Add stock button click
            btnAddStock.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddStockClick(ingredient);
                }
            });
            
            // View details button click
            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });
            
            // Dim card if ingredient is inactive
            if (ingredient.isInactive()) {
                cardIngredient.setAlpha(0.6f);
                cardIngredient.setEnabled(false);
            } else {
                cardIngredient.setAlpha(1.0f);
                cardIngredient.setEnabled(true);
            }
        }
        
        /**
         * Update stock status chip based on ingredient stock level
         */
        private void updateStockStatusChip(Ingredient ingredient) {
            if (ingredient.isOutOfStock()) {
                // Out of Stock - Red
                chipStockStatus.setText(R.string.out_of_stock);
                chipStockStatus.setChipBackgroundColorResource(R.color.stock_out_of_stock_bg);
                chipStockStatus.setTextColor(itemView.getContext().getColor(R.color.stock_out_of_stock));
                chipStockStatus.setChipStrokeWidth(0);
            } else if (ingredient.isLowStock()) {
                // Low Stock - Orange
                chipStockStatus.setText(R.string.low_stock);
                chipStockStatus.setChipBackgroundColorResource(R.color.stock_low_stock_bg);
                chipStockStatus.setTextColor(itemView.getContext().getColor(R.color.stock_low_stock));
                chipStockStatus.setChipStrokeWidth(0);
            } else {
                // In Stock - Green
                chipStockStatus.setText(R.string.in_stock);
                chipStockStatus.setChipBackgroundColorResource(R.color.stock_in_stock_bg);
                chipStockStatus.setTextColor(itemView.getContext().getColor(R.color.stock_in_stock));
                chipStockStatus.setChipStrokeWidth(0);
            }
        }
    }
}
