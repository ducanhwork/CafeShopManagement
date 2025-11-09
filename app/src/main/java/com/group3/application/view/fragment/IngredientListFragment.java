package com.group3.application.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.group3.application.R;
import com.group3.application.common.util.PreferenceManager;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.view.AddEditIngredientActivity;
import com.group3.application.view.adapter.IngredientAdapter;
import com.group3.application.viewmodel.InventoryViewModel;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying and managing the inventory ingredient list
 * Shows ingredients in a grid layout with search, filter, and CRUD operations
 */
public class IngredientListFragment extends Fragment {
    
    private InventoryViewModel viewModel;
    private IngredientAdapter adapter;
    
    // View components
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerIngredients;
    private SearchBar searchBar;
    private SearchView searchView;
    private RecyclerView recyclerSearchResults;
    private ChipGroup chipGroupFilters;
    private Chip chipAll, chipLowStock, chipOutOfStock;
    private ExtendedFloatingActionButton fabAddIngredient;
    private MaterialCardView lowStockBanner;
    private TextView lowStockCount;
    private View emptyState;
    
    // Search adapter
    private IngredientAdapter searchAdapter;
    
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
        setupSearchBar();
        setupFilters();
        setupFAB();
        setupSwipeRefresh();
        observeViewModel();
        
