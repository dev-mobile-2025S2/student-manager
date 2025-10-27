package com.example.textview.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
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
import com.example.textview.models.Avaliacao;
import com.example.textview.repository.AvaliacaoRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView recyclerExams;
    private FloatingActionButton fabAddExam;
    private TextView tvEmptyState;
    private TextView tvDaysWithExams;
    private AvaliacaoAdapter avaliacaoAdapter;
    private AvaliacaoRepository avaliacaoRepository;
    private long selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupCalendar();
        setupFab();
        loadExams();
    }

    private void initViews(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        recyclerExams = view.findViewById(R.id.recycler_exams);
        fabAddExam = view.findViewById(R.id.fab_add_exam);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvDaysWithExams = view.findViewById(R.id.tv_days_with_exams);
    }

    private void initDatabase() {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        avaliacaoRepository = new AvaliacaoRepository(database.avaliacaoDao());
    }

    private void setupRecyclerView() {
        avaliacaoAdapter = new AvaliacaoAdapter(this::onAvaliacaoClick);
        recyclerExams.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerExams.setAdapter(avaliacaoAdapter);
    }

    private void setupCalendar() {
        selectedDate = System.currentTimeMillis();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTimeInMillis();
            loadExams();
        });
    }

    private void setupFab() {
        fabAddExam.setOnClickListener(v -> {
            AddAvaliacaoDialog dialog = new AddAvaliacaoDialog();
            dialog.setOnAvaliacaoAddedListener(this::loadExams);
            dialog.show(getParentFragmentManager(), "AddAvaliacaoDialog");
        });
    }

    private void loadExams() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Avaliacao> allAvaliacoes = avaliacaoRepository.getAllSync();

            // Get start and end of selected day
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.setTimeInMillis(selectedDate);
            selectedCal.set(Calendar.HOUR_OF_DAY, 0);
            selectedCal.set(Calendar.MINUTE, 0);
            selectedCal.set(Calendar.SECOND, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);

            Calendar endOfDay = (Calendar) selectedCal.clone();
            endOfDay.set(Calendar.HOUR_OF_DAY, 23);
            endOfDay.set(Calendar.MINUTE, 59);
            endOfDay.set(Calendar.SECOND, 59);
            endOfDay.set(Calendar.MILLISECOND, 999);

            long startOfDay = selectedCal.getTimeInMillis();
            long endOfDayTime = endOfDay.getTimeInMillis();

            // Show only exams for the selected day
            List<Avaliacao> filteredAvaliacoes = allAvaliacoes.stream()
                    .filter(a -> {
                        long avaliacaoTime = a.getDataAsDate().getTime();
                        return avaliacaoTime >= startOfDay && avaliacaoTime <= endOfDayTime;
                    })
                    .collect(Collectors.toList());

            // Collect all dates with exams to display
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            Set<String> examDatesFormatted = new HashSet<>();
            for (Avaliacao avaliacao : allAvaliacoes) {
                examDatesFormatted.add(dateFormat.format(avaliacao.getDataAsDate()));
            }

            String daysWithExamsText = examDatesFormatted.isEmpty()
                    ? "Nenhum dia com atividades"
                    : String.join(", ", examDatesFormatted);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Update days with exams text
                    tvDaysWithExams.setText(daysWithExamsText);

                    // Update exam list
                    if (filteredAvaliacoes.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerExams.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerExams.setVisibility(View.VISIBLE);
                        avaliacaoAdapter.setAvaliacoes(filteredAvaliacoes);
                    }
                });
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
                    loadExams();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExams(); // Refresh when returning to fragment
    }
}
