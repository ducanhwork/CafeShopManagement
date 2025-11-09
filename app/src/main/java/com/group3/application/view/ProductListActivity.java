package com.group3.application.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.model.entity.Category;
import com.group3.application.model.entity.Product;
import com.group3.application.view.adapter.ProductAdapter;
import com.group3.application.viewmodel.ProductListViewModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> products = new ArrayList<>();

    private ProductAdapter adapter;

    private RecyclerView recyclerView;
    private ProductListViewModel viewModel;
    private TextInputEditText et_search_name;
    private Spinner spinner_category_filter;

    private String selectedCategory = "All";
    private ActivityResultLauncher<Intent> createProductLauncher;
    private Toolbar toolbar;
    private CircleImageView ivAvatar;
    private MaterialButton btnCreate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
        String keyword = et_search_name.getText().toString();
        setUpSpinner();
        trackOnChangeKeyword();
        viewModel.getProducts(keyword, selectedCategory == "All" ? null : selectedCategory);
        viewModel.products.observe(this, products -> {
            this.products.clear();
            this.products.addAll(products);
            adapter.setData(products);
            adapter.notifyDataSetChanged();
        });
        adapter = new ProductAdapter(products, updateProduct -> {
            Intent intent = new Intent(this, ProductUpdateActivity.class);
            intent.putExtra("productName", updateProduct.getName());
            intent.putExtra("productPrice", updateProduct.getPrice());
            intent.putExtra("productDescription", updateProduct.getDescription());
            intent.putExtra("productStatus", updateProduct.getStatus());
            intent.putExtra("productId", updateProduct.getId());
            intent.putExtra("productImage", updateProduct.getImageLink());
            startActivity(intent);
        }, viewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        createProductLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                viewModel.getProducts(keyword, selectedCategory == "All" ? null : selectedCategory);
            } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                Log.d("ProductListActivity", "Product creation cancelled.");
            }
        });
    }

    public void initView() {
        recyclerView = findViewById(R.id.recycler_view_products);
        et_search_name = findViewById(R.id.et_search_name);
        spinner_category_filter = findViewById(R.id.spinner_category_filter);
        toolbar = findViewById(R.id.toolbar);
        ivAvatar = findViewById(R.id.iv_avatar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ivAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
        btnCreate = findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductCreateActivity.class);
            createProductLauncher.launch(intent);
        });

    }


    public void setUpSpinner() {
        List<String> categoriesList = new ArrayList<>();
        categoriesList.add("All");
        viewModel.getCategories();
        viewModel.categories.observe(this, categories -> {
            for (Category category : categories) {
                categoriesList.add(category.getName());
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category_filter.setAdapter(adapter);
        spinner_category_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoriesList.get(position);
                String keyword = et_search_name.getText().toString();
                viewModel.getProducts(keyword, selectedCategory == "All" ? null : selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void trackOnChangeKeyword() {
        et_search_name.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 500;

            @Override
            public void afterTextChanged(Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            String keyword = s.toString().trim();
                            String category = "All".equals(selectedCategory) ? null : selectedCategory;
                            viewModel.getProducts(keyword.isEmpty() ? null : keyword, category);
                        });
                    }
                }, DELAY);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }
}
