# Inventory Management Feature - Implementation Guide

## Overview

Complete inventory management system for coffee shop ingredients with stock tracking, transactions, and low stock alerts.

## Completed Components

### 1. Data Models ✅

#### Entities

-   **Ingredient.java** - Main ingredient entity

    -   Fields: id, name, description, price, imageLink, status, categoryName, quantityInStock, reorderLevel, isLowStock
    -   Helper methods: isActive(), isLowStock(), isOutOfStock(), getStockDeficit(), getTotalStockValue(), isValid()
    -   Status constants: STATUS_ACTIVE, STATUS_INACTIVE

-   **StockTransaction.java** - Transaction tracking

    -   Fields: id, productId, productName, quantity, transactionType, notes, timestamp, performedBy
    -   Transaction types: INCOMING, OUTGOING, ADJUSTMENT
    -   Helper methods: isIncoming(), isOutgoing(), isAdjustment(), getSignedQuantity(), isValid()

-   **LowStockAlert.java** - Low stock notifications
    -   Fields: productId, productName, currentStock, reorderLevel, deficit, status
    -   Status types: LOW_STOCK, OUT_OF_STOCK
    -   Helper methods: isOutOfStock(), isLowStock(), getSafeDeficit()

#### DTOs

-   **CreateIngredientRequest.java** - Create ingredient request
-   **UpdateIngredientRequest.java** - Update ingredient request
-   **AddStockRequest.java** - Stock transaction request

### 2. API Service ✅

**InventoryApiService.java** - Retrofit interface with endpoints:

-   `GET /inventory/ingredients` - Get all ingredients
-   `GET /inventory/ingredients/{id}` - Get ingredient by ID
-   `GET /inventory/ingredients/search?name={name}` - Search ingredients
-   `POST /inventory/ingredients` - Create ingredient (Manager)
-   `PUT /inventory/ingredients/{id}` - Update ingredient (Manager)
-   `DELETE /inventory/ingredients/{id}` - Delete ingredient (Manager)
-   `POST /inventory/stock/incoming` - Add stock transaction
-   `GET /inventory/stock/{productId}` - Get stock level
-   `GET /inventory/transactions/{productId}` - Get transaction history
-   `GET /inventory/low-stock` - Get low stock alerts (Manager)

### 3. Repository ✅

**InventoryRepository.java** - Repository with ApiCallback pattern:

-   getAllIngredients(token, callback)
-   getIngredientById(token, id, callback)
-   searchIngredients(token, name, callback)
-   createIngredient(token, request, callback)
-   updateIngredient(token, id, request, callback)
-   deleteIngredient(token, id, callback)
-   addStockTransaction(token, request, callback)
-   getStockLevel(token, productId, callback)
-   getTransactionHistory(token, productId, callback)
-   getLowStockAlerts(token, callback)

### 4. ViewModel ✅

**InventoryViewModel.java** - AndroidViewModel with LiveData:

**LiveData:**

-   ingredientList - List of ingredients
-   currentIngredient - Selected ingredient
-   lowStockAlerts - Low stock alerts list
-   transactionHistory - Transaction history list
-   loading - Loading state
-   error - Error messages
-   successMessage - Success messages
-   stockFilter - Filter state (all, low_stock, out_of_stock)
-   searchQuery - Search query

**Methods:**

-   loadIngredients() - Load all ingredients
-   searchIngredients(name) - Search by name
-   getIngredientById(id) - Get single ingredient
-   createIngredient(...) - Create new ingredient (Manager)
-   updateIngredient(...) - Update ingredient (Manager)
-   deleteIngredient(id) - Delete ingredient (Manager)
-   addStockTransaction(...) - Add stock transaction
-   getTransactionHistory(productId) - Get transaction history
-   getLowStockAlerts() - Get low stock alerts (Manager)
-   setStockFilter(filter) - Set filter
-   setSearchQuery(query) - Set search query
-   clearFilters() - Clear all filters
-   clearMessages() - Clear messages

## Next Steps - UI Implementation

### 5. Fragment & Adapter (High Priority)

Need to create:

-   **IngredientListFragment.java** - Main inventory screen

    -   SearchView for search
    -   Filter chips (All, Low Stock, Out of Stock)
    -   RecyclerView grid layout
    -   FAB for add ingredient (Manager only)
    -   SwipeRefreshLayout
    -   Low stock banner at top
    -   Empty state view

-   **IngredientAdapter.java** - RecyclerView adapter
    -   Ingredient cards with:
        -   Image (using Glide)
        -   Name and description
        -   Current stock quantity
        -   Reorder level
        -   Low stock badge (red if isLowStock)
        -   Price
        -   Quick action buttons (Add Stock, View Details)
        -   Long-press for edit/delete (Manager)

### 6. Dialogs & Activities (Medium Priority)

Need to create:

-   **AddStockDialog.java** - Dialog for adding stock

    -   Transaction type RadioGroup (Incoming/Outgoing/Adjustment)
    -   Quantity input with quick buttons (10, 50, 100, 500)
    -   Notes input
    -   Real-time stock preview
    -   Validation

-   **AddEditIngredientActivity.java** - Create/Edit ingredient (Manager)

    -   Image picker
    -   All field inputs with validation
    -   Status switch
    -   Save/Delete buttons

