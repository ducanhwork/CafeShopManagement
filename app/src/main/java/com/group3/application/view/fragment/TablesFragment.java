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
 * Tables Management Fragment
 * Displays table grid and table operations
 */
public class TablesFragment extends Fragment {

    private TextView tvPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tables, container, false);
        
        tvPlaceholder = view.findViewById(R.id.tv_placeholder);
        tvPlaceholder.setText("Table Management\n\nComing Soon:\n• Table Grid View\n• Add/Edit Tables\n• Table Status\n• QR Code Generation");
        
        return view;
    }
}
