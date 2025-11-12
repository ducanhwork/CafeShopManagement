package com.group3.application.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;
import com.group3.application.R;
import com.group3.application.common.util.DisplayMapperUtil;
import com.group3.application.model.dto.BillDetailResponse;
import com.group3.application.model.dto.BillItemDTO;
import com.group3.application.model.dto.BillPaymentDTO;
import com.group3.application.viewmodel.BillDetailViewModel;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class BillDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BILL_ID = "extra_bill_id";

    private BillDetailViewModel viewModel;

    private TextView tvBillId, tvIssuedTime, tvTableCashier;
    private TextView tvCustomerName, tvStatus, tvSubtotal, tvDiscount, tvFinalTotal, tvPaymentMethodTitle;
    private TextView tvVoucher, tvPoints;
    private Spinner spinnerPaymentMethod;
    private Button btnConfirm, btnCancel;
    private RecyclerView rvBillItems;
    private LinearLayout llPaymentsContainer;

    private final NumberFormat moneyFormatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);

        moneyFormatter.setGroupingUsed(true);
        moneyFormatter.setMaximumFractionDigits(0);

        initViews();
        setupToolbar();

        viewModel = new ViewModelProvider(this).get(BillDetailViewModel.class);

        setupListeners();
        observeViewModel();

        String billId = getIntent().getStringExtra(EXTRA_BILL_ID);
        if (billId != null) {
            viewModel.loadBillDetails(billId);
        } else {
            Toast.makeText(this, "Không tìm thấy ID hóa đơn", Toast.LENGTH_SHORT).show();
            finishWithOriginCheck();
        }
    }

    private void initViews() {
        tvBillId = findViewById(R.id.tv_bill_id);
        tvIssuedTime = findViewById(R.id.tv_bill_issued_time);
        tvTableCashier = findViewById(R.id.tv_bill_table_cashier);

        tvCustomerName = findViewById(R.id.tv_bill_customer_name);
        tvStatus = findViewById(R.id.tv_bill_status);
        tvSubtotal = findViewById(R.id.tv_bill_subtotal);
        tvDiscount = findViewById(R.id.tv_bill_discount);
        tvFinalTotal = findViewById(R.id.tv_bill_final_total);
        tvVoucher = findViewById(R.id.tv_bill_voucher);
        tvPoints = findViewById(R.id.tv_bill_points);
        tvPaymentMethodTitle = findViewById(R.id.tv_payment_method_title);
        spinnerPaymentMethod = findViewById(R.id.spinner_payment_method);
        btnConfirm = findViewById(R.id.btn_confirm_payment);
        btnCancel = findViewById(R.id.btn_cancel_payment);

        rvBillItems = findViewById(R.id.rv_bill_items);
        llPaymentsContainer = findViewById(R.id.ll_payments_container);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.payment_methods_display, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        rvBillItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_bill_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finishWithOriginCheck());
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> finishWithOriginCheck());
        btnConfirm.setOnClickListener(v -> {
            int selectedPosition = spinnerPaymentMethod.getSelectedItemPosition();
            String[] apiValues = getResources().getStringArray(R.array.payment_methods_values);
            if (selectedPosition < 0 || selectedPosition >= apiValues.length) {
                Toast.makeText(this, "Chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            String apiMethod = apiValues[selectedPosition];
            viewModel.confirmPayment(apiMethod);
        });
    }

    private void observeViewModel() {
        viewModel.getBillDetails().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    updateUi(resource.data);
                    break;
                case ERROR:
                    Toast.makeText(this, "Lỗi: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getPaymentResult().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.status) {
                case LOADING:
                    btnConfirm.setEnabled(false);
                    break;
                case SUCCESS:
                    btnConfirm.setEnabled(true);
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    // refresh details (optional) or finish activity
                    viewModel.loadBillDetails(resource.data != null ? resource.data.getBillId().toString() : null);
                    break;
                case ERROR:
                    btnConfirm.setEnabled(true);
                    Toast.makeText(this, "Lỗi thanh toán: " + resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "-";
        return moneyFormatter.format(amount) + " đ";
    }

    private String formatDateTime(LocalDateTime ldt) {
        if (ldt == null) return "-";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return ldt.format(fmt);
    }

    private void updateUi(BillDetailResponse bill) {
        if (bill == null) return;

        tvBillId.setText(bill.getBillId() != null ? "Mã hóa đơn: " + bill.getBillId().toString() : "Mã hóa đơn: -");
        tvIssuedTime.setText("Thời gian: " + formatDateTime(bill.getIssuedTime()));
        String table = bill.getTableName() != null ? bill.getTableName() : "-";
        String cashier = bill.getCashierName() != null ? bill.getCashierName() : "-";
        tvTableCashier.setText(String.format("Bàn: %s  •  Thu ngân: %s", table, cashier));

        String customerName = (bill.getCustomerInfo() != null && bill.getCustomerInfo().getName() != null
            && !bill.getCustomerInfo().getName().isEmpty())
            ? bill.getCustomerInfo().getName()
            : "Khách lẻ";
        tvCustomerName.setText("Tên khách hàng: " + customerName);

        String displayStatus = DisplayMapperUtil.mapPaymentStatus(bill.getPaymentStatus());
        tvStatus.setText("Trạng thái: " + displayStatus);

        tvSubtotal.setText("Tạm tính: " + formatMoney(bill.getSubtotal()));
        tvDiscount.setText("Giảm giá: " + (bill.getTotalDiscount() != null ? "-" + formatMoney(bill.getTotalDiscount()) : "-"));
        tvFinalTotal.setText("Tổng cộng: " + formatMoney(bill.getFinalAmount()));

        tvVoucher.setText("Voucher: " + (bill.getVoucherCode() != null ? bill.getVoucherCode() : "-"));
        tvPoints.setText("Điểm quy đổi: " + bill.getPointsRedeemed());

        List<BillItemDTO> items = bill.getItems() != null ? bill.getItems() : Collections.emptyList();
        if (rvBillItems.getAdapter() == null) {
            rvBillItems.setAdapter(new BillItemAdapter(items, this::formatMoney));
        } else {
            ((BillItemAdapter) rvBillItems.getAdapter()).setItems(items);
        }

        llPaymentsContainer.removeAllViews();
        List<BillPaymentDTO> payments = bill.getPayments();

        if (payments != null && !payments.isEmpty()) {
            for (BillPaymentDTO p : payments) {

                MaterialTextView tv = new MaterialTextView(this, null,
                    R.style.TextAppearance_App_BodyMedium);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                tv.setLayoutParams(lp);
                String rawMethod = p.getPaymentMethod();
                String displayMethod = "Không rõ";

                if (rawMethod != null) {
                    displayMethod = DisplayMapperUtil.mapPaymentMethod(this, rawMethod);
                }

                String amount = formatMoney(p.getAmount());
                tv.setText(displayMethod + ": " + amount);

                int paddingInDp = 4;
                float density = getResources().getDisplayMetrics().density;
                int paddingInPx = (int)(paddingInDp * density);
                tv.setPadding(0, paddingInPx, 0, paddingInPx);

                llPaymentsContainer.addView(tv);
            }
        } else {
            MaterialTextView tv = new MaterialTextView(this, null,
                R.style.TextAppearance_App_BodyMedium);

            tv.setText("Chưa có thanh toán nào");
            llPaymentsContainer.addView(tv);
        }

        boolean isPaid = "PAID".equalsIgnoreCase(bill.getPaymentStatus());
        int visibility = isPaid ? View.GONE : View.VISIBLE;

        tvPaymentMethodTitle.setVisibility(visibility);
        spinnerPaymentMethod.setVisibility(visibility);
        btnConfirm.setVisibility(visibility);
    }

    public static class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.VH> {
        private List<BillItemDTO> items;
        private final Function<BigDecimal, String> moneyFormatter;

        public BillItemAdapter(List<BillItemDTO> items, Function<BigDecimal, String> moneyFormatter) {
            this.items = items != null ? items : Collections.emptyList();
            this.moneyFormatter = moneyFormatter;
        }

        public void setItems(List<BillItemDTO> newItems) {
            this.items = newItems != null ? newItems : Collections.emptyList();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill_line, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            BillItemDTO it = items.get(position);
            holder.tvName.setText(it.getProductName() != null ? it.getProductName() : "-");
            holder.tvQty.setText("x" + it.getQuantity());
            holder.tvPrice.setText(moneyFormatter.apply(it.getPriceAtOrder()));
            holder.tvLineTotal.setText(moneyFormatter.apply(it.getLineTotal()));
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvQty, tvPrice, tvLineTotal;
            VH(@NonNull android.view.View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.item_name);
                tvQty = itemView.findViewById(R.id.item_qty);
                tvPrice = itemView.findViewById(R.id.item_price);
                tvLineTotal = itemView.findViewById(R.id.item_line_total);
            }
        }
    }

    private void finishWithOriginCheck() {
        String origin = getIntent().getStringExtra("origin");
        if ("list".equals(origin)) {
            finish();
        } else {
            Intent intent = new Intent(this, OrderListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

}
