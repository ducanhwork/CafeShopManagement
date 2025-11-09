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
 * Shift Management Fragment
 * Displays current shift status, cash balance, and shift operations
 */
public class ShiftFragment extends Fragment {

    private TextView tvPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shift, container, false);
        
        tvPlaceholder = view.findViewById(R.id.tv_placeholder);
        tvPlaceholder.setText("Shift Management\n\nComing Soon:\n• Start/End Shift\n• Cash Transactions\n• Shift History\n• Shift Statistics");
        
        return view;
    }
}
