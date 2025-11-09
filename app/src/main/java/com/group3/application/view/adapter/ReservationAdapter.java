
package com.group3.application.view.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.Reservation;

import java.util.List;

public class ReservationAdapter /*extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>*/ {

    /*private List<Reservation> reservationList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Reservation reservation);
    }

    public ReservationAdapter(List<Reservation> reservationList, OnItemClickListener listener) {
        this.reservationList = reservationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.bind(reservation, listener);
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public void filterList(List<Reservation> filteredList) {
        reservationList = filteredList;
        notifyDataSetChanged();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, time, status, date, pax, table;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customer_name_label);
            time = itemView.findViewById(R.id.time_label);
            status = itemView.findViewById(R.id.status_label);
            date = itemView.findViewById(R.id.date_label);
            pax = itemView.findViewById(R.id.pax_label);
            table = itemView.findViewById(R.id.table_label);
        }

        public void bind(final Reservation reservation, final OnItemClickListener listener) {
            customerName.setText(reservation.getCustomerName());
            time.setText(reservation.getTime());
            status.setText(reservation.getStatus());
            date.setText(reservation.getDate());
            pax.setText(reservation.getPax() + " Pax");
            table.setText("Table " + reservation.getTableNumber());

            GradientDrawable statusBackground = (GradientDrawable) status.getBackground();
            switch (reservation.getStatus()) {
                case "Confirmed":
                    statusBackground.setColor(Color.parseColor("#4CAF50")); // Green
                    break;
                case "Pending":
                    statusBackground.setColor(Color.parseColor("#FFC107")); // Amber
                    break;
                case "Canceled":
                    statusBackground.setColor(Color.parseColor("#F44336")); // Red
                    break;
                default:
                    statusBackground.setColor(Color.parseColor("#757575")); // Gray
                    break;
            }

            itemView.setOnClickListener(v -> listener.onItemClick(reservation));
        }
    }*/
}
