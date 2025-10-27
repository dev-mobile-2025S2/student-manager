package com.example.textview.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textview.R;
import com.example.textview.adapters.AvaliacaoAdapter;
import com.example.textview.database.AppDatabase;
import com.example.textview.dialogs.AddAvaliacaoDialog;
import com.example.textview.dialogs.AddMateriaDialog;
import com.example.textview.models.Avaliacao;
import com.example.textview.repository.AvaliacaoRepository;
import com.example.textview.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvScheduledCount, tvWeekCount;
    private RecyclerView recyclerRecent;
    private MaterialButton btnAddSubjects, btnNewExam;
    private AvaliacaoAdapter avaliacaoAdapter;
    private AvaliacaoRepository avaliacaoRepository;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupButtons();
        loadUserData();
        loadStats();
        loadRecentActivity();
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvScheduledCount = view.findViewById(R.id.tv_scheduled_count);
        tvWeekCount = view.findViewById(R.id.tv_week_count);
        recyclerRecent = view.findViewById(R.id.recycler_recent);
        btnAddSubjects = view.findViewById(R.id.btn_add_subjects);
        btnNewExam = view.findViewById(R.id.btn_new_exam);
    }

    private void initDatabase() {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        avaliacaoRepository = new AvaliacaoRepository(database.avaliacaoDao());
        sharedPrefManager = new SharedPrefManager(requireContext());
    }

    private void setupRecyclerView() {
        avaliacaoAdapter = new AvaliacaoAdapter(this::onAvaliacaoClick);
        recyclerRecent.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRecent.setAdapter(avaliacaoAdapter);
    }

    private void setupButtons() {
        btnAddSubjects.setOnClickListener(v -> {
            AddMateriaDialog dialog = new AddMateriaDialog();
            dialog.setOnMateriaAddedListener(() -> {
                // Refresh stats if needed
            });
            dialog.show(getParentFragmentManager(), "AddMateriaDialog");
        });

        btnNewExam.setOnClickListener(v -> {
            AddAvaliacaoDialog dialog = new AddAvaliacaoDialog();
            dialog.setOnAvaliacaoAddedListener(() -> {
                loadStats();
                loadRecentActivity();
            });
            dialog.show(getParentFragmentManager(), "AddAvaliacaoDialog");
        });
    }

    private void loadUserData() {
        String userName = sharedPrefManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setText("Olá, " + userName + "!");
        }
    }

    private void loadStats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Avaliacao> allAvaliacoes = avaliacaoRepository.getAllSync();

            int scheduledCount = 0;
            int weekCount = 0;

            Calendar calendar = Calendar.getInstance();

            // Start of today
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long currentTime = calendar.getTimeInMillis();

            // Get end of this week (Sunday)
            Calendar endWeekCal = Calendar.getInstance();
            endWeekCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            endWeekCal.set(Calendar.HOUR_OF_DAY, 23);
            endWeekCal.set(Calendar.MINUTE, 59);
            endWeekCal.set(Calendar.SECOND, 59);
            endWeekCal.set(Calendar.MILLISECOND, 999);

            // If Sunday is in the past, move to next Sunday
            if (endWeekCal.getTimeInMillis() < currentTime) {
                endWeekCal.add(Calendar.WEEK_OF_YEAR, 1);
            }

            long endOfWeek = endWeekCal.getTimeInMillis();

            for (Avaliacao avaliacao : allAvaliacoes) {
                long avaliacaoTime = avaliacao.getDataAsDate().getTime();
                if (avaliacaoTime >= currentTime) {
                    scheduledCount++;
                    if (avaliacaoTime <= endOfWeek) {
                        weekCount++;
                    }
                }
            }

            final int finalScheduled = scheduledCount;
            final int finalWeek = weekCount;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvScheduledCount.setText(String.valueOf(finalScheduled));
                    tvWeekCount.setText(String.valueOf(finalWeek));
                });
            }
        });
    }

    private void loadRecentActivity() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Avaliacao> recentAvaliacoes = avaliacaoRepository.getAllSync();

            // Sort by date and get recent 5
            recentAvaliacoes.sort((a1, a2) -> a2.getData().compareTo(a1.getData()));
            if (recentAvaliacoes.size() > 5) {
                recentAvaliacoes = recentAvaliacoes.subList(0, 5);
            }

            final List<Avaliacao> finalList = recentAvaliacoes;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> avaliacaoAdapter.setAvaliacoes(finalList));
            }
        });
    }

    private void onAvaliacaoClick(Avaliacao avaliacao) {
        // Show delete confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Avaliação")
                .setMessage("Deseja realmente excluir a avaliação \"" + avaliacao.getTitulo() + "\"?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    deleteAvaliacao(avaliacao);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteAvaliacao(Avaliacao avaliacao) {
        Executors.newSingleThreadExecutor().execute(() -> {
            avaliacaoRepository.delete(avaliacao);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Avaliação excluída com sucesso", Toast.LENGTH_SHORT).show();
                    loadStats();
                    loadRecentActivity();
                });
            }
        });
    }
}
