# Cafe Shop Management - Implementation Guide

## Quick Start for Completing the Project

This guide provides step-by-step instructions to complete the Cafe Shop Management application.

## Phase 1: Complete Core Infrastructure (2-3 days)

### Step 1.1: Create Utility Classes

Create these files in `com/group3/application/common/util/`:

#### DialogUtils.java

```java
package com.group3.application.common.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

    public static void showSuccessDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    public static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showConfirmationDialog(Context context, String message, OnConfirmListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> listener.onConfirm())
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public interface OnConfirmListener {
        void onConfirm();
    }
}
```

#### NetworkUtils.java

```java
package com.group3.application.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
```

#### StatusColorUtil.java

```java
package com.group3.application.common.util;

import android.graphics.Color;

public class StatusColorUtil {

    public static int getColorForStatus(String status) {
        switch (status.toUpperCase()) {
            case "AVAILABLE":
                return Color.parseColor("#4CAF50");
            case "OCCUPIED":
                return Color.parseColor("#F44336");
            case "RESERVED":
                return Color.parseColor("#FF9800");
            case "MAINTENANCE":
                return Color.parseColor("#9E9E9E");
            default:
                return Color.GRAY;
        }
    }
}
```

#### QRCodeGenerator.java

```java
package com.group3.application.common.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {

    public static Bitmap generate(String content, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

### Step 1.2: Create Base Classes

Create these files in `com/group3/application/common/base/`:

#### BaseActivity.java

```java
package com.group3.application.common.base;

import android.app.ProgressDialog;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.group3.application.common.util.DialogUtils;
import com.group3.application.common.util.NetworkUtils;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    protected void showLoading(String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = DialogUtils.showProgressDialog(this, message);
    }

    protected void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showError(String message) {
        DialogUtils.showErrorDialog(this, message);
    }

    protected void showSuccess(String message) {
        DialogUtils.showSuccessDialog(this, message);
    }

    protected boolean isNetworkAvailable() {
        return NetworkUtils.isNetworkAvailable(this);
    }
}
```

### Step 1.3: Implement Dagger 2 DI

Create these files in `com/group3/application/di/`:

#### AppComponent.java

```java
package com.group3.application.di;

import com.group3.application.CafeShopApplication;
import javax.inject.Singleton;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
    AndroidInjectionModule.class,
    AppModule.class,
    NetworkModule.class,
    DatabaseModule.class,
    RepositoryModule.class,
    ViewModelModule.class
})
public interface AppComponent {
    void inject(CafeShopApplication application);
}
```

#### AppModule.java

```java
package com.group3.application.di;

import android.app.Application;
import android.content.Context;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
```

## Phase 2: Room Database Setup (1 day)

### Step 2.1: Create Database Entities

Example for CachedTableEntity.java in `com/group3/application/data/local/entity/`:

```java
package com.group3.application.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "cached_tables")
public class CachedTableEntity {
    @PrimaryKey
    @NonNull
    private String id;

    private int tableNumber;
    private int capacity;
    private String status;
    private String qrCode;
    private long lastUpdated;

    // Constructor, getters, and setters
}
```

### Step 2.2: Create DAOs

Example for TableDao.java in `com/group3/application/data/local/dao/`:

```java
package com.group3.application.data.local.dao;

import androidx.room.*;
import com.group3.application.data.local.entity.CachedTableEntity;
import java.util.List;

@Dao
public interface TableDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTable(CachedTableEntity table);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedTableEntity> tables);

    @Query("SELECT * FROM cached_tables")
    List<CachedTableEntity> getAllTables();

    @Query("SELECT * FROM cached_tables WHERE status = :status")
    List<CachedTableEntity> getTablesByStatus(String status);

    @Query("SELECT * FROM cached_tables WHERE id = :id")
    CachedTableEntity getTableById(String id);

    @Delete
    void deleteTable(CachedTableEntity table);

    @Query("DELETE FROM cached_tables")
    void deleteAll();
}
```

### Step 2.3: Create AppDatabase

Create in `com/group3/application/data/local/`:

```java
package com.group3.application.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.group3.application.data.local.dao.*;
import com.group3.application.data.local.entity.*;

