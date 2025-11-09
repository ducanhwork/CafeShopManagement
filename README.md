# Cafe Shop Management - Android Application

## Project Overview

A comprehensive MVVM + Clean Architecture Android application for managing cafe shop operations including authentication, shift management, table management, and inventory management.

## Architecture

-   **Pattern**: MVVM + Clean Architecture
-   **Language**: Java
-   **Min SDK**: 27 (Android 8.1)
-   **Target SDK**: 36 (Android 14)
-   **Build System**: Gradle (Kotlin DSL)

## Completed Components

### 1. Gradle Dependencies ✅

All required dependencies have been added:

-   **Lifecycle**: ViewModel, LiveData, Runtime
-   **Navigation**: Fragment, UI
-   **Networking**: Retrofit, OkHttp, Gson
-   **Dependency Injection**: Dagger 2
-   **Local Database**: Room
-   **Image Loading**: Glide
-   **Security**: EncryptedSharedPreferences
-   **Charts**: MPAndroidChart
-   **QR Code**: ZXing
-   **Animations**: Lottie
-   **UI Components**: Shimmer, Paging 3, SwipeRefreshLayout
-   **Background Tasks**: WorkManager

### 2. Base Utilities ✅

Created in `com.group3.application.common.util`:

-   **PreferenceManager.java**: Secure token and user data storage using EncryptedSharedPreferences
-   **ValidationUtils.java**: Email, password, phone validation with strength checking
-   **CurrencyUtils.java**: Vietnamese currency formatting (₫)
-   **DateUtils.java**: Date/time formatting and parsing
-   **JWTUtils.java**: JWT token parsing and validation

### 3. API Client Setup ✅

-   **ApiClient.java**: Retrofit client with JWT authentication interceptor
-   **AuthApiService.java**: Authentication API endpoints

### 4. Model Classes (DTOs) ✅

Created in `com.group3.application.model.bean`:

-   **LoginRequest.java**
-   **RegisterRequest.java**
-   **AuthenticationResponse.java**

### 5. Application Class ✅

-   **CafeShopApplication.java**: Initializes API client with application context

### 6. Resources ✅

-   **colors.xml**: Complete color scheme including primary colors, status colors, table status colors
-   **dimens.xml**: Comprehensive dimension resources for spacing, text sizes, icons
-   **strings.xml**: All string resources for authentication, tables, shifts, inventory, errors

### 7. AndroidManifest ✅

-   Added Application class registration
-   Added necessary permissions (Internet, Network State, Camera)

## Project Structure

```
app/src/main/java/com/group3/application/
├── CafeShopApplication.java ✅
├── common/
│   ├── constant/ (README only)
│   ├── util/ ✅
│   │   ├── PreferenceManager.java
│   │   ├── ValidationUtils.java
│   │   ├── CurrencyUtils.java
│   │   ├── DateUtils.java
│   │   └── JWTUtils.java
│   └── validator/ (README only)
├── model/
│   ├── bean/ ✅ (Partial)
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── AuthenticationResponse.java
│   ├── entity/ ⏳ (TO DO)
│   │   └── TableInfo.java (exists)
│   ├── repository/ ⏳ (TO DO)
│   │   └── TableRepository.java (exists)
│   └── webservice/ ✅ (Partial)
│       ├── ApiClient.java
│       ├── ApiService.java (old, to be replaced)
│       └── AuthApiService.java
├── view/ ⏳ (Mostly TO DO)
│   ├── TableListActivity.java (exists)
│   ├── adapter/
│   │   └── TableAdapter.java (exists)
│   └── fragment/ (README only)
└── viewmodel/ ⏳ (Mostly TO DO)
    ├── TableViewModel.java (exists)
    ├── provider/ (README only)
    └── service/ (README only)
```

## TODO: Complete Implementation

### Phase 1: Core Infrastructure 🔴 PRIORITY

#### 1.1 Additional Utilities Needed

```java
// com.group3.application.common.util
- DialogUtils.java // Show dialogs for success, error, confirmation
- NetworkUtils.java // Check network connectivity
- StatusColorUtil.java // Get colors for different statuses
- QRCodeGenerator.java // Generate QR codes using ZXing
- ShiftCalculator.java // Calculate shift discrepancies
```

#### 1.2 Base Classes

```java
// com.group3.application.common.base
- BaseActivity.java // Common activity functionality
- BaseFragment.java // Common fragment functionality
- BaseViewModel.java // Common ViewModel functionality
- BaseRepository.java // Repository error handling
```

#### 1.3 Dependency Injection (Dagger 2)

```java
// com.group3.application.di
- AppComponent.java // Main Dagger component
- AppModule.java // Application-level dependencies
- NetworkModule.java // Retrofit, OkHttp configuration
- DatabaseModule.java // Room database configuration
- RepositoryModule.java // Repository bindings
- ViewModelModule.java // ViewModel factory
```