        // Load initial data
        viewModel.loadIngredients();
    }
    
    /**
     * Initialize all view components
     */
    private void initializeViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        recyclerIngredients = view.findViewById(R.id.recycler_ingredients);
        searchBar = view.findViewById(R.id.search_bar);
        searchView = view.findViewById(R.id.search_view);
        recyclerSearchResults = view.findViewById(R.id.recycler_search_results);
        chipGroupFilters = view.findViewById(R.id.chip_group_filters);
        chipAll = view.findViewById(R.id.chip_all);
        chipLowStock = view.findViewById(R.id.chip_low_stock);
        chipOutOfStock = view.findViewById(R.id.chip_out_of_stock);
        fabAddIngredient = view.findViewById(R.id.fab_add_ingredient);
        lowStockBanner = view.findViewById(R.id.low_stock_banner);
        lowStockCount = view.findViewById(R.id.low_stock_count);
        emptyState = view.findViewById(R.id.empty_state);
    }
    
    /**
     * Setup ViewModel and lifecycle
     */
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);
    }
    
    /**
     * Setup RecyclerView with grid layout and adapter
     */
    private void setupRecyclerView() {
        // Main ingredients list
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerIngredients.setLayoutManager(gridLayoutManager);
        
        adapter = new IngredientAdapter(new ArrayList<>(), new IngredientAdapter.OnIngredientClickListener() {
            @Override
            public void onIngredientClick(Ingredient ingredient) {
                // Navigate to ingredient detail
                openIngredientDetail(ingredient);
            }
            
            @Override
            public void onAddStockClick(Ingredient ingredient) {
                // Open add stock dialog
                showAddStockDialog(ingredient);
            }
            
            @Override
            public void onIngredientLongClick(Ingredient ingredient) {
                // Show context menu for Manager only
                if (isManager()) {
                    showIngredientContextMenu(ingredient);
                }
            }
        });
        
        recyclerIngredients.setAdapter(adapter);
        
        // Search results list
        GridLayoutManager searchLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerSearchResults.setLayoutManager(searchLayoutManager);
        
        searchAdapter = new IngredientAdapter(new ArrayList<>(), new IngredientAdapter.OnIngredientClickListener() {
            @Override
            public void onIngredientClick(Ingredient ingredient) {
                searchView.hide();
                openIngredientDetail(ingredient);
            }
            
            @Override
            public void onAddStockClick(Ingredient ingredient) {
                searchView.hide();
                showAddStockDialog(ingredient);
            }
            
            @Override
            public void onIngredientLongClick(Ingredient ingredient) {
                if (isManager()) {
                    searchView.hide();
                    showIngredientContextMenu(ingredient);
                }
            }
        });
        
        recyclerSearchResults.setAdapter(searchAdapter);
        
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
    
    /**
     * Setup SearchBar and SearchView
     */
    private void setupSearchBar() {
        // Setup SearchView text listener
        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            String query = searchView.getText().toString();
            if (!query.isEmpty()) {
                searchBar.setText(query);
                viewModel.setSearchQuery(query);
            }
            searchView.hide();
            return false;
        });
        
        // Search text change listener
        searchView.getEditText().addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    viewModel.searchIngredients(s.toString());
                } else {
                    searchAdapter.updateList(new ArrayList<>());
                }
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    /**
     * Setup filter chips
     */
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
    
    /**
     * Setup Floating Action Button (Manager only)
     */
    private void setupFAB() {
        // Show FAB only for Manager role
        if (isManager()) {
            fabAddIngredient.setVisibility(View.VISIBLE);
            fabAddIngredient.setOnClickListener(v -> openAddIngredient());
        } else {
            fabAddIngredient.setVisibility(View.GONE);
        }
    }
    
    /**
     * Setup SwipeRefreshLayout
     */
    private void setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener(() -> {
            viewModel.loadIngredients();
        });
    }
    
    /**
     * Observe ViewModel LiveData
     */
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
    
    /**
     * Update low stock banner based on ingredient list
     */
    private void updateLowStockBanner(List<Ingredient> ingredients) {
        long lowStockItemsCount = ingredients.stream()
                .filter(Ingredient::isLowStock)
                .count();
        
        if (lowStockItemsCount > 0) {
            lowStockBanner.setVisibility(View.VISIBLE);
            lowStockCount.setText(getString(R.string.low_stock_count) + ": " + lowStockItemsCount);
            
            // Setup view low stock button
            lowStockBanner.findViewById(R.id.btn_view_low_stock).setOnClickListener(v -> {
                chipLowStock.setChecked(true);
            });
        } else {
            lowStockBanner.setVisibility(View.GONE);
        }
    }
    
    /**
     * Check if current user is Manager
     */
    private boolean isManager() {
        String role = PreferenceManager.getUserRole(requireContext());
        return "MANAGER".equals(role);
    }
    
    /**
     * Open ingredient detail activity
     */
    private void openIngredientDetail(Ingredient ingredient) {
        // TODO: Navigate to IngredientDetailActivity (Task 10)
        // Intent intent = new Intent(getActivity(), IngredientDetailActivity.class);
        // intent.putExtra("ingredient_id", ingredient.getId());
        // startActivity(intent);
    }
    
    /**
     * Show add stock dialog
     */
    private void showAddStockDialog(Ingredient ingredient) {
        AddStockDialog dialog = AddStockDialog.newInstance(ingredient);
        dialog.setOnStockAddedListener((ingredientId, quantity, type, notes) -> {
            // Add stock transaction via ViewModel
            viewModel.addStockTransaction(ingredientId, (int) quantity, type, notes);
        });
        dialog.show(getChildFragmentManager(), "AddStockDialog");
    }
    
    /**
     * Show ingredient context menu (Manager only)
     */
    private void showIngredientContextMenu(Ingredient ingredient) {
        String[] options = {getString(R.string.edit_ingredient), getString(R.string.delete_ingredient)};
        
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(ingredient.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Edit
                        openEditIngredient(ingredient);
                    } else {
                        // Delete
                        confirmDeleteIngredient(ingredient);
                    }
                })
                .show();
    }
    
    /**
     * Open edit ingredient activity
     */
    private void openEditIngredient(Ingredient ingredient) {
        Intent intent = new Intent(getActivity(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_MODE, AddEditIngredientActivity.MODE_EDIT);
        intent.putExtra(AddEditIngredientActivity.EXTRA_INGREDIENT_ID, ingredient.getId());
        startActivity(intent);
    }
    
    /**
     * Confirm and delete ingredient
     */
    private void confirmDeleteIngredient(Ingredient ingredient) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_ingredient)
                .setMessage(R.string.delete_ingredient_confirm)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteIngredient(ingredient.getId());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    /**
     * Open add ingredient activity (Manager only)
     */
    private void openAddIngredient() {
        Intent intent = new Intent(getActivity(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_MODE, AddEditIngredientActivity.MODE_ADD);
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Reload data when fragment resumes
        viewModel.loadIngredients();
    }
}
