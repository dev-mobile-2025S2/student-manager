package com.example.textview.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textview.R;
import com.example.textview.adapters.MateriaAdapter;
import com.example.textview.database.AppDatabase;
import com.example.textview.dialogs.AddMateriaDialog;
import com.example.textview.dialogs.StudyTimerDialog;
import com.example.textview.models.Materia;
import com.example.textview.repository.MateriaRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SubjectsFragment extends Fragment {

    private RecyclerView recyclerSubjects;
    private FloatingActionButton fabAddSubject;
    private TextView tvEmptyState;
    private MateriaAdapter materiaAdapter;
    private MateriaRepository materiaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupFab();
        loadSubjects();
    }

    private void initViews(View view) {
        recyclerSubjects = view.findViewById(R.id.recycler_subjects);
        fabAddSubject = view.findViewById(R.id.fab_add_subject);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
    }

    private void initDatabase() {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        materiaRepository = new MateriaRepository(database.materiaDao());
    }

    private void setupRecyclerView() {
        materiaAdapter = new MateriaAdapter(this::onCronometroClick);
        recyclerSubjects.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSubjects.setAdapter(materiaAdapter);
    }

    private void setupFab() {
        fabAddSubject.setOnClickListener(v -> {
            AddMateriaDialog dialog = new AddMateriaDialog();
            dialog.setOnMateriaAddedListener(this::loadSubjects);
            dialog.show(getParentFragmentManager(), "AddMateriaDialog");
        });
    }

    private void loadSubjects() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Materia> materias = materiaRepository.getAllSync();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (materias.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        recyclerSubjects.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        recyclerSubjects.setVisibility(View.VISIBLE);
                        materiaAdapter.setMaterias(materias);
                    }
                });
            }
        });
    }

    private void onCronometroClick(Materia materia) {
        StudyTimerDialog dialog = StudyTimerDialog.newInstance(materia);
        dialog.show(getParentFragmentManager(), "StudyTimerDialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubjects(); // Refresh when returning to fragment
    }
}
