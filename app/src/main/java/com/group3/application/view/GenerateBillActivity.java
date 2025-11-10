package com.group3.application.view;

import static com.group3.application.common.util.Resource.Status.LOADING;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.group3.application.R;
import com.group3.application.common.util.Resource;
import com.group3.application.model.dto.BillCalculationResponse;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.CustomerSearchResponse;
import com.group3.application.view.activity.ConfirmPaymentActivity;
import com.group3.application.viewmodel.GenerateBillViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Activity for Use Case UC-0301: Generate Bill
 */
public class GenerateBillActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "ORDER_ID";
    // ViewModel
    private GenerateBillViewModel viewModel;

    // UI Components
    private TextView tvOrderId, tvSubtotal;
    private TextInputEditText etCustomerPhone, etVoucherCode, etPointsToRedeem;
    private Button btnSearchCustomer, btnCalculate, btnGenerateBill;
    private LinearLayout layoutCustomerInfo;
    private TextView tvCustomerName, tvCustomerPoints, tvPointsDiscount;
    private MaterialCardView cardPointsRedemption, cardCalculationResult;
    private TextView tvCalcSubtotal, tvCalcDiscount, tvCalcFinalTotal;
    private ProgressBar progressCustomer, progressGenerate;

    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);

        viewModel = new ViewModelProvider(this).get(GenerateBillViewModel.class);

        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        initViews();
        setupListeners();
        observeViewModel();

        String orderIdString = getIntent().getStringExtra("ORDER_ID");
        if (orderIdString != null) {
            UUID orderId = UUID.fromString(orderIdString);
            viewModel.setOrderId(orderId);
            tvOrderId.setText("Order ID: " + orderId.toString());
        }

        // Get subtotal from intent (if available)
        double subtotal = getIntent().getDoubleExtra("SUBTOTAL", 0.0);
        if (subtotal > 0) {
            tvSubtotal.setText("Subtotal: " + formatCurrency(BigDecimal.valueOf(subtotal)));
        }
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvSubtotal = findViewById(R.id.tvSubtotal);

        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etVoucherCode = findViewById(R.id.etVoucherCode);
        etPointsToRedeem = findViewById(R.id.etPointsToRedeem);

        btnSearchCustomer = findViewById(R.id.btnSearchCustomer);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnGenerateBill = findViewById(R.id.btnGenerateBill);

        layoutCustomerInfo = findViewById(R.id.layoutCustomerInfo);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvCustomerPoints = findViewById(R.id.tvCustomerPoints);
        tvPointsDiscount = findViewById(R.id.tvPointsDiscount);

        cardPointsRedemption = findViewById(R.id.cardPointsRedemption);
        cardCalculationResult = findViewById(R.id.cardCalculationResult);

        tvCalcSubtotal = findViewById(R.id.tvCalcSubtotal);
        tvCalcDiscount = findViewById(R.id.tvCalcDiscount);
        tvCalcFinalTotal = findViewById(R.id.tvCalcFinalTotal);

        progressCustomer = findViewById(R.id.progressCustomer);
        progressGenerate = findViewById(R.id.progressGenerate);
    }

    private void setupListeners() {
        // UC-0301: Step 3 - Search Customer
        btnSearchCustomer.setOnClickListener(v -> searchCustomer());

        // Points input listener
        etPointsToRedeem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePointsDiscount();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // UC-0301: Step 7 - Calculate Bill
        btnCalculate.setOnClickListener(v -> calculateBill());

        // UC-0301: Step 8 - Generate Bill
        btnGenerateBill.setOnClickListener(v -> generateBill());
    }

    private void observeViewModel() {
        // Observe customer search result (Step 4)
        viewModel.getCustomerSearchResult().observe(this, resource ->
            handleCustomerSearchResult(resource)
        );

        // Observe add member result (AT1)
        viewModel.getAddMemberResult().observe(this, resource ->
            handleAddMemberResult(resource)
        );

        // Observe calculation result (Step 7)
        viewModel.getCalculateResult().observe(this, resource ->
            handleCalculationResult(resource)
        );

        // Observe generate bill result (Step 8-10)
        viewModel.getGenerateBillResult().observe(this, resource ->
            handleGenerateBillResult(resource)
        );

        // Observe customer data
        viewModel.getCustomerData().observe(this, customer ->
            displayCustomerInfo(customer)
        );
    }

    // ========== UC-0301: Step 3-4 - Search Customer ==========
    private void searchCustomer() {
        String phone = etCustomerPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            etCustomerPhone.setError("Please enter phone number");
            return;
        }

        viewModel.searchCustomer(phone);
    }

    private void handleCustomerSearchResult(Resource<CustomerSearchResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                progressCustomer.setVisibility(View.VISIBLE);
                btnSearchCustomer.setEnabled(false);
                break;

            case SUCCESS:
                progressCustomer.setVisibility(View.GONE);
                btnSearchCustomer.setEnabled(true);
                if (resource.data != null) {
                    Toast.makeText(this, "Customer found!", Toast.LENGTH_SHORT).show();
                }
                break;

            case ERROR:
                progressCustomer.setVisibility(View.GONE);
                btnSearchCustomer.setEnabled(true);
                // AT1: Customer Not Found
                showAddCustomerDialog(etCustomerPhone.getText().toString().trim());
                break;
        }
    }

    // ========== AT1 - Add New Loyalty Member ==========
    private void showAddCustomerDialog(String phone) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Customer Not Found")
            .setMessage("Customer not found. Would you like to register a new loyalty member?")
            .setPositiveButton("Yes", (dialog, which) -> showAddMemberForm(phone))
            .setNegativeButton("No", (dialog, which) -> {
                Toast.makeText(this, "Continue without customer info", Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private void showAddMemberForm(String phone) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_member, null);

        EditText etName = dialogView.findViewById(R.id.etMemberName);
        EditText etEmail = dialogView.findViewById(R.id.etMemberEmail);
        TextView tvPhone = dialogView.findViewById(R.id.tvMemberPhone);

        tvPhone.setText(phone);

        new MaterialAlertDialogBuilder(this)
            .setTitle("Add New Member")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewModel.addNewMember(name, phone, email);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void handleAddMemberResult(Resource<CustomerSearchResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                Toast.makeText(this, "Creating member...", Toast.LENGTH_SHORT).show();
                break;

            case SUCCESS:
                Toast.makeText(this, "Member created successfully!", Toast.LENGTH_SHORT).show();
                break;

            case ERROR:
                Toast.makeText(this, "Failed to create member: " + resource.message,
                    Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // ========== Display Customer Info ==========
    private void displayCustomerInfo(CustomerSearchResponse customer) {
        if (customer == null) {
            layoutCustomerInfo.setVisibility(View.GONE);
            cardPointsRedemption.setVisibility(View.GONE);
            return;
        }

        layoutCustomerInfo.setVisibility(View.VISIBLE);
        tvCustomerName.setText(customer.getName());
        tvCustomerPoints.setText("Available Points: " + customer.getAvailablePoints());

        // Show points redemption section if customer has points
        if (customer.getAvailablePoints() > 0) {
            cardPointsRedemption.setVisibility(View.VISIBLE);
        }
    }

    // ========== Points Discount Calculation ==========
    private void updatePointsDiscount() {
        String pointsStr = etPointsToRedeem.getText().toString().trim();
        if (TextUtils.isEmpty(pointsStr)) {
            tvPointsDiscount.setText("Discount: 0 VND");
            return;
        }

        try {
            int points = Integer.parseInt(pointsStr);

            // AT3: Validate points
            if (!viewModel.validatePoints(points)) {
                etPointsToRedeem.setError("Insufficient points");
                tvPointsDiscount.setText("Discount: Invalid");
                return;
            }

            BigDecimal discount = viewModel.calculatePointsDiscount(points);
            tvPointsDiscount.setText("Discount: " + formatCurrency(discount));
            viewModel.setPointsToRedeem(points);
        } catch (NumberFormatException e) {
            tvPointsDiscount.setText("Discount: 0 VND");
        }
    }

    // ========== UC-0301: Step 5-7 - Apply Voucher & Calculate ==========
    private void calculateBill() {
        // Get voucher code
        String voucherCode = etVoucherCode.getText().toString().trim();
        if (!TextUtils.isEmpty(voucherCode)) {
            viewModel.setVoucherCode(voucherCode);
        }

        // Get points to redeem
        String pointsStr = etPointsToRedeem.getText().toString().trim();
        if (!TextUtils.isEmpty(pointsStr)) {
            try {
                int points = Integer.parseInt(pointsStr);
                if (!viewModel.validatePoints(points)) {
                    Toast.makeText(this, "AT3: Insufficient points", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.setPointsToRedeem(points);
            } catch (NumberFormatException e) {
                viewModel.setPointsToRedeem(0);
            }
        }

        viewModel.calculateBill();
    }

    private void handleCalculationResult(Resource<BillCalculationResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                btnCalculate.setEnabled(false);
                btnCalculate.setText("Calculating...");
                break;

            case SUCCESS:
                btnCalculate.setEnabled(true);
                btnCalculate.setText("Calculate Total");

                if (resource.data != null) {
                    displayCalculationResult(resource.data);
                    btnGenerateBill.setEnabled(true);
                }
                break;

            case ERROR:
                btnCalculate.setEnabled(true);
                btnCalculate.setText("Calculate Total");

                // AT2: Invalid or Expired Voucher
                if (resource.message != null && resource.message.contains("voucher")) {
                    new MaterialAlertDialogBuilder(this)
                        .setTitle("Invalid Voucher")
                        .setMessage("AT2: " + resource.message)
                        .setPositiveButton("OK", null)
                        .show();
                } else {
                    Toast.makeText(this, "Calculation failed: " + resource.message,
                        Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void displayCalculationResult(BillCalculationResponse calculation) {
        cardCalculationResult.setVisibility(View.VISIBLE);

        tvCalcSubtotal.setText(formatCurrency(calculation.getSubtotal()));
        tvCalcDiscount.setText("-" + formatCurrency(calculation.getDiscount()));
        tvCalcFinalTotal.setText(formatCurrency(calculation.getFinalTotal()));
    }

    // ========== UC-0301: Step 8-10 - Generate Bill ==========
    private void generateBill() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Generate Bill")
            .setMessage("Are you sure you want to generate this bill?")
            .setPositiveButton("Generate", (dialog, which) -> {
                viewModel.generateBill();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void handleGenerateBillResult(Resource<BillResponse> resource) {
        if (resource == null) return;

        switch (resource.status) {
            case LOADING:
                progressGenerate.setVisibility(View.VISIBLE);
                btnGenerateBill.setEnabled(false);
                break;

            case SUCCESS:
                progressGenerate.setVisibility(View.GONE);

                if (resource.data != null) {
                    showSuccessDialog(resource.data);
                }
                break;

            case ERROR:
                progressGenerate.setVisibility(View.GONE);
                btnGenerateBill.setEnabled(true);

                Toast.makeText(this, "Failed to generate bill: " + resource.message,
                    Toast.LENGTH_LONG).show();
                break;
        }
    }

    // ========== Step 10 - Success & Navigate ==========
    private void showSuccessDialog(BillResponse bill) {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Bill Generated Successfully")
            .setMessage("Bill ID: " + bill.getBillId() + "\n" +
                "Status: " + bill.getPaymentStatus() + "\n" +
                "Final Total: " + formatCurrency(bill.getFinalTotal()))
            .setPositiveButton("Go to Payment", (dialog, which) -> {
                // Navigate to UC-0205: Confirm Payment
                Intent intent = new Intent(this, ConfirmPaymentActivity.class);
                intent.putExtra("BILL_ID", bill.getBillId().toString());
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Close", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }

    // ========== Helper Methods ==========
    private String formatCurrency(BigDecimal amount) {
        return currencyFormat.format(amount) + " VND";
    }
}