-   **IngredientDetailActivity.java** - View/Edit ingredient

    -   TabLayout with 2 tabs:
        -   Tab 1: Ingredient Info
        -   Tab 2: Transaction History with chart
    -   Add Stock button
    -   Edit button (Manager)

-   **LowStockAlertsActivity.java** - Low stock alerts (Manager)
    -   List of low stock items
    -   Sort options
    -   Quick reorder buttons
    -   Export capability

### 7. Dependencies Required

Need to add to `build.gradle`:

```gradle
// Image loading
implementation 'com.github.bumptech.glide:glide:4.16.0'
annotationProcessor 'com.github.bumptech.glide:compiler:4.16.0'

// Charts for analytics
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

### 8. Helper Classes Needed

-   **ImageUtils.java** - Glide helper for loading images
    -   Load ingredient images with placeholders
    -   Handle error states
    -   Circular crop for thumbnails

### 9. Layout Files Required

-   `fragment_ingredient_list.xml` - Main fragment layout
-   `item_ingredient.xml` - Ingredient card item
-   `dialog_add_stock.xml` - Add stock dialog
-   `activity_add_edit_ingredient.xml` - Add/Edit ingredient
-   `activity_ingredient_detail.xml` - Ingredient detail
-   `activity_low_stock_alerts.xml` - Low stock alerts
-   `item_transaction.xml` - Transaction history item
-   `item_low_stock_alert.xml` - Low stock alert item

### 10. Drawable Resources Needed

-   Placeholder image for ingredients
-   Error image for failed loads
-   Icons for transaction types
-   Low stock warning icon
-   Empty state illustrations

## Color Scheme (Material Design 3)

```xml
<!-- Stock Status Colors -->
<color name="stock_in_stock">#4CAF50</color>      <!-- Green -->
<color name="stock_low_stock">#FF9800</color>     <!-- Orange -->
<color name="stock_out_of_stock">#F44336</color>  <!-- Red -->

<!-- Transaction Type Colors -->
<color name="transaction_incoming">#4CAF50</color>   <!-- Green -->
<color name="transaction_outgoing">#F44336</color>   <!-- Red -->
<color name="transaction_adjustment">#2196F3</color> <!-- Blue -->
```

## Key Features to Implement

### Role-Based Access

-   **Cashier**: View ingredients, add incoming stock, view stock levels, view transactions
-   **Manager**: Full CRUD on ingredients, all stock operations, view reports, low stock alerts, export data

### Validation Rules

-   Name: Required, max 100 characters
-   Description: Optional, max 500 characters
-   Price: Required, must be > 0, max 10 digits with 2 decimal places
-   Reorder Level: Optional, must be >= 0
-   Image Link: Optional, max 255 characters
-   Status: Must be 'active' or 'inactive'
-   Transaction Quantity: 1-10000
-   Transaction Type: INCOMING, OUTGOING, or ADJUSTMENT
-   Notes: Optional, max 500 characters

### Error Handling

-   Duplicate ingredient: Show dialog
-   Insufficient stock: Warning before outgoing transaction
-   Network errors: Snackbar with retry
-   Image load errors: Show placeholder
-   Invalid input: Inline errors in TextInputLayout

## Testing Checklist

-   [ ] Load ingredients list
-   [ ] Search ingredients by name
-   [ ] Filter by stock status (All, Low Stock, Out of Stock)
-   [ ] Create new ingredient (Manager)
-   [ ] Update ingredient (Manager)
-   [ ] Delete ingredient (Manager)
-   [ ] Add incoming stock
-   [ ] Add outgoing stock
-   [ ] Add adjustment transaction
-   [ ] View transaction history
-   [ ] View low stock alerts (Manager)
-   [ ] Image loading with Glide
-   [ ] Role-based UI visibility
-   [ ] Validation on all forms
-   [ ] Error handling
-   [ ] Success/error messages

## Backend Integration Notes

-   Base URL: `http://10.0.2.2:8080/api`
-   All endpoints require JWT Bearer token
-   Ingredients are stored as Products with category='Ingredient'
-   Stock tracking managed through Stock entity
-   Transaction types: INCOMING (add), OUTGOING (use), ADJUSTMENT (correct)
-   Low stock when quantityInStock <= reorderLevel
-   Status values: 'active' or 'inactive' (lowercase)

## Implementation Priority

1. ✅ Data models, API service, Repository, ViewModel (DONE)
2. 🔄 Add Glide and MPAndroidChart dependencies (NEXT)
3. 🔄 Create ImageUtils helper
4. 🔄 Create IngredientListFragment and Adapter
5. 🔄 Create AddStockDialog
6. 🔄 Create AddEditIngredientActivity
7. 🔄 Create IngredientDetailActivity
8. 🔄 Create LowStockAlertsActivity
9. 🔄 Add analytics dashboard (Manager)
10. 🔄 Testing and bug fixes

## Notes

-   All LiveData getters use correct names (getLoading, getError) matching observer patterns
-   Token automatically retrieved from PreferenceManager and prefixed with "Bearer "
-   Repository uses BaseRepository.ApiCallback pattern for consistency
-   ViewModel properly extends AndroidViewModel for context access
-   All entities have validation methods
-   Transaction types are case-sensitive: INCOMING, OUTGOING, ADJUSTMENT
-   Status values are lowercase: 'active', 'inactive'
