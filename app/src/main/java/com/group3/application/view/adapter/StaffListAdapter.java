package com.group3.application.view.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group3.application.R;
import com.group3.application.model.entity.User;
import com.group3.application.view.ReservationDetailActivity;
import com.group3.application.view.StaffDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.StaffViewHolder> {

    private List<User> staffs = new ArrayList<>();

    public void setStaffs(List<User> staffs) {
        this.staffs = staffs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_staff, null);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        User staff = staffs.get(position);
        holder.bind(staff);
    }

    @Override
    public int getItemCount() {
        return staffs.size();
    }

    static class StaffViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }

        public void bind(User staff) {
            nameTextView.setText(staff.getFullname());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), StaffDetailActivity.class);
                intent.putExtra("staff", staff);
                if (staff != null && staff.getId() != null) {
                    intent.putExtra("staffId", staff.getId().toString());
                }
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
