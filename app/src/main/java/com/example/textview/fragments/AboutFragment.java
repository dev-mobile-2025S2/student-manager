package com.example.textview.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.textview.R;

public class AboutFragment extends Fragment {

    private TextView tvVersion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvVersion = view.findViewById(R.id.tv_version);
        loadAppVersion();
        setupFeatures(view);
    }

    private void loadAppVersion() {
        try {
            PackageInfo pInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText("Versão " + version);
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setText("Versão 1.0");
        }
    }

    private void setupFeatures(View view) {
        // Feature 1: Gestão de Matérias
        setupFeatureItem(view, R.id.feature_1,
                android.R.drawable.ic_menu_info_details,
                "Gestão de Matérias",
                "Organize todas as suas disciplinas",
                R.color.blue_600);

        // Feature 2: Cronômetro de Estudos
        setupFeatureItem(view, R.id.feature_2,
                android.R.drawable.ic_menu_recent_history,
                "Cronômetro de Estudos",
                "Acompanhe seu tempo de estudo",
                R.color.green_600);

        // Feature 3: Calendário de Provas
        setupFeatureItem(view, R.id.feature_3,
                android.R.drawable.ic_menu_my_calendar,
                "Calendário de Provas",
                "Visualize suas avaliações agendadas",
                R.color.blue_600);

        // Feature 4: To-Do List de Estudos
        setupFeatureItem(view, R.id.feature_4,
                android.R.drawable.checkbox_on_background,
                "To-Do List de Estudos",
                "Gerencie suas tarefas de estudo",
                R.color.purple_500);
    }

    private void setupFeatureItem(View rootView, int featureId, int iconResId,
                                   String title, String description, int colorResId) {
        View featureView = rootView.findViewById(featureId);
        if (featureView != null) {
            ImageView icon = featureView.findViewById(R.id.feature_icon);
            TextView tvTitle = featureView.findViewById(R.id.feature_title);
            TextView tvDescription = featureView.findViewById(R.id.feature_description);

            icon.setImageResource(iconResId);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), colorResId));
            tvTitle.setText(title);
            tvDescription.setText(description);
        }
    }
}
