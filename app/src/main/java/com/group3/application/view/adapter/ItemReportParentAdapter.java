package com.group3.application.view.adapter; // (Sửa package)

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group3.application.R;
import com.group3.application.model.dto.PeriodItemReportDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemReportParentAdapter extends RecyclerView.Adapter<ItemReportParentAdapter.ViewHolder> {

    private final List<PeriodItemReportDTO> dailyReports = new ArrayList<>();
    private final Context context;
    private String filterType = "Day"; // Bộ lọc mặc định

    // Thêm các formatter cần thiết
    private final DateTimeFormatter dayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
    private final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ItemReportParentAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_period_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeriodItemReportDTO dailyReport = dailyReports.get(position);

        holder.tvPeriodLabel.setText(formatPeriodLabel(dailyReport.getPeriodLabel()));
        ItemReportDetailAdapter topAdapter = new ItemReportDetailAdapter();
        holder.rvTopItems.setLayoutManager(new LinearLayoutManager(context));
        holder.rvTopItems.setAdapter(topAdapter);
        topAdapter.setData(dailyReport.getTopItems());

        ItemReportDetailAdapter bottomAdapter = new ItemReportDetailAdapter();
        holder.rvBottomItems.setLayoutManager(new LinearLayoutManager(context));
        holder.rvBottomItems.setAdapter(bottomAdapter);
        bottomAdapter.setData(dailyReport.getBottomItems());
    }

    @Override
    public int getItemCount() {
        return dailyReports.size();
    }

    public void setData(List<PeriodItemReportDTO> newReports) {
        this.dailyReports.clear();
        if (newReports != null) {
            this.dailyReports.addAll(newReports);
        }
        notifyDataSetChanged();
    }

    public void setFilterType(String filterType) {
        if (filterType != null) {
            this.filterType = filterType;
            notifyDataSetChanged();
        }
    }

    private String formatPeriodLabel(String periodLabel) {
        if (periodLabel == null || periodLabel.isEmpty()) {
            return "";
        }
        switch (filterType.toUpperCase()) {
            case "WEEK":
                if (periodLabel.length() >= 6) {
                    try {
                        int year = Integer.parseInt(periodLabel.substring(0, 4));
                        int week = Integer.parseInt(periodLabel.substring(4));

                        WeekFields weekFields = WeekFields.of(Locale.GERMANY);
                        LocalDate firstDayOfWeek = LocalDate.of(year, 2, 1)
                                .with(weekFields.weekOfWeekBasedYear(), week)
                                .with(DayOfWeek.MONDAY);
                        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);
                        return firstDayOfWeek.format(dayMonthFormatter) + " - " + lastDayOfWeek.format(dayMonthFormatter);
                    } catch (Exception e) {
                        return periodLabel;
                    }
                }
                return periodLabel;

            case "MONTH":
                try {
                    YearMonth ym = YearMonth.parse(periodLabel);
                    return ym.format(monthYearFormatter);
                } catch (DateTimeParseException e) {
                    return periodLabel;
                }

            case "DAY":
            default:
                try {
                    LocalDate date = LocalDate.parse(periodLabel);
                    return date.format(fullDateFormatter);
                } catch (DateTimeParseException e) {
                    return periodLabel;
                }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPeriodLabel;
        RecyclerView rvTopItems;
        RecyclerView rvBottomItems;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPeriodLabel = itemView.findViewById(R.id.tv_period_label);
            rvTopItems = itemView.findViewById(R.id.rv_top_items);
            rvBottomItems = itemView.findViewById(R.id.rv_bottom_items);
        }
    }
}