### Phase 2: Data Layer 🔴 PRIORITY

#### 2.1 Room Database

```java
// com.group3.application.data.local
- AppDatabase.java // Room database instance
- dao/
  - ShiftDao.java
  - TableDao.java
  - IngredientDao.java
  - PendingTransactionDao.java
- entity/
  - CachedShiftEntity.java
  - CachedTableEntity.java
  - CachedIngredientEntity.java
  - PendingTransactionEntity.java
```

#### 2.2 Complete API Services

```java
// com.group3.application.model.webservice
- ShiftApiService.java
- TableApiService.java
- InventoryApiService.java
```

#### 2.3 Complete DTOs/Models

```java
// com.group3.application.model.bean

// Shift-related
- StartShiftRequest.java
- EndShiftRequest.java
- ShiftResponse.java
- CashTransactionRequest.java
- CashTransactionResponse.java
- CashBalanceResponse.java

// Table-related
- TableRequest.java
- TableResponse.java

// Inventory-related
- IngredientRequest.java
- IngredientResponse.java
- IncomingStockRequest.java
- StockTransactionResponse.java
- LowStockResponse.java
```

#### 2.4 Repositories

```java
// com.group3.application.model.repository
- AuthRepository.java
- ShiftRepository.java
- TableRepository.java (enhance existing)
- InventoryRepository.java
```

### Phase 3: Authentication Feature 🟡 HIGH PRIORITY

#### 3.1 Activities & ViewModels

```java
// Activities
- SplashActivity.java
- LoginActivity.java
- RegisterActivity.java

// ViewModels
- LoginViewModel.java
- RegisterViewModel.java
```

#### 3.2 Layouts

```xml
<!-- res/layout -->
- activity_splash.xml
- activity_login.xml
- activity_register.xml
```

### Phase 4: Shift Management Feature 🟡 HIGH PRIORITY

#### 4.1 Components

```java
// Fragments
- ShiftDashboardFragment.java
- StartShiftBottomSheet.java
- EndShiftBottomSheet.java

// Activities
- RecordTransactionActivity.java
- ShiftHistoryActivity.java
- ShiftStatisticsActivity.java

// ViewModels
- ShiftViewModel.java
- ShiftHistoryViewModel.java
- ShiftStatisticsViewModel.java

// Adapters
- TransactionAdapter.java
- ShiftPagingAdapter.java
- ShiftPagingSource.java
```

#### 4.2 Layouts

```xml
- fragment_shift_dashboard.xml
- bottom_sheet_start_shift.xml
- bottom_sheet_end_shift.xml
- activity_record_transaction.xml
- activity_shift_history.xml
- activity_shift_statistics.xml
- item_transaction.xml
- item_shift.xml
```

### Phase 5: Table Management Feature 🟡 HIGH PRIORITY

#### 5.1 Components (Enhance Existing)

```java
// Fragments
- TableGridFragment.java

// BottomSheets
- TableActionBottomSheet.java

// Activities
- AddEditTableActivity.java (enhance existing TableListActivity)

// Enhance existing:
- TableAdapter.java
- TableViewModel.java
```

#### 5.2 Layouts

```xml
- fragment_table_grid.xml
- bottom_sheet_table_action.xml
- activity_add_edit_table.xml (enhance activity_table_list.xml)
- item_table_grid.xml
- item_table_list.xml (enhance table_list_item.xml)
```

### Phase 6: Inventory Management Feature 🟠 MEDIUM PRIORITY

#### 6.1 Components

```java
// Fragments
- IngredientListFragment.java

// Activities
- IngredientDetailsActivity.java
- AddEditIngredientActivity.java
- LowStockNotificationsActivity.java

// BottomSheets
- AddStockBottomSheet.java

// ViewModels
- InventoryViewModel.java

// Adapters
- IngredientAdapter.java
```

#### 6.2 Layouts

```xml
- fragment_ingredient_list.xml
- activity_ingredient_details.xml
- activity_add_edit_ingredient.xml
- activity_low_stock.xml
- bottom_sheet_add_stock.xml
- item_ingredient.xml
```

### Phase 7: Dashboard & Navigation 🟠 MEDIUM PRIORITY

#### 7.1 Components

```java
- DashboardActivity.java
- DashboardViewModel.java
```

#### 7.2 Navigation

```xml
<!-- res/navigation -->
- nav_graph.xml // Main navigation graph
```

#### 7.3 Layouts

```xml
- activity_dashboard.xml
- fragment_home.xml
```

#### 7.4 Menu Resources

```xml
<!-- res/menu -->
- bottom_navigation_menu.xml
- dashboard_menu.xml
- table_menu.xml
```

### Phase 8: Background Services 🟢 LOW PRIORITY

#### 8.1 WorkManager

