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
import com.example.textview.adapters.MateriaTempoAdapter;
import com.example.textview.adapters.StudyTaskAdapter;
import com.example.textview.database.AppDatabase;
import com.example.textview.dialogs.AddStudyTaskDialog;
import com.example.textview.dialogs.StudyTimerDialog;
import com.example.textview.models.Materia;
import com.example.textview.models.StudyTask;
import com.example.textview.repository.MateriaRepository;
import com.example.textview.repository.StudyTaskRepository;
import com.example.textview.utils.DateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class StudyFragment extends Fragment {

    private TextView tvTotalStudyTime, tvTasksSummary, tvEmptyTasks, tvEmptyMaterias;
    private RecyclerView recyclerStudyTasks, recyclerMateriasTempo;
    private FloatingActionButton fabAddTask;
    private StudyTaskAdapter taskAdapter;
    private MateriaTempoAdapter materiaTempoAdapter;
    private StudyTaskRepository taskRepository;
    private MateriaRepository materiaRepository;
    private Map<Long, String> materiaMap = new HashMap<>();
    private Map<Long, Materia> materiaObjectMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initDatabase();
        setupRecyclerView();
        setupFab();
        loadData();
    }

    private void initViews(View view) {
        tvTotalStudyTime = view.findViewById(R.id.tv_total_study_time);
        tvTasksSummary = view.findViewById(R.id.tv_tasks_summary);
        tvEmptyTasks = view.findViewById(R.id.tv_empty_tasks);
        tvEmptyMaterias = view.findViewById(R.id.tv_empty_materias);
        recyclerStudyTasks = view.findViewById(R.id.recycler_study_tasks);
        recyclerMateriasTempo = view.findViewById(R.id.recycler_materias_tempo);
        fabAddTask = view.findViewById(R.id.fab_add_task);
    }

    private void initDatabase() {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        taskRepository = new StudyTaskRepository(database.studyTaskDao());
        materiaRepository = new MateriaRepository(database.materiaDao());
    }

    private void setupRecyclerView() {
        taskAdapter = new StudyTaskAdapter(new StudyTaskAdapter.OnTaskActionListener() {
            @Override
            public void onTaskChecked(StudyTask task, boolean checked) {
                StudyFragment.this.onTaskChecked(task, checked);
            }

            @Override
            public void onTimerClick(StudyTask task) {
                StudyFragment.this.onTimerClick(task);
            }

            @Override
            public void onDeleteClick(StudyTask task) {
                StudyFragment.this.onDeleteClick(task);
            }

            @Override
            public String getMateriaNome(long materiaId) {
                return materiaMap.get(materiaId);
            }
        });
        recyclerStudyTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerStudyTasks.setAdapter(taskAdapter);

        // Adapter de tempo por matéria
        materiaTempoAdapter = new MateriaTempoAdapter();
        recyclerMateriasTempo.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerMateriasTempo.setAdapter(materiaTempoAdapter);
    }

    private void setupFab() {
        fabAddTask.setOnClickListener(v -> {
            AddStudyTaskDialog dialog = new AddStudyTaskDialog();
            dialog.setOnTaskAddedListener(this::loadData);
            dialog.show(getParentFragmentManager(), "AddStudyTaskDialog");
        });
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Carregar matérias
            List<Materia> materias = materiaRepository.getAllSync();
            materiaMap.clear();
            materiaObjectMap.clear();
            int totalSeconds = 0;
            for (Materia materia : materias) {
                materiaMap.put(materia.getId(), materia.getNome());
                materiaObjectMap.put(materia.getId(), materia);
                totalSeconds += materia.getTempoEstudado();
            }

            // Carregar tarefas
            List<StudyTask> tasks = taskRepository.getAllTasksSync();
            int incompleteTasks = (int) tasks.stream().filter(t -> !t.isCompletada()).count();

            final int finalTotalSeconds = totalSeconds;
            final int finalIncompleteTasks = incompleteTasks;
            final List<Materia> finalMaterias = materias;

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Atualizar tempo total
                    tvTotalStudyTime.setText(DateUtils.formatTime(finalTotalSeconds));

                    // Atualizar resumo de tarefas
                    String tasksSummary = finalIncompleteTasks + " tarefa" +
                            (finalIncompleteTasks != 1 ? "s" : "") + " pendente" +
                            (finalIncompleteTasks != 1 ? "s" : "");
                    tvTasksSummary.setText(tasksSummary);

                    // Atualizar lista de matérias com tempo
                    if (finalMaterias.isEmpty()) {
                        tvEmptyMaterias.setVisibility(View.VISIBLE);
                        recyclerMateriasTempo.setVisibility(View.GONE);
                    } else {
                        tvEmptyMaterias.setVisibility(View.GONE);
                        recyclerMateriasTempo.setVisibility(View.VISIBLE);
                        materiaTempoAdapter.setMaterias(finalMaterias);
                    }

                    // Atualizar lista de tarefas
                    if (tasks.isEmpty()) {
                        tvEmptyTasks.setVisibility(View.VISIBLE);
                        recyclerStudyTasks.setVisibility(View.GONE);
                    } else {
                        tvEmptyTasks.setVisibility(View.GONE);
                        recyclerStudyTasks.setVisibility(View.VISIBLE);
                        taskAdapter.setTasks(tasks);
                    }
                });
            }
        });
    }

    private void onTaskChecked(StudyTask task, boolean checked) {
        task.setCompletada(checked);
        Executors.newSingleThreadExecutor().execute(() -> {
            taskRepository.update(task);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    String message = checked ? "Tarefa concluída!" : "Tarefa reaberta";
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    // Atualiza apenas o contador, não recarrega a lista toda
                    updateTaskSummary();
                });
            }
        });
    }

    private void updateTaskSummary() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<StudyTask> tasks = taskRepository.getAllTasksSync();
            int incompleteTasks = (int) tasks.stream().filter(t -> !t.isCompletada()).count();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    String tasksSummary = incompleteTasks + " tarefa" +
                            (incompleteTasks != 1 ? "s" : "") + " pendente" +
                            (incompleteTasks != 1 ? "s" : "");
                    tvTasksSummary.setText(tasksSummary);
                });
            }
        });
    }

    private void onTimerClick(StudyTask task) {
        // Busca a matéria direto do mapa em memória
        Materia materia = materiaObjectMap.get(task.getMateriaId());
        if (materia != null) {
            StudyTimerDialog dialog = StudyTimerDialog.newInstance(materia);
            dialog.show(getParentFragmentManager(), "StudyTimerDialog");
        } else {
            Toast.makeText(requireContext(), "Matéria não encontrada", Toast.LENGTH_SHORT).show();
        }
    }

    private void onDeleteClick(StudyTask task) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Excluir Tarefa")
                .setMessage("Deseja realmente excluir esta tarefa?\n\nEsta ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    deleteTask(task);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteTask(StudyTask task) {
        Executors.newSingleThreadExecutor().execute(() -> {
            taskRepository.delete(task);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Tarefa excluída com sucesso", Toast.LENGTH_SHORT).show();
                    loadData();
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
