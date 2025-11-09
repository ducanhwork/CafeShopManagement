package com.group3.application.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group3.application.R;

/**
 * Reports & Analytics Fragment
 * Displays sales reports, shift statistics, and inventory reports
 */
public class ReportsFragment extends Fragment {

    private TextView tvPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        
        tvPlaceholder = view.findViewById(R.id.tv_placeholder);
        tvPlaceholder.setText("Reports & Analytics\n\nComing Soon:\n• Sales Reports\n• Shift Statistics\n• Inventory Reports\n• Charts & Graphs");
        
        return view;
    }
}
