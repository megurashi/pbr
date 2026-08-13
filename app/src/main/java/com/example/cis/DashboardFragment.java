package com.example.cis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.cis.databinding.FragmentDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String name = prefs.getString("username", "Fajri");
        
        // Take first word for dashboard
        String firstName = name.split(" ")[0];
        binding.tvName.setText(firstName);

        BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);

        binding.cardAktivitas.setOnClickListener(v -> {
            if (nav != null) nav.setSelectedItemId(R.id.navigation_tasks);
        });

        binding.cardPengaturan.setOnClickListener(v -> {
            if (nav != null) nav.setSelectedItemId(R.id.navigation_settings);
        });

        binding.cardTentang.setOnClickListener(v -> {
            // Could open an AboutActivity or a dialog
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
            builder.setTitle("Tentang");
            builder.setMessage("Task Reminder App\nVersi 1.0.0\nDibuat oleh: Muhammad Fajri Rahman");
            builder.setPositiveButton("OK", null);
            builder.show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
