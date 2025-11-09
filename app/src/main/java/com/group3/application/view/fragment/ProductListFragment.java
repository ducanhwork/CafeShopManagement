package com.group3.application.view.fragment;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
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
import com.group3.application.view.OrderHostActivity;
import com.group3.application.view.adapter.ProductAdapter;
import com.group3.application.viewmodel.OrderViewModel;
import com.group3.application.viewmodel.ProductListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

// SỬA: Bỏ implements, Fragment sẽ không cần biết chi tiết của Adapter nữa
public class ProductListFragment extends Fragment {

    private ProductListViewModel productVM;
    private OrderViewModel orderVM; // ViewModel dùng chung
    private ProductAdapter adapter;

    private SwipeRefreshLayout swipe;
    private RecyclerView rv;
    private TextInputEditText edtSearch;
    private MaterialAutoCompleteTextView actCategory;
    private ExtendedFloatingActionButton fab;
    private TextView tvSub;

    private final List<Category> categoryData = new ArrayList<>();
    private ArrayAdapter<String> catAdapter;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. Ánh xạ UI ---
        MaterialToolbar tb = view.findViewById(R.id.toolbar);
        tvSub = view.findViewById(R.id.tvSubtitle);
        swipe = view.findViewById(R.id.swipe);
        rv = view.findViewById(R.id.rvProducts);
        edtSearch = view.findViewById(R.id.edtSearch);
        actCategory = view.findViewById(R.id.actCategory);
        fab = view.findViewById(R.id.fabAdd);
        View appBar = view.findViewById(R.id.appBar);

        // --- 2. Setup Toolbar ---
        ((AppCompatActivity) requireActivity()).setSupportActionBar(tb);
        tb.setTitle("Create Order");
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        // --- 3. Khởi tạo ViewModel ---
        productVM = new ViewModelProvider(this).get(ProductListViewModel.class);
        orderVM = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        // --- 4. Lấy thông tin bàn từ OrderViewModel ---
        String tableName = orderVM.getTableNames();
        tvSub.setText("Choose product for table " + (tableName == null ? "" : tableName));

        // --- 5. Setup Adapter (SỬA: Khởi tạo theo cách mới) ---
        adapter = new ProductAdapter((product, newQuantity) -> {
            orderVM.addOrUpdateItem(product, newQuantity);
        });
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

        // --- 6. Observers ---
        productVM.getProducts().observe(getViewLifecycleOwner(), list -> {
            // SỬA: Gửi thẳng danh sách cho adapter
            adapter.setProductList(list);
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

        // SỬA: Lắng nghe giỏ hàng và đẩy xuống Adapter
        orderVM.getCurrentOrderItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setOrderItems(items);
        });

        orderVM.getTotalAmount().observe(getViewLifecycleOwner(), total -> {
            int totalItemCount = 0;
            List<OrderItemDTO> items = orderVM.getCurrentOrderItems().getValue();
            if (items != null) {
                // SỬA: Đếm tổng số lượng thay vì số loại món ăn
                for (OrderItemDTO item : items) {
                    totalItemCount += item.quantity;
                }
            }

            if (totalItemCount > 0) {
                fab.setText(String.format(Locale.getDefault(),
                        "Tổng: %,.0f đ (%d món)", total, totalItemCount));
                fab.show();
            } else {
                fab.hide();
            }
        });

        // --- 7. Load dữ liệu ban đầu ---
        productVM.setStatus("active");
        productVM.fetchCategories();
        productVM.reload();

        // --- 8. FAB Click ---
        fab.setOnClickListener(v -> {
            List<OrderItemDTO> cart = orderVM.getCurrentOrderItems().getValue();

            if (cart == null || cart.isEmpty()) {
                Toast.makeText(requireContext(), "Chưa chọn món nào", Toast.LENGTH_SHORT).show();
                return;
            }

            if (getActivity() instanceof OrderHostActivity) {
                ((OrderHostActivity) getActivity()).navigateToSummary();
            }
        });
    }

    private String currentCategoryIdFromUi() {
        String label = safeText(actCategory);
        if (label == null || label.isEmpty() || "Tất cả".equalsIgnoreCase(label)) return null;
        for (Category c : categoryData) if (label.equals(c.getName())) return c.getId();
        return null;
    }

    private static String safeText(TextView tv) {
        return tv.getText() == null ? null : tv.getText().toString().trim();
    }

    // SỬA: Bỏ 2 hàm onQuantityChanged và getQuantity vì Fragment không còn implements interfaces của Adapter nữa
}
