package com.example.textview.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.textview.R;
import com.example.textview.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView tvProfileInitial, tvProfileName, tvProfileEmail;
    private LinearLayout optionEditProfile, optionNotifications, optionAbout;
    private MaterialButton btnLogout;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        sharedPrefManager = new SharedPrefManager(requireContext());
        loadUserData();
        setupClickListeners();
    }

    private void initViews(View view) {
        tvProfileInitial = view.findViewById(R.id.tv_profile_initial);
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        optionEditProfile = view.findViewById(R.id.option_edit_profile);
        optionNotifications = view.findViewById(R.id.option_notifications);
        optionAbout = view.findViewById(R.id.option_about);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void loadUserData() {
        String userName = sharedPrefManager.getUserName();
        String userEmail = sharedPrefManager.getUserEmail();

        if (userName != null && !userName.isEmpty()) {
            tvProfileName.setText(userName);
            tvProfileInitial.setText(String.valueOf(userName.charAt(0)).toUpperCase());
        }

        if (userEmail != null && !userEmail.isEmpty()) {
            tvProfileEmail.setText(userEmail);
        }
    }

    private void setupClickListeners() {
        optionEditProfile.setOnClickListener(v -> {
            // Edit profile - will be implemented in future
            Toast.makeText(requireContext(), "Editar Perfil - Em desenvolvimento", Toast.LENGTH_SHORT).show();
        });

        optionNotifications.setOnClickListener(v -> {
            // Notifications settings - will be implemented in future
            Toast.makeText(requireContext(), "Configurações de Notificações - Em desenvolvimento", Toast.LENGTH_SHORT).show();
        });

        optionAbout.setOnClickListener(v -> {
            // Open About Fragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(v -> {
            // Show logout confirmation dialog
            showLogoutDialog();
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sair da Conta")
                .setMessage("Deseja realmente sair da sua conta?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void performLogout() {
        sharedPrefManager.clearUser();
        Toast.makeText(requireContext(), "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();

        // Refresh the fragment to show default values
        loadUserData();
    }
}
