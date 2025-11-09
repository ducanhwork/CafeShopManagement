package com.group3.application.view.fragment; // Gói của Fragment

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // Cần để setup Toolbar
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment; // Lớp cha đã đổi
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Product;
import com.group3.application.view.OrderHostActivity; // Import Activity chứa
import com.group3.application.view.adapter.ProductAdapter;
import com.group3.application.viewmodel.OrderViewModel;
import com.group3.application.viewmodel.ProductListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// Sửa: extends Fragment và implements interfaces
public class ProductListFragment extends Fragment
        implements ProductAdapter.Listener, ProductAdapter.QuantityFetcher {

    // --- (Tất cả biến thành viên được copy từ Activity) ---
    private ProductListViewModel productVM;
    private OrderViewModel orderVM; // ViewModel dùng chung
    private ProductAdapter adapter;

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private TextInputEditText edtSearch;
    private MaterialAutoCompleteTextView actCategory;
    private ExtendedFloatingActionButton fab;
    private TextView tvSub; // Thêm biến cho tvSub

    private final List<Product> source = new ArrayList<>();
    private final List<Category> categoryData = new ArrayList<>();
    private ArrayAdapter<String> catAdapter;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;

    // Bỏ: tableId, tableName (sẽ lấy từ OrderViewModel)

    public ProductListFragment() {
        // Required empty public constructor
    }

    // Bỏ: newInstance() và các tham số (trừ khi bạn cần truyền dữ liệu đặc biệt)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Logic không liên quan đến View có thể đặt ở đây
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- (Toàn bộ logic từ onCreate của Activity được chuyển vào đây) ---

        // --- 1. Ánh xạ UI (dùng view.findViewById) ---
        MaterialToolbar tb = view.findViewById(R.id.toolbar);
        tvSub = view.findViewById(R.id.tvSubtitle);
        swipe = view.findViewById(R.id.swipe);
        rv = view.findViewById(R.id.rvProducts);
        edtSearch = view.findViewById(R.id.edtSearch);
        actCategory = view.findViewById(R.id.actCategory);
        fab = view.findViewById(R.id.fabAdd);
        View appBar = view.findViewById(R.id.appBar);

        // --- 2. Setup Toolbar (Fragment cần tham chiếu đến Activity) ---
        // (Giả sử OrderHostActivity kế thừa AppCompatActivity)
        ((AppCompatActivity) requireActivity()).setSupportActionBar(tb);
        tb.setTitle("Create Order");
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        // Sửa: Quay lại (pop fragment) hoặc đóng Activity
        tb.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        // --- 3. Khởi tạo ViewModel (QUAN TRỌNG) ---
        // productVM có scope là Fragment này (this)
        productVM = new ViewModelProvider(this).get(ProductListViewModel.class);
        // orderVM có scope là Activity chứa (requireActivity())
        orderVM = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        // --- 4. Lấy thông tin bàn từ OrderViewModel ---
        String tableName = orderVM.getTableNames();
        tvSub.setText("Choose product for table " + (tableName == null ? "" : tableName));

        // --- 5. Setup Adapter (dùng requireContext()) ---
        adapter = new ProductAdapter(this, this);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(adapter);

        catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        actCategory.setAdapter(catAdapter);
        actCategory.setOnClickListener(v -> actCategory.showDropDown());
        actCategory.setOnItemClickListener((parent, v, pos, id) -> {
            String selectedCategoryId = null;
            if (pos > 0 && pos - 1 < categoryData.size()) {
                selectedCategoryId = categoryData.get(pos - 1).getId();
            }
            productVM.applyFilter(selectedCategoryId, safeText(edtSearch));
        });

        swipe.setOnRefreshListener(productVM::reload);

        // --- 6. Observers (dùng getViewLifecycleOwner()) ---
        productVM.getProducts().observe(getViewLifecycleOwner(), list -> {
            source.clear();
            if (list != null) source.addAll(list);
            adapter.submit(source);
        });
        productVM.getLoadingProducts().observe(getViewLifecycleOwner(), isLoading ->
                swipe.setRefreshing(Boolean.TRUE.equals(isLoading)));
        productVM.getError().observe(getViewLifecycleOwner(), err -> {
            if (err != null) Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show();
        });
        productVM.getCategories().observe(getViewLifecycleOwner(), list -> {
            categoryData.clear();
            if (list != null) categoryData.addAll(list);

            List<String> display = new ArrayList<>();
            display.add("Tất cả");
            for (Category c : categoryData) display.add(c.getName());

            catAdapter.clear();
            catAdapter.addAll(display);
            catAdapter.notifyDataSetChanged();
            actCategory.setText("Tất cả", false);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable e) {
                if (pendingSearch != null) handler.removeCallbacks(pendingSearch);
                pendingSearch = () -> productVM.applyFilter(currentCategoryIdFromUi(), safeText(edtSearch));
                handler.postDelayed(pendingSearch, 300);
            }
        });

        // Observers cho OrderViewModel (dùng getViewLifecycleOwner())
        orderVM.getCurrentOrderItems().observe(getViewLifecycleOwner(), items -> {
            adapter.notifyDataSetChanged();
        });

        orderVM.getTotalAmount().observe(getViewLifecycleOwner(), total -> {
            int itemCount = 0;
            List<OrderItemDTO> items = orderVM.getCurrentOrderItems().getValue();
            if (items != null) {
                itemCount = items.size();
            }

            if (itemCount > 0) {
                fab.setText(String.format(Locale.getDefault(),
                        "Tổng: %,.0f đ (%d món)", total, itemCount));
                fab.show();
            } else {
                fab.hide();
            }
        });

        // --- 7. Load dữ liệu ban đầu ---
        productVM.setStatus("active");
        productVM.fetchCategories();
        productVM.reload();

        // --- 8. SỬA FAB Click (Điều hướng Fragment) ---
        fab.setOnClickListener(v -> {
            List<OrderItemDTO> cart = orderVM.getCurrentOrderItems().getValue();

            if (cart == null || cart.isEmpty()) {
                Toast.makeText(requireContext(), "Chưa chọn món nào", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm của Activity chứa để chuyển Fragment
            if (getActivity() instanceof OrderHostActivity) {
                ((OrderHostActivity) getActivity()).navigateToSummary();
            }
        });
    }

    // --- (Copy các hàm helper và interface) ---

    private String currentCategoryIdFromUi() {
        String label = safeText(actCategory);
        if (label == null || label.isEmpty() || "Tất cả".equalsIgnoreCase(label)) return null;
        for (Category c : categoryData) if (label.equals(c.getName())) return c.getId();
        return null;
    }

    private static String safeText(TextView tv) {
        return tv.getText() == null ? null : tv.getText().toString().trim();
    }

    @Override
    public void onQuantityChanged(Product product, int newQuantity) {
        orderVM.addOrUpdateItem(product, newQuantity);
    }

    @Override
    public int getQuantity(String productId) {
        List<OrderItemDTO> items = orderVM.getCurrentOrderItems().getValue();
        if (items != null) {
            for (OrderItemDTO item : items) {
                if (Objects.equals(item.productId, productId)) {
                    return item.quantity;
                }
            }
        }
        return 0;
    }
}