@Database(entities = {
    CachedTableEntity.class,
    // Add other entities here
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract TableDao tableDao();
    // Add other DAOs here

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "cafe_shop_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
```

## Phase 3: Authentication Implementation (2 days)

### Step 3.1: Create LoginActivity

Location: `com/group3/application/view/auth/LoginActivity.java`

Key points:

1. Use `ActivityLoginBinding` for view binding
2. Create `LoginViewModel`
3. Observe LiveData for login result
4. Handle success: Save token, navigate to Dashboard
5. Handle error: Show error dialog
6. Validate input before API call

### Step 3.2: Create activity_login.xml

Location: `res/layout/activity_login.xml`

Use Material Design 3 components:

-   TextInputLayout with TextInputEditText for email
-   TextInputLayout with TextInputEditText for password
-   MaterialButton for login
-   ProgressBar for loading state
-   TextView for register link

### Step 3.3: Create LoginViewModel

Location: `com/group3/application/viewmodel/LoginViewModel.java`

```java
package com.group3.application.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.group3.application.model.bean.*;
import com.group3.application.model.repository.AuthRepository;
import javax.inject.Inject;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private MutableLiveData<AuthenticationResponse> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    @Inject
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        LoginRequest request = new LoginRequest(email, password);

        authRepository.login(request, new ApiCallback<AuthenticationResponse>() {
            @Override
            public void onSuccess(AuthenticationResponse data) {
                isLoading.setValue(false);
                loginResult.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // Getters for LiveData
    public MutableLiveData<AuthenticationResponse> getLoginResult() {
        return loginResult;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
```

## Testing Strategy

### Unit Tests

Create in `app/src/test/java/`:

-   ValidationUtilsTest.java
-   CurrencyUtilsTest.java
-   DateUtilsTest.java

### Instrumentation Tests

Create in `app/src/androidTest/java/`:

-   LoginActivityTest.java
-   TableListActivityTest.java
-   DatabaseTest.java

## Common Issues & Solutions

### Issue 1: Dagger DI not working

**Solution**: Make sure to:

1. Add `@Inject` constructor to repositories and ViewModels
2. Provide dependencies in appropriate modules
3. Inject in activities using `AndroidInjection.inject(this)` in onCreate()

### Issue 2: Room database errors

**Solution**:

1. Enable fallbackToDestructiveMigration() for development
2. Check entity annotations are correct
3. Ensure DAO methods are properly annotated

### Issue 3: Retrofit network errors

**Solution**:

1. Check BASE_URL is correct (use 10.0.2.2 for emulator)
2. Add INTERNET permission in manifest
3. Enable usesCleartextTraffic for HTTP
4. Check backend API is running

## Development Timeline

### Week 1: Foundation

-   Days 1-2: Complete utilities and base classes
-   Days 3-4: Dagger DI and Room database
-   Day 5: Authentication feature

### Week 2: Core Features

-   Days 1-2: Dashboard and Navigation
-   Days 3-4: Table Management
-   Day 5: Testing and bug fixes

### Week 3: Advanced Features

-   Days 1-2: Shift Management
-   Days 3-4: Inventory Management
-   Day 5: Polish and optimization

### Week 4: Finalization

-   Days 1-2: WorkManager background tasks
-   Days 3-4: Testing (Unit + Integration)
-   Day 5: Documentation and deployment

## Best Practices

1. **Always check network before API calls**
2. **Use try-catch for database operations**
3. **Show loading indicators during async operations**
4. **Validate user input before processing**
5. **Handle all error cases gracefully**
6. **Use constants for magic strings**
7. **Comment complex business logic**
8. **Follow MVVM strictly - no business logic in Activities**
9. **Use ViewBinding instead of findViewById**
10. **Test on both emulator and physical device**

## Resources for Each Feature

### For Table Management:

-   RecyclerView with GridLayoutManager
-   SwipeRefreshLayout
-   Material CardView
-   QR Code generation with ZXing

### For Shift Management:

-   BottomSheetDialogFragment
-   MPAndroidChart for statistics
-   Paging 3 for history list
-   NumberPicker for cash amounts

### For Inventory:

-   ImageView with Glide
-   SearchView with filtering
-   ItemTouchHelper for swipe actions
-   Material Banner for low stock alerts

## Contact & Support

For issues or questions during implementation:

1. Check README.md for overview
2. Review this Implementation Guide
3. Refer to Android documentation
4. Use GitHub Copilot for code assistance

---

**Happy Coding! 🚀**
