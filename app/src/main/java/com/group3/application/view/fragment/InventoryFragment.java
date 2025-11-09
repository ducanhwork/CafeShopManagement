package com.group3.application.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group3.application.R;

/**
 * Inventory Management Fragment
 * Displays ingredient list and inventory operations
 */
public class InventoryFragment extends Fragment {

    private TextView tvPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        
        tvPlaceholder = view.findViewById(R.id.tv_placeholder);
        tvPlaceholder.setText("Inventory Management\n\nComing Soon:\n• Ingredient List\n• Add/Edit Ingredients\n• Incoming Stock\n• Low Stock Alerts");
        
        return view;
    }
}
