package com.group3.application.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.group3.application.R;
import com.group3.application.common.util.PreferenceManager;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.view.AddEditIngredientActivity;
import com.group3.application.view.adapter.IngredientAdapter;
import com.group3.application.viewmodel.InventoryViewModel;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

/**
 * Inventory Management Fragment
 * Displays ingredient list with CRUD operations
 */
public class InventoryFragment extends Fragment {
    
    private InventoryViewModel viewModel;
    private IngredientAdapter adapter;
    
    // View components
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerIngredients;
    private ChipGroup chipGroupFilters;
    private Chip chipAll, chipLowStock, chipOutOfStock;
    private ExtendedFloatingActionButton fabAddIngredient;
    private MaterialCardView lowStockBanner;
    private TextView lowStockCount;
    private View emptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredient_list, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupViewModel();
        setupRecyclerView();
        setupFilters();
        setupFAB();
        setupSwipeRefresh();
        observeViewModel();
        
        // Load initial data
        viewModel.loadIngredients();
    }
    
    private void initializeViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        recyclerIngredients = view.findViewById(R.id.recycler_ingredients);
        chipGroupFilters = view.findViewById(R.id.chip_group_filters);
        chipAll = view.findViewById(R.id.chip_all);
        chipLowStock = view.findViewById(R.id.chip_low_stock);
        chipOutOfStock = view.findViewById(R.id.chip_out_of_stock);
        fabAddIngredient = view.findViewById(R.id.fab_add_ingredient);
        lowStockBanner = view.findViewById(R.id.low_stock_banner);
        lowStockCount = view.findViewById(R.id.low_stock_count);
        emptyState = view.findViewById(R.id.empty_state);
        
        // Debug: Check if FAB was found
        if (fabAddIngredient == null) {
            Toast.makeText(requireContext(), "Inventory FAB not found in layout!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireContext(), "Inventory FAB found, role: " + PreferenceManager.getUserRole(requireContext()), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
    }
    
    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerIngredients.setLayoutManager(gridLayoutManager);
        
        adapter = new IngredientAdapter(new ArrayList<>(), new IngredientAdapter.OnIngredientClickListener() {
            @Override
            public void onIngredientClick(Ingredient ingredient) {
                showIngredientDetails(ingredient);
            }
            
            @Override
            public void onAddStockClick(Ingredient ingredient) {
                showAddStockDialog(ingredient);
            }
            
            @Override
            public void onIngredientLongClick(Ingredient ingredient) {
                if (isManager()) {
                    showIngredientContextMenu(ingredient);
                }
            }
        });
        
        recyclerIngredients.setAdapter(adapter);
        
        // Add scroll listener to hide/show FAB
        recyclerIngredients.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fabAddIngredient.isExtended()) {
                    fabAddIngredient.shrink();
                } else if (dy < 0 && !fabAddIngredient.isExtended()) {
                    fabAddIngredient.extend();
                }
            }
        });
    }
    
    private void setupFilters() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_all) {
                    viewModel.setStockFilter(null);
                } else if (checkedId == R.id.chip_low_stock) {
                    viewModel.setStockFilter("low_stock");
                } else if (checkedId == R.id.chip_out_of_stock) {
                    viewModel.setStockFilter("out_of_stock");
                }
            }
        });
    }
    
    private void setupFAB() {
        // Show FAB only for Manager role
        if (fabAddIngredient == null) {
            Toast.makeText(requireContext(), "ERROR: Inventory FAB is NULL!", Toast.LENGTH_LONG).show();
            return;
        }
        
        String role = PreferenceManager.getUserRole(requireContext());
        Toast.makeText(requireContext(), "Your role: " + role, Toast.LENGTH_LONG).show();
        
        if (isManager()) {
            fabAddIngredient.setVisibility(View.VISIBLE);
            fabAddIngredient.show(); // Force show
            fabAddIngredient.setOnClickListener(v -> openAddIngredient());
            Toast.makeText(requireContext(), "Inventory FAB visible for Manager", Toast.LENGTH_SHORT).show();
        } else {
            fabAddIngredient.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Inventory FAB hidden - not Manager role", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadIngredients();
        });
    }
    
    private void observeViewModel() {
        // Observe ingredient list
        viewModel.getIngredientList().observe(getViewLifecycleOwner(), ingredients -> {
            if (ingredients != null) {
                adapter.updateList(ingredients);
                
                // Show/hide empty state
                if (ingredients.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerIngredients.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerIngredients.setVisibility(View.VISIBLE);
                }
                
                // Update low stock banner
                updateLowStockBanner(ingredients);
            }
        });
        
        // Observe loading state
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(isLoading != null && isLoading);
            }
        });
        
        // Observe error messages
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG)
                        .setAction("Retry", v -> viewModel.loadIngredients())
                        .show();
                viewModel.clearMessages();
            }
        });
        
        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
                viewModel.clearMessages();
            }
        });
    }
    
    private void updateLowStockBanner(List<Ingredient> ingredients) {
        long lowStockItemsCount = ingredients.stream()
                .filter(Ingredient::isLowStock)
                .count();
        
        if (lowStockItemsCount > 0) {
            lowStockBanner.setVisibility(View.VISIBLE);
            lowStockCount.setText("Low Stock Items: " + lowStockItemsCount);
            
            lowStockBanner.findViewById(R.id.btn_view_low_stock).setOnClickListener(v -> {
                chipLowStock.setChecked(true);
            });
        } else {
            lowStockBanner.setVisibility(View.GONE);
        }
    }
    
    private boolean isManager() {
        String role = PreferenceManager.getUserRole(requireContext());
        return "MANAGER".equals(role);
    }
    
    private void showIngredientDetails(Ingredient ingredient) {
        String details = "Name: " + ingredient.getName() + "\n" +
                        "Description: " + ingredient.getDescription() + "\n" +
                        "Current Stock: " + ingredient.getQuantityInStock() + "\n" +
                        "Reorder Level: " + ingredient.getReorderLevel() + "\n" +
                        "Status: " + ingredient.getStatus();
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ingredient Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .show();
    }
    
    private void showAddStockDialog(Ingredient ingredient) {
        AddStockDialog dialog = AddStockDialog.newInstance(ingredient);
        dialog.setOnStockAddedListener((ingredientId, quantity, type, notes) -> {
            Toast.makeText(requireContext(), 
                "Adding stock: " + quantity + " units (" + type + ")", 
                Toast.LENGTH_SHORT).show();
            viewModel.addStockTransaction(ingredientId, (int) quantity, type, notes);
        });
        dialog.show(getChildFragmentManager(), "AddStockDialog");
    }
    
    private void showIngredientContextMenu(Ingredient ingredient) {
        String[] options = {"Edit Ingredient", "Delete Ingredient"};
        
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(ingredient.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openEditIngredient(ingredient);
                    } else {
                        confirmDeleteIngredient(ingredient);
                    }
                })
                .show();
    }
    
    private void openEditIngredient(Ingredient ingredient) {
        Intent intent = new Intent(getActivity(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_MODE, AddEditIngredientActivity.MODE_EDIT);
        intent.putExtra(AddEditIngredientActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
        startActivity(intent);
    }
    
    private void confirmDeleteIngredient(Ingredient ingredient) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete " + ingredient.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteIngredient(ingredient.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void openAddIngredient() {
        Intent intent = new Intent(getActivity(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_MODE, AddEditIngredientActivity.MODE_ADD);
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadIngredients();
    }
}
