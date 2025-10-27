package com.example.textview.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.textview.R;
import com.example.textview.database.AppDatabase;
import com.example.textview.models.Materia;
import com.example.textview.models.StudyTask;
import com.example.textview.repository.MateriaRepository;
import com.example.textview.repository.StudyTaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AddStudyTaskDialog extends DialogFragment {

    public interface OnTaskAddedListener {
        void onTaskAdded();
    }

    private OnTaskAddedListener listener;
    private EditText etTitulo, etDescricao;
    private Spinner spinnerMateria, spinnerPrioridade;
    private List<Materia> materias = new ArrayList<>();

    public void setOnTaskAddedListener(OnTaskAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_study_task, null);

        etTitulo = view.findViewById(R.id.et_task_titulo);
        etDescricao = view.findViewById(R.id.et_task_descricao);
        spinnerMateria = view.findViewById(R.id.spinner_materia);
        spinnerPrioridade = view.findViewById(R.id.spinner_prioridade);

        // Setup spinner de prioridade
        ArrayAdapter<CharSequence> prioridadeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.prioridades,
                android.R.layout.simple_spinner_item
        );
        prioridadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridade.setAdapter(prioridadeAdapter);
        spinnerPrioridade.setSelection(1); // Média por padrão

        // Carregar matérias
        loadMaterias();

        builder.setView(view)
                .setTitle("Adicionar Tarefa de Estudo")
                .setPositiveButton("Adicionar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String descricao = etDescricao.getText().toString().trim();

                    if (titulo.isEmpty()) {
                        Toast.makeText(getContext(), "Título é obrigatório", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (materias.isEmpty()) {
                        Toast.makeText(getContext(), "Cadastre uma matéria primeiro", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    StudyTask task = new StudyTask();
                    task.setTitulo(titulo);
                    task.setDescricao(descricao.isEmpty() ? null : descricao);
                    task.setMateriaId(materias.get(spinnerMateria.getSelectedItemPosition()).getId());

                    // Prioridade
                    String[] prioridades = {"baixa", "media", "alta"};
                    task.setPrioridade(prioridades[spinnerPrioridade.getSelectedItemPosition()]);

                    saveTask(task);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void loadMaterias() {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        MateriaRepository repository = new MateriaRepository(database.materiaDao());

        Executors.newSingleThreadExecutor().execute(() -> {
            materias = repository.getAllSync();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    List<String> materiaNomes = new ArrayList<>();
                    for (Materia materia : materias) {
                        materiaNomes.add(materia.getNome());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            materiaNomes
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMateria.setAdapter(adapter);
                });
            }
        });
    }

    private void saveTask(StudyTask task) {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        StudyTaskRepository repository = new StudyTaskRepository(database.studyTaskDao());

        Executors.newSingleThreadExecutor().execute(() -> {
            repository.insert(task);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Tarefa adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onTaskAdded();
                    }
                });
            }
        });
    }
}