```java
// com.group3.application.worker
- SyncTransactionsWorker.java // Sync pending transactions
- StockMonitorService.java // Monitor low stock
```

### Phase 9: Drawable Resources 🟢 LOW PRIORITY

```xml
<!-- res/drawable -->
- bg_button_primary.xml
- bg_button_secondary.xml
- bg_edittext.xml
- bg_card.xml
- bg_status_chip.xml (exists)
- ic_table.xml
- ic_shift.xml
- ic_inventory.xml
- ic_dashboard.xml
- selector_bottom_nav.xml
```

### Phase 10: Animation Resources 🟢 LOW PRIORITY

```xml
<!-- res/anim -->
- fade_in.xml
- fade_out.xml
- slide_up.xml
- slide_down.xml
- scale_up.xml
```

## API Endpoints Reference

### Base URL

```
http://10.0.2.2:8080/api
```

### Authentication

-   `POST /auth/login` - User login
-   `POST /auth/register` - User registration

### Shift Management

-   `POST /shifts/start` - Start new shift
-   `POST /shifts/end` - End current shift
-   `GET /shifts/current` - Get current shift
-   `POST /shifts/cash/record` - Record cash transaction
-   `GET /shifts/cash/balance` - Get cash balance
-   `GET /shifts` - Get shift history
-   `GET /shifts/statistics` - Get shift statistics

### Table Management

-   `GET /tables` - Get all tables
-   `POST /tables` - Create table
-   `PUT /tables/{id}` - Update table
-   `DELETE /tables/{id}` - Delete table
-   `GET /tables/status/{status}` - Get tables by status

### Inventory Management

-   `GET /inventory/ingredients` - Get all ingredients
-   `POST /inventory/ingredients` - Add ingredient
-   `PUT /inventory/ingredients/{id}` - Update ingredient
-   `DELETE /inventory/ingredients/{id}` - Delete ingredient
-   `POST /inventory/stock` - Add incoming stock
-   `GET /inventory/low-stock` - Get low stock items

## User Roles & Permissions

### CASHIER

-   Start/end shift
-   Record cash transactions
-   View tables
-   Add stock

### WAITER

-   View tables
-   Create/update tables

### MANAGER

-   All shift operations
-   All table operations
-   All inventory operations
-   View analytics

## Build Instructions

1. **Sync Gradle**: Ensure all dependencies are downloaded

    ```bash
    ./gradlew build
    ```

2. **Generate APK**:

    ```bash
    ./gradlew assembleDebug
    ```

3. **Install on Device**:
    ```bash
    ./gradlew installDebug
    ```

## Testing Credentials (For Development)

Create test users via the backend API:

-   **Manager**: manager@cafe.com / password123
-   **Cashier**: cashier@cafe.com / password123
-   **Waiter**: waiter@cafe.com / password123

## Known Issues & Limitations

1. **Offline Mode**: Pending transactions are queued but sync logic needs WorkManager implementation
2. **Image Upload**: Ingredient image upload needs multipart implementation
3. **PDF Export**: Shift statistics PDF export requires iText implementation
4. **Push Notifications**: Low stock notifications need Firebase Cloud Messaging
5. **Biometric Auth**: Fingerprint authentication not yet implemented

## Next Steps for Full Implementation

1. **Immediate**:

    - Complete Dagger 2 DI setup
    - Implement Room database
    - Create base classes (BaseActivity, BaseFragment)
    - Complete all API service interfaces

2. **Short Term**:

    - Implement Authentication UI (Splash, Login, Register)
    - Implement Dashboard with Navigation Component
    - Complete Table Management feature

3. **Medium Term**:

    - Implement Shift Management feature
    - Implement Inventory Management feature
    - Add proper error handling

4. **Long Term**:
    - WorkManager for background sync
    - Advanced features (PDF export, charts)
    - Unit and instrumentation tests

## Code Quality Standards

-   **Naming Conventions**: Follow Java conventions (camelCase for methods, PascalCase for classes)
-   **Comments**: Document complex business logic
-   **Error Handling**: Always handle network and database errors
-   **Security**: Use EncryptedSharedPreferences for sensitive data
-   **Architecture**: Maintain MVVM separation of concerns

## Resources & Documentation

-   [Android Architecture Components](https://developer.android.com/topic/architecture)
-   [Retrofit Documentation](https://square.github.io/retrofit/)
-   [Dagger 2 Guide](https://dagger.dev/)
-   [Room Database](https://developer.android.com/training/data-storage/room)
-   [Material Design 3](https://m3.material.io/)

## Contributors

-   Group 3 Development Team
-   GitHub Copilot AI Assistant

## License

This project is developed for educational purposes as part of the Cafe Shop Management System course project.

---

**Last Updated**: November 5, 2025
**Version**: 1.0.0
**Status**: Foundation Complete - Features In Progress
