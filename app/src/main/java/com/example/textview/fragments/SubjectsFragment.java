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
import com.example.textview.adapters.MateriaAdapter;
import com.example.textview.database.AppDatabase;
import com.example.textview.dialogs.AddMateriaDialog;
import com.example.textview.dialogs.EditMateriaDialog;
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
        materiaAdapter = new MateriaAdapter(new MateriaAdapter.OnMateriaActionListener() {
            @Override
            public void onCronometroClick(Materia materia) {
                SubjectsFragment.this.onCronometroClick(materia);
            }

            @Override
            public void onEditClick(Materia materia) {
                SubjectsFragment.this.onEditClick(materia);
            }

            @Override
            public void onDeleteClick(Materia materia) {
                SubjectsFragment.this.onDeleteClick(materia);
            }
        });
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

    private void onEditClick(Materia materia) {
        EditMateriaDialog dialog = EditMateriaDialog.newInstance(materia);
        dialog.setOnMateriaUpdatedListener(this::loadSubjects);
        dialog.show(getParentFragmentManager(), "EditMateriaDialog");
    }

    private void onDeleteClick(Materia materia) {
        // Show delete confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Matéria")
                .setMessage("Deseja realmente excluir a matéria \"" + materia.getNome() + "\"?\n\nEsta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    deleteMateria(materia);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteMateria(Materia materia) {
        Executors.newSingleThreadExecutor().execute(() -> {
            materiaRepository.delete(materia);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Matéria excluída com sucesso", Toast.LENGTH_SHORT).show();
                    loadSubjects();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubjects(); // Refresh when returning to fragment
    }
}
