package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients;
    private final OnIngredientClickListener onClickListener;
    private final OnIngredientEditListener onEditListener;
    private final OnIngredientDeleteListener onDeleteListener;
    private final OnAddStockListener onAddStockListener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public interface OnIngredientEditListener {
        void onIngredientEdit(Ingredient ingredient);
    }

    public interface OnIngredientDeleteListener {
        void onIngredientDelete(Ingredient ingredient);
    }

    public interface OnAddStockListener {
        void onAddStock(Ingredient ingredient);
    }

    public IngredientAdapter(List<Ingredient> ingredients,
                            OnIngredientClickListener onClickListener,
                            OnIngredientEditListener onEditListener,
                            OnIngredientDeleteListener onDeleteListener,
                            OnAddStockListener onAddStockListener) {
        this.ingredients = ingredients;
        this.onClickListener = onClickListener;
        this.onEditListener = onEditListener;
        this.onDeleteListener = onDeleteListener;
        this.onAddStockListener = onAddStockListener;
    }

    public void updateData(List<Ingredient> newIngredients) {
        this.ingredients = newIngredients;
        notifyDataSetChanged();
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
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvName;
        private final TextView tvUnit;
        private final TextView tvPrice;
        private final TextView tvQuantity;
        private final TextView tvLowStock;
        private final Button btnAddStock;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvName = itemView.findViewById(R.id.tvName);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvLowStock = itemView.findViewById(R.id.tvLowStock);
            btnAddStock = itemView.findViewById(R.id.btnAddStock);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Ingredient ingredient) {
            tvName.setText(ingredient.getName());
            tvUnit.setText(ingredient.getUnit());
            tvPrice.setText("₫" + ingredient.getPrice());
            // Use helper method to display quantity with unit (e.g., "Stock: 75 kg")
            tvQuantity.setText("Stock: " + ingredient.getQuantityWithUnit());

            if (ingredient.isLowStock()) {
                tvLowStock.setVisibility(View.VISIBLE);
                tvQuantity.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
            } else {
                tvLowStock.setVisibility(View.GONE);
                tvQuantity.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
            }

            cardView.setOnClickListener(v -> onClickListener.onIngredientClick(ingredient));
            btnAddStock.setOnClickListener(v -> onAddStockListener.onAddStock(ingredient));
            btnEdit.setOnClickListener(v -> onEditListener.onIngredientEdit(ingredient));
            btnDelete.setOnClickListener(v -> onDeleteListener.onIngredientDelete(ingredient));
        }
    }
}
