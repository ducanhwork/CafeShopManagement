package com.group3.application.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.group3.application.R;
import com.group3.application.model.entity.Ingredient;
import com.group3.application.model.entity.LowStockNotification;
import com.group3.application.view.adapter.IngredientAdapter;
import com.group3.application.view.adapter.LowStockAdapter;
import com.group3.application.viewmodel.InventoryViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class InventoryManagementActivity extends AppCompatActivity {

    private InventoryViewModel viewModel;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;

    private IngredientAdapter ingredientAdapter;
    private LowStockAdapter lowStockAdapter;

    private int currentTab = 0; // 0: Ingredients, 1: Low Stock

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);

        setupToolbar();
        initViews();

        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        setupTabs();
        setupRecyclerView();
        observeViewModel();

        fabAdd.setOnClickListener(v -> {
            if (currentTab == 0) {
                showAddIngredientDialog();
            }
        });

        swipeRefresh.setOnRefreshListener(() -> loadCurrentTabData());

        loadCurrentTabData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Inventory Management");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Ingredients"));
        tabLayout.addTab(tabLayout.newTab().setText("Low Stock Alerts"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                fabAdd.setVisibility(currentTab == 0 ? View.VISIBLE : View.GONE);
                setupRecyclerView();
                loadCurrentTabData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerView() {
        if (currentTab == 0) {
            ingredientAdapter = new IngredientAdapter(new ArrayList<>(), 
                this::onIngredientClick, 
                this::onIngredientEdit, 
                this::onIngredientDelete,
                this::onAddStock);
            recyclerView.setAdapter(ingredientAdapter);
        } else {
            lowStockAdapter = new LowStockAdapter(new ArrayList<>(), this::onLowStockClick);
            recyclerView.setAdapter(lowStockAdapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void observeViewModel() {
        viewModel.getIngredients().observe(this, ingredients -> {
            if (ingredients != null && currentTab == 0) {
                ingredientAdapter.updateData(ingredients);
            }
        });

        viewModel.getLowStockItems().observe(this, lowStockItems -> {
            if (lowStockItems != null && currentTab == 1) {
                lowStockAdapter.updateData(lowStockItems);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(isLoading);
            } else {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentTabData() {
        if (currentTab == 0) {
            viewModel.fetchIngredients();
        } else {
            viewModel.fetchLowStockNotifications();
        }
    }

    private void onIngredientClick(Ingredient ingredient) {
        showIngredientDetailsDialog(ingredient);
    }

    private void onIngredientEdit(Ingredient ingredient) {
        showEditIngredientDialog(ingredient);
    }

    private void onIngredientDelete(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete " + ingredient.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteIngredient(ingredient.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void onAddStock(Ingredient ingredient) {
        showAddStockDialog(ingredient);
    }

    private void onLowStockClick(LowStockNotification notification) {
        // Load the ingredient and show add stock dialog
        showAddStockDialogById(notification.getIngredientId());
    }

    private void showAddIngredientDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Ingredient");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ingredient_form, null);
        setupIngredientFormDialog(dialogView, null);

        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            saveIngredient(dialogView, null);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditIngredientDialog(Ingredient ingredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Ingredient");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ingredient_form, null);
        setupIngredientFormDialog(dialogView, ingredient);

        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, which) -> {
            saveIngredient(dialogView, ingredient);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupIngredientFormDialog(View dialogView, Ingredient ingredient) {
        EditText etName = dialogView.findViewById(R.id.etIngredientName);
        EditText etUnit = dialogView.findViewById(R.id.etUnit);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        EditText etReorderLevel = dialogView.findViewById(R.id.etReorderLevel);

        if (ingredient != null) {
            etName.setText(ingredient.getName());
            etUnit.setText(ingredient.getUnit());
            etPrice.setText(ingredient.getPrice().toString());
            etQuantity.setText(ingredient.getQuantityInStock().toString());
            etReorderLevel.setText(ingredient.getReorderLevel().toString());
        }
    }

    private void saveIngredient(View dialogView, Ingredient existingIngredient) {
        EditText etName = dialogView.findViewById(R.id.etIngredientName);
        EditText etUnit = dialogView.findViewById(R.id.etUnit);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        EditText etReorderLevel = dialogView.findViewById(R.id.etReorderLevel);

        String name = etName.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String reorderLevelStr = etReorderLevel.getText().toString().trim();

        if (name.isEmpty() || unit.isEmpty() || priceStr.isEmpty() || 
            quantityStr.isEmpty() || reorderLevelStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Double price = Double.parseDouble(priceStr);
            Integer quantity = Integer.parseInt(quantityStr);
            Integer reorderLevel = Integer.parseInt(reorderLevelStr);

            if (existingIngredient != null) {
                // updateIngredient(id, name, description, price, reorderLevel, unit, imageLink, status)
                viewModel.updateIngredient(existingIngredient.getId(), name, unit, price, 
                    reorderLevel, unit, existingIngredient.getImageLink(), existingIngredient.getStatus());
            } else {
                // addIngredient(name, description, price, reorderLevel, unit, imageLink, status)
                viewModel.addIngredient(name, unit, price, reorderLevel, unit, "", "active");
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddStockDialog(Ingredient ingredient) {
        // Show unit in title so user knows what unit they're entering
        String title = "Add Stock - " + ingredient.getName();
        if (ingredient.getUnit() != null && !ingredient.getUnit().isEmpty()) {
            title += " (" + ingredient.getUnit() + ")";
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_stock, null);
        
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        EditText etSupplier = dialogView.findViewById(R.id.etSupplier);
        EditText etNotes = dialogView.findViewById(R.id.etNotes);
        
        // Add hint to quantity field to show unit
        if (ingredient.getUnit() != null && !ingredient.getUnit().isEmpty()) {
            etQuantity.setHint("Quantity (" + ingredient.getUnit() + ")");
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String quantityStr = etQuantity.getText().toString().trim();
            String supplier = etSupplier.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (quantityStr.isEmpty()) {
                Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Integer quantity = Integer.parseInt(quantityStr);
                // Combine supplier and notes for the notes field
                String fullNotes = notes;
                if (!supplier.isEmpty()) {
                    fullNotes = "Supplier: " + supplier + (notes.isEmpty() ? "" : "; " + notes);
                }
                // Use "INCOMING" as the transaction type for adding stock
                viewModel.addIncomingStock(ingredient.getId(), quantity, "INCOMING", fullNotes);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showAddStockDialogById(UUID ingredientId) {
        // In a real app, fetch the ingredient first
        // For now, show a simplified dialog
        Toast.makeText(this, "Add stock for ingredient: " + ingredientId, Toast.LENGTH_SHORT).show();
    }

    private void showIngredientDetailsDialog(Ingredient ingredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(ingredient.getName());

        String details = "Unit: " + ingredient.getUnit() + "\n" +
                        "Price: ₫" + ingredient.getPrice() + "\n" +
                        "In Stock: " + ingredient.getQuantityInStock() + "\n" +
                        "Reorder Level: " + ingredient.getReorderLevel() + "\n" +
                        "Low Stock: " + (ingredient.isLowStock() ? "Yes" : "No");

        builder.setMessage(details);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
