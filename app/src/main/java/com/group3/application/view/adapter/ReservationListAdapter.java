package com.group3.application.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.group3.application.R;
import com.group3.application.model.entity.Reservation;
import com.group3.application.view.ReservationDetailActivity;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ReservationListViewHolder> {

    private List<Reservation> reservations = new ArrayList<>();

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservationListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_list_item, parent, false);
        return new ReservationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationListViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationListViewHolder extends RecyclerView.ViewHolder {

        private final TextView customerNameTextView;
        private final TextView reservationTimeTextView;
        private final TextView numGuestsTextView;
        private final Chip statusChip;

        public ReservationListViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customer_name_text_view);
            reservationTimeTextView = itemView.findViewById(R.id.reservation_time_text_view);
            numGuestsTextView = itemView.findViewById(R.id.num_guests_text_view);
            statusChip = itemView.findViewById(R.id.status_chip);
        }

        public void bind(Reservation reservation) {
            customerNameTextView.setText(reservation.getCustomerName());
            reservationTimeTextView.setText(reservation.getReservationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            numGuestsTextView.setText(String.valueOf(reservation.getNumGuests()));
            statusChip.setText(reservation.getStatus());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ReservationDetailActivity.class);
                intent.putExtra("reservation", reservation);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
