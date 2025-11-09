package com.group3.application.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.group3.application.R;
import com.group3.application.model.entity.TableInfo;
import com.group3.application.view.AdminHomeActivity;
import com.group3.application.view.adapter.TableManagementAdapter;
import com.group3.application.viewmodel.TableManagementViewModel;

/**
 * Tables Management Fragment
 * Displays table grid with search, filters, and CRUD operations
 */
public class TablesFragment extends Fragment {

    private TableManagementViewModel viewModel;
    private TableManagementAdapter adapter;
    
    // Views
    private SearchBar searchBar;
    private ChipGroup chipGroupStatus;
    private ChipGroup chipGroupLocation;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTable;
    
    // Filter states
    private String currentStatusFilter = null;
    private String currentLocationFilter = null;
    private String currentSearchQuery = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_management, container, false);
        
        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupFilters();
        setupSearchBar();
        setupFAB();
        
        // Initial load
        loadTables();
        
        return view;
    }

    private void initViews(View view) {
        searchBar = view.findViewById(R.id.searchBar);
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus);
        chipGroupLocation = view.findViewById(R.id.chipGroupLocation);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        recyclerView = view.findViewById(R.id.recyclerViewTables);
        fabAddTable = view.findViewById(R.id.fabAddTable);
        
        // Debug: Check if FAB was found
        if (fabAddTable == null) {
            Toast.makeText(requireContext(), "FAB not found in layout!", Toast.LENGTH_LONG).show();
        } else {
            fabAddTable.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "FAB found and set to visible", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(TableManagementViewModel.class);
        
        // Observe tables
        viewModel.getTableList().observe(getViewLifecycleOwner(), tables -> {
            if (tables != null) {
                adapter.setData(tables);
            }
        });
        
        // Observe loading state
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(Boolean.TRUE.equals(isLoading));
        });
        
        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                // Check if error is related to no active shift
                if (error.contains("No active shift") || error.contains("Please start a shift")) {
                    showNoActiveShiftMessage();
                } else {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                }
            }
        });
        
        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new TableManagementAdapter(new TableManagementAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TableInfo table) {
                showTableActionsDialog(table);
            }

            @Override
            public void onItemLongClick(TableInfo table) {
                showTableActionsDialog(table);
            }
        });
        
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        
        // Swipe to refresh
        swipeRefresh.setOnRefreshListener(this::loadTables);
    }

    private void setupFilters() {
        // Status filter
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                currentStatusFilter = null;
            } else {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chipAll) {
                    currentStatusFilter = null;
                } else if (checkedId == R.id.chipAvailable) {
                    currentStatusFilter = "Available";
                } else if (checkedId == R.id.chipOccupied) {
                    currentStatusFilter = "Occupied";
                } else if (checkedId == R.id.chipReserved) {
                    currentStatusFilter = "Reserved";
                }
            }
            loadTables();
        });
        
        // Location filter - simplified
        if (chipGroupLocation != null) {
            chipGroupLocation.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    currentLocationFilter = null;
                } else {
                    // Get the text of the checked chip
                    currentLocationFilter = null; // Simplified for now
                }
                loadTables();
            });
        }
    }

    private void setupSearchBar() {
        // SearchBar doesn't have addTextChangeListener, use different approach
        // For now, disable search or implement differently
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Search feature coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupFAB() {
        if (fabAddTable == null) {
            Toast.makeText(requireContext(), "ERROR: fabAddTable is NULL!", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Make absolutely sure it's visible
        fabAddTable.setVisibility(View.VISIBLE);
        fabAddTable.show(); // Force show if it's a FAB
        fabAddTable.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "FAB Clicked!", Toast.LENGTH_SHORT).show();
            showAddTableDialog();
        });
        
        Toast.makeText(requireContext(), "FAB setup complete and visible", Toast.LENGTH_SHORT).show();
    }

    private void showAddTableDialog() {
        AddTableBottomSheet dialog = AddTableBottomSheet.newInstance(
                (name, seatCount, location, status) -> {
                    // Create table via ViewModel
                    // ViewModel expects: (String name, String location, Integer seatCount, String status)
                    viewModel.createTable(name, location, seatCount, status);
                }
        );
        dialog.show(getParentFragmentManager(), "AddTableBottomSheet");
        
        // Observe success/error from ViewModel
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                dialog.onSuccess();
            }
        });
        
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                dialog.showError(error);
            }
        });
    }

    private void loadTables() {
        viewModel.loadTables(currentStatusFilter, currentLocationFilter);
    }

    private void showTableActionsDialog(TableInfo table) {
        String[] actions = {"View Details", "Change Status", "Edit Table", "Delete Table"};
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Table " + table.getName())
            .setItems(actions, (dialog, which) -> {
                switch (which) {
                    case 0: // View Details
                        showTableDetails(table);
                        break;
                    case 1: // Change Status
                        showChangeStatusDialog(table);
                        break;
                    case 2: // Edit
                        Toast.makeText(requireContext(), "Edit Table - Coming soon", Toast.LENGTH_SHORT).show();
                        break;
                    case 3: // Delete
                        confirmDeleteTable(table);
                        break;
                }
            })
            .show();
    }

    private void showTableDetails(TableInfo table) {
        String details = "Table Name: " + table.getName() + "\n" +
                        "Capacity: " + table.getSeatCount() + " people\n" +
                        "Location: " + table.getLocation() + "\n" +
                        "Status: " + table.getStatus();
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Table Details")
            .setMessage(details)
            .setPositiveButton("OK", null)
            .show();
    }

    private void showChangeStatusDialog(TableInfo table) {
        String[] statuses = {"Available", "Occupied", "Reserved"};
        int currentIndex = 0;
        
        if ("Occupied".equals(table.getStatus())) currentIndex = 1;
        else if ("Reserved".equals(table.getStatus())) currentIndex = 2;
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Table Status")
            .setSingleChoiceItems(statuses, currentIndex, (dialog, which) -> {
                String newStatus = statuses[which];
                viewModel.updateTableStatus(table.getId(), newStatus);
                dialog.dismiss();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void confirmDeleteTable(TableInfo table) {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Table")
            .setMessage("Are you sure you want to delete Table " + table.getName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> {
                viewModel.deleteTable(table.getId());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void showNoActiveShiftMessage() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("No Active Shift")
            .setMessage("Table management requires an active shift. Please start a shift from the Shift tab first.")
            .setPositiveButton("Go to Shift Tab", (dialog, which) -> {
                // Switch to Shift tab (index 0)
                if (getActivity() != null && getActivity() instanceof AdminHomeActivity) {
                    ((AdminHomeActivity) getActivity()).switchToShiftTab();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
