package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.dto.BillResponse;
import com.group3.application.model.dto.BillSummaryDTO;
import com.group3.application.model.entity.Order;
import com.group3.application.view.BillListActivity;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BillSummaryAdapter extends RecyclerView.Adapter<BillSummaryAdapter.BillSummaryViewHolder> {

    private List<BillSummaryDTO> bills = new ArrayList<>();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DecimalFormat currencyFormatter = new DecimalFormat("#,###,### đ");

    private final OnBillClickListener clickListener;

    public interface OnBillClickListener {
        void onBillClick(BillSummaryDTO bill);
    }

    public BillSummaryAdapter(BillSummaryAdapter.OnBillClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setBills(List<BillSummaryDTO> bills) {
        this.bills = bills;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_summary, parent, false);
        return new BillSummaryViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BillSummaryViewHolder holder, int position) {
        BillSummaryDTO bill = bills.get(position);
        holder.bind(bill);
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    class BillSummaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvIssuedTime;
        private final TextView tvCashierName;
        private final TextView tvFinalAmount;
        private final TextView tvPaymentStatus;
        private final TextView tvPaymentMethod;

        private final OnBillClickListener listener;

        public BillSummaryViewHolder(@NonNull View itemView, OnBillClickListener listener) {
            super(itemView);
            this.listener = listener;
            tvIssuedTime = itemView.findViewById(R.id.tv_issued_time);
            tvCashierName = itemView.findViewById(R.id.tv_cashier_name);
            tvFinalAmount = itemView.findViewById(R.id.tv_final_amount);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && this.listener != null) {
                    this.listener.onBillClick((BillSummaryDTO) itemView.getTag());
                }
            });
        }


        public void bind(BillSummaryDTO bill) {
            itemView.setTag(bill);
            tvIssuedTime.setText("Thời gian: " + bill.getIssuedTime().format(timeFormatter));
            tvCashierName.setText("Thu ngân: " + bill.getCashierName());
            tvFinalAmount.setText("Tổng tiền: " + currencyFormatter.format(bill.getFinalAmount()));
            tvPaymentStatus.setText("Trạng thái: " + bill.getPaymentStatus());
            tvPaymentMethod.setText("Phương thức: " + bill.getPaymentMethod());
        }
    }
}
