package com.group3.application.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.dto.RevenueDetailDTO;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RevenueReportAdapter extends RecyclerView.Adapter<RevenueReportAdapter.ReportViewHolder> {

    private final List<RevenueDetailDTO> details = new ArrayList<>();
    private final DecimalFormat formatter = new DecimalFormat("#,###,###");
    private String filterType = "Day";

    private final DateTimeFormatter dayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
    private final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_revenue_detail, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        RevenueDetailDTO item = details.get(position);
        holder.tvDate.setText(formatDate(item.getDate()));
        holder.tvRevenue.setText(formatCurrency(item.getTotalRevenue()));
        holder.tvBillCount.setText(String.valueOf(item.getBillCount()));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public void setData(List<RevenueDetailDTO> newDetails) {
        this.details.clear();
        if (newDetails != null) {
            this.details.addAll(newDetails);
        }
        notifyDataSetChanged();
    }

    public void setFilterType(String filterType) {
        if (filterType != null) {
            this.filterType = filterType;
            notifyDataSetChanged();
        }
    }

    private String formatDate(String timeLabel) {
        if (timeLabel == null || timeLabel.isEmpty()) {
            return "";
        }

        switch (filterType.toUpperCase()) {
            case "WEEK":
                if (timeLabel.length() >= 6) {
                    try {
                        int year = Integer.parseInt(timeLabel.substring(0, 4));
                        int week = Integer.parseInt(timeLabel.substring(4));
                        
                        WeekFields weekFields = WeekFields.of(Locale.GERMANY);
                        LocalDate firstDayOfWeek = LocalDate.of(year, 2, 1)
                                .with(weekFields.weekOfWeekBasedYear(), week)
                                .with(DayOfWeek.MONDAY);
                        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);
                        return firstDayOfWeek.format(dayMonthFormatter) + " - " + lastDayOfWeek.format(dayMonthFormatter);
                    } catch (Exception e) {
                        return timeLabel;
                    }
                }
                return timeLabel;

            case "MONTH":
                try {
                    YearMonth ym = YearMonth.parse(timeLabel);
                    return ym.format(monthYearFormatter);
                } catch (DateTimeParseException e) {
                    return timeLabel;
                }

            case "DAY":
            default:
                try {
                    LocalDate date = LocalDate.parse(timeLabel);
                    return date.format(fullDateFormatter);
                } catch (DateTimeParseException e) {
                    return timeLabel;
                }
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return formatter.format(amount) + " đ";
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRevenue, tvBillCount;
        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.item_tv_date);
            tvRevenue = itemView.findViewById(R.id.item_tv_revenue);
            tvBillCount = itemView.findViewById(R.id.item_tv_bill_count);
        }
    }
}