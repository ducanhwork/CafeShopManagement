package com.group3.application.view.fragment;

import android.app.Activity;
import android.content.Intent;
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
import com.group3.application.model.dto.APIResult;
import com.group3.application.model.dto.OrderItemDTO;
import com.group3.application.model.entity.Category;
import com.group3.application.view.OrderHostActivity;
import com.group3.application.view.adapter.ProductAdapter;
import com.group3.application.viewmodel.OrderViewModel;
import com.group3.application.viewmodel.ProductListViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProductListFragment extends Fragment {

    private ProductListViewModel productVM;
    private OrderViewModel orderVM;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productVM = new ViewModelProvider(this).get(ProductListViewModel.class);
        orderVM = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI(view);
        setupViewModel();
        setupAdapter();
        setupListeners();
        observeData();

        loadInitialData();
    }

    private void setupUI(View view) {
        MaterialToolbar tb = view.findViewById(R.id.toolbar);
        tvSub = view.findViewById(R.id.tvSubtitle);
        swipe = view.findViewById(R.id.swipe);
        rv = view.findViewById(R.id.rvProducts);
        edtSearch = view.findViewById(R.id.edtSearch);
        actCategory = view.findViewById(R.id.actCategory);
        fab = view.findViewById(R.id.fabAdd);
        View appBar = view.findViewById(R.id.appBar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(tb);
        tb.setTitle(orderVM.isEditMode() ? "Edit Items" : "Create Order");
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        tb.setNavigationOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        ViewCompat.setOnApplyWindowInsetsListener(appBar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, top, 0, 0);
            return insets;
        });

        String tableName = orderVM.getTableNames();
        tvSub.setText("For table(s): " + (tableName == null ? "" : tableName));
    }

    private void setupViewModel() {
        productVM = new ViewModelProvider(this).get(ProductListViewModel.class);
        orderVM = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
    }

    private void setupAdapter() {
        adapter = new ProductAdapter((product, newQuantity) -> orderVM.addOrUpdateItem(product, newQuantity));
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setAdapter(adapter);

        catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        actCategory.setAdapter(catAdapter);
    }

    private void setupListeners() {
        swipe.setOnRefreshListener(productVM::reload);
        actCategory.setOnClickListener(v -> actCategory.showDropDown());
        actCategory.setOnItemClickListener((parent, v, pos, id) -> {
            String selectedCategoryId = null;
            if (pos > 0 && pos - 1 < categoryData.size()) {
                selectedCategoryId = categoryData.get(pos - 1).getId();
            }
            productVM.applyFilter(selectedCategoryId, safeText(edtSearch));
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

        // --- SỬA LẠI HOÀN TOÀN LOGIC NÚT FAB ---
        fab.setOnClickListener(v -> {
            if (orderVM.isEditMode()) {
                // --- CHẾ ĐỘ SỬA ---

                // 1. Lấy danh sách món ăn đã cập nhật từ ViewModel
                List<OrderItemDTO> updatedItems = orderVM.getCurrentOrderItems().getValue();

                // 2. Tạo Intent kết quả
                Intent resultIntent = new Intent();

                // 3. Đặt "extra" VỚI ĐÚNG TÊN MÀ OrderDetailActivity đang chờ ("updatedItems")
                resultIntent.putExtra("updatedItems", (Serializable) updatedItems);

                // 4. Đặt cờ RESULT_OK và đóng Activity
                requireActivity().setResult(Activity.RESULT_OK, resultIntent);
                requireActivity().finish();

            } else {
                // --- CHẾ ĐỘ TẠO MỚI (Code cũ của bạn - đã đúng) ---
                if (getActivity() instanceof OrderHostActivity) {
                    ((OrderHostActivity) getActivity()).navigateToSummary();
                }
            }
        });
    }

    private void observeData() {
        productVM.getProducts().observe(getViewLifecycleOwner(), adapter::setProductList);
        productVM.getLoadingProducts().observe(getViewLifecycleOwner(), isLoading -> swipe.setRefreshing(Boolean.TRUE.equals(isLoading)));
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

        orderVM.getCurrentOrderItems().observe(getViewLifecycleOwner(), adapter::setOrderItems);

        orderVM.getTotalAmount().observe(getViewLifecycleOwner(), total -> updateFabButton());

        // SỬA: Xóa logic xử lý thành công cho chế độ sửa, chỉ giữ lại xử lý lỗi
        orderVM.orderSubmissionResult.observe(getViewLifecycleOwner(), event -> {
            APIResult result = event.getContentIfNotHandled();
            if (result != null && !result.isSuccess()) {
                Toast.makeText(getContext(), "Error: " + result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFabButton() {
        if (orderVM.isEditMode()) {
            fab.setText("Save");
            fab.show();
        } else {
            double total = orderVM.getTotalAmount().getValue() != null ? orderVM.getTotalAmount().getValue() : 0;
            int totalItemCount = 0;
            List<OrderItemDTO> items = orderVM.getCurrentOrderItems().getValue();
            if (items != null) {
                for (OrderItemDTO item : items) {
                    totalItemCount += item.quantity;
                }
            }

            if (totalItemCount > 0) {
                fab.setText(String.format(Locale.getDefault(), "Total: %,.0f đ (%d items)", total, totalItemCount));
                fab.show();
            } else {
                fab.hide();
            }
        }
    }

    private void loadInitialData() {
        productVM.setStatus("active");
        productVM.fetchCategories();
        productVM.reload();
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
}
