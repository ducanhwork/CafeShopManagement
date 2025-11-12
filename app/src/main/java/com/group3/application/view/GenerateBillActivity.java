package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.common.util.Resource;
import com.group3.application.common.validator.LoyaltyValidator;
import com.group3.application.common.validator.ValidationResult;
import com.group3.application.model.bean.UpdateLoyaltyMemberRequest;
import com.group3.application.model.dto.BillCalculationResponse;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.CustomerSearchResponse;
import com.group3.application.model.entity.Order;
import com.group3.application.view.adapter.OrderDetailItemAdapter;
import com.group3.application.viewmodel.GenerateBillViewModel;
import com.group3.application.viewmodel.OrderDetailViewModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class GenerateBillActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private GenerateBillViewModel generateBillViewModel;
    private OrderDetailViewModel orderDetailViewModel;

    private OrderDetailItemAdapter adapter;
    private RecyclerView rvBillItems;
    private TextView tvBillNote, tvSubtotal, tvCustomerName, tvCustomerPoints, tvDiscountAmount, tvTotalAmount, tvCustomerNotFound;
    private TextInputEditText edtCustomerPhone, edtVoucher;
    private Button btnSearchCustomer, btnApplyPoints, btnAddCustomer, btnApplyVoucher, btnCancelBill, btnGenerateBill;
    private LinearLayout layoutCustomerInfo;

    private final DecimalFormat formatter = new DecimalFormat("#,###,### đ");

    private int currentAppliedPoints = 0;

    private String phone = "";
    private String currentAppliedVoucher = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);

        initViews();
        setupToolbar();

        generateBillViewModel = new ViewModelProvider(this).get(GenerateBillViewModel.class);
        orderDetailViewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);

        setupListeners();
        observeViewModel();

        String orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        if (orderId != null) {
            orderDetailViewModel.fetchOrderDetails(orderId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        rvBillItems = findViewById(R.id.rv_bill_items);
        tvBillNote = findViewById(R.id.tv_bill_note);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        edtCustomerPhone = findViewById(R.id.edt_customer_phone);
        btnSearchCustomer = findViewById(R.id.btn_search_customer);
        layoutCustomerInfo = findViewById(R.id.layout_customer_info);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerPoints = findViewById(R.id.tv_customer_points);
        btnApplyPoints = findViewById(R.id.btn_redeem_points);
        btnAddCustomer = findViewById(R.id.btn_add_customer);
        edtVoucher = findViewById(R.id.edt_voucher);
        btnApplyVoucher = findViewById(R.id.btn_apply_voucher);
        tvDiscountAmount = findViewById(R.id.tv_discount_amount);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnCancelBill = findViewById(R.id.btn_cancel_bill);
        btnGenerateBill = findViewById(R.id.btn_generate_bill);
        tvCustomerNotFound = findViewById(R.id.tv_customer_not_found);

        rvBillItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderDetailItemAdapter();
        rvBillItems.setAdapter(adapter);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_generate_bill);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnCancelBill.setOnClickListener(v -> finish());

        btnSearchCustomer.setOnClickListener(v -> {
            String inputPhone = edtCustomerPhone.getText().toString().trim();
            ValidationResult result = LoyaltyValidator.validatePhone(inputPhone);

            if (!result.isValid && "phone".equals(result.errorField)) {
                Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show();
                return;
            }
            phone = inputPhone;
            generateBillViewModel.searchCustomer(inputPhone);
        });

        btnApplyVoucher.setOnClickListener(v -> {
            String voucherCode = edtVoucher.getText().toString().trim();
            currentAppliedVoucher = voucherCode;
            generateBillViewModel.calculateBill(phone, currentAppliedVoucher, currentAppliedPoints);
        });

        btnAddCustomer.setOnClickListener(v -> showAddCustomerDialog());

        btnApplyPoints.setOnClickListener(v -> showApplyPointsDialog());

        btnGenerateBill.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận tạo hóa đơn")
                .setMessage("Bạn có chắc chắn muốn tạo hóa đơn này không?")
                .setPositiveButton("Tạo", (dialog, which) -> generateBillViewModel.generateBill(currentAppliedVoucher, currentAppliedPoints))
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    private void observeViewModel() {
        orderDetailViewModel.order.observe(this, order -> {
            if (order != null) {
                updateUi(order);
                generateBillViewModel.setOrder(order);
            }
        });

        generateBillViewModel.getCustomerSearchResult().observe(this, this::handleCustomerSearchResult);
        generateBillViewModel.getAddCustomerResult().observe(this, this::handleAddCustomerResult);
        generateBillViewModel.getCalculateResult().observe(this, this::handleCalculationResult);
        generateBillViewModel.getGenerateBillResult().observe(this, this::handleGenerateBillResult);
    }

    private void updateUi(Order order) {
        if (order == null) return;

        adapter.setItems(order.getItems());
        if(order.getNote() != null && !order.getNote().isEmpty()){
            tvBillNote.setText(order.getNote());
        } else {
            tvBillNote.setText("Không có ghi chú.");
        }

        tvSubtotal.setText("Tạm tính: " + formatter.format(order.getTotalAmount()));
        tvTotalAmount.setText("Tổng cộng: " + formatter.format(order.getTotalAmount()));
        tvDiscountAmount.setVisibility(View.GONE);
    }

    private void showAddCustomerDialog() {
        final TextInputEditText nameInput = new TextInputEditText(this);
        nameInput.setHint("Tên khách hàng");

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (16 * getResources().getDisplayMetrics().density);
        lp.setMargins(margin, 0, margin, 0);
        nameInput.setLayoutParams(lp);
        container.addView(nameInput);

        phone = edtCustomerPhone.getText().toString().trim();

        new MaterialAlertDialogBuilder(this)
            .setTitle("Thêm khách hàng mới")
            .setMessage("Thêm khách hàng với SĐT: " + phone)
            .setView(container)
            .setPositiveButton("Thêm", (dialog, which) -> {
                String name = nameInput.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }
                generateBillViewModel.addNewCustomer(name, phone);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void showApplyPointsDialog() {
        Resource<CustomerSearchResponse> customerResource = generateBillViewModel.getCustomerSearchResult().getValue();
        if (customerResource == null || customerResource.data == null) {
            Toast.makeText(this, "Không có thông tin khách hàng để áp dụng điểm", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomerSearchResponse customer = customerResource.data;

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int margin = (int) (16 * getResources().getDisplayMetrics().density);

        final TextInputEditText pointsInput = new TextInputEditText(this);
        pointsInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        pointsInput.setHint("Số điểm sử dụng");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(margin, 0, margin, 0);
        pointsInput.setLayoutParams(lp);
        container.addView(pointsInput);

        TextView infoText = new TextView(this);
        infoText.setText("Điểm khả dụng: " + customer.getAvailablePoints() + "\n(1 điểm = 1.000đ)");
        infoText.setLayoutParams(lp);
        infoText.setPadding(margin, margin/2, margin, 0);
        container.addView(infoText);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Áp dụng điểm tích lũy")
            .setView(container)
            .setPositiveButton("Áp dụng", (dialog, which) -> {
                String pointsStr = pointsInput.getText().toString().trim();
                if (pointsStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập số điểm", Toast.LENGTH_SHORT).show();
                    return;
                }

                int pointsToUse = 0;
                try {
                    pointsToUse = Integer.parseInt(pointsStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số điểm không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pointsToUse > customer.getAvailablePoints()) {
                    Toast.makeText(this, "Số điểm vượt quá điểm khả dụng", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pointsToUse < 0) {
                    Toast.makeText(this, "Số điểm phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentAppliedPoints = pointsToUse;
                generateBillViewModel.calculateBill(phone, currentAppliedVoucher, currentAppliedPoints);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void handleCustomerSearchResult(Resource<CustomerSearchResponse> resource) {
        if (resource == null) return;
        switch (resource.status) {
            case LOADING:
                btnSearchCustomer.setEnabled(false);
                tvCustomerNotFound.setVisibility(View.GONE);
                break;
            case SUCCESS:
                btnSearchCustomer.setEnabled(true);
                CustomerSearchResponse customer = resource.data;
                if (customer != null) {
                    layoutCustomerInfo.setVisibility(View.VISIBLE);
                    btnAddCustomer.setVisibility(View.GONE);
                    tvCustomerNotFound.setVisibility(View.GONE);
                    tvCustomerName.setText("Tên: " + customer.getName());
                    tvCustomerPoints.setText("Điểm: " + customer.getAvailablePoints());
                    btnApplyPoints.setVisibility(customer.getAvailablePoints() > 0 ? View.VISIBLE : View.GONE);
                } else {
                    layoutCustomerInfo.setVisibility(View.GONE);
                    btnAddCustomer.setVisibility(View.VISIBLE);
                    tvCustomerNotFound.setVisibility(View.VISIBLE);
                    tvCustomerNotFound.setText("Khách hàng chưa có trong hệ thống. Vui lòng tạo mới.");
                    btnApplyPoints.setVisibility(View.GONE);
                }
                break;
            case ERROR:
                btnSearchCustomer.setEnabled(true);
                layoutCustomerInfo.setVisibility(View.GONE);
                btnAddCustomer.setVisibility(View.VISIBLE);
                tvCustomerNotFound.setVisibility(View.VISIBLE);
                tvCustomerNotFound.setText("Khách hàng chưa có trong hệ thống. Vui lòng tạo mới.");
                btnApplyPoints.setVisibility(View.GONE);
                break;
        }
    }

    private void handleAddCustomerResult(Resource<CustomerSearchResponse> resource) {
        if (resource == null) return;
        switch (resource.status) {
            case LOADING:
                break;
            case SUCCESS:
                Toast.makeText(this, "Thêm khách hàng thành công!", Toast.LENGTH_SHORT).show();
                phone = edtCustomerPhone.getText().toString().trim();
                if (!phone.isEmpty()) {
                    generateBillViewModel.searchCustomer(phone);
                }
                break;
            case ERROR:
                Toast.makeText(this, "Thêm khách hàng thất bại: " + resource.message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleCalculationResult(Resource<BillCalculationResponse> resource) {
        if (resource == null) return;
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            BillCalculationResponse calculation = resource.data;
            try {
                BigDecimal discount = calculation.getDiscount();
                BigDecimal finalTotal = calculation.getFinalTotal();

                if (discount.compareTo(BigDecimal.ZERO) > 0) {
                    tvDiscountAmount.setVisibility(View.VISIBLE);
                    tvDiscountAmount.setText("Giảm: -" + formatter.format(discount));
                } else {
                    tvDiscountAmount.setVisibility(View.GONE);
                }

                tvTotalAmount.setText("Tổng cộng: " + formatter.format(finalTotal));
                btnGenerateBill.setEnabled(true);

                Toast.makeText(this, "Áp dụng thành công!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi định dạng số từ server", Toast.LENGTH_SHORT).show();
                btnGenerateBill.setEnabled(false);
            }
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(this, "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
            currentAppliedPoints = 0;
            currentAppliedVoucher = "";
            tvDiscountAmount.setVisibility(View.GONE);
        }
    }

    private void handleGenerateBillResult(Resource<BillResponse> resource) {
        if (resource == null) return;
        switch (resource.status) {
            case LOADING:
                btnGenerateBill.setEnabled(false);
                break;
            case SUCCESS:
                btnGenerateBill.setEnabled(true);
                Toast.makeText(this, "Tạo hóa đơn thành công!", Toast.LENGTH_LONG).show();
                if (resource.data != null && resource.data.getBillId() != null) {
                    Intent intent = new Intent(this, BillDetailActivity.class);
                    intent.putExtra(BillDetailActivity.EXTRA_BILL_ID, resource.data.getBillId().toString());
                    intent.putExtra("origin", "generate");
                    startActivity(intent);
                    finish();
                }
                break;
            case ERROR:
                btnGenerateBill.setEnabled(true);
                Toast.makeText(this, "Tạo hóa đơn thất bại: " + resource.message, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
