package com.example.textview.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.textview.R;
import com.example.textview.database.AppDatabase;
import com.example.textview.models.Materia;
import com.example.textview.repository.MateriaRepository;

import java.util.concurrent.Executors;

public class AddMateriaDialog extends DialogFragment {

    public interface OnMateriaAddedListener {
        void onMateriaAdded();
    }

    private OnMateriaAddedListener listener;
    private EditText etNome, etCodigo, etProfessor, etCargaHoraria;

    public void setOnMateriaAddedListener(OnMateriaAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_materia, null);

        etNome = view.findViewById(R.id.et_nome);
        etCodigo = view.findViewById(R.id.et_codigo);
        etProfessor = view.findViewById(R.id.et_professor);
        etCargaHoraria = view.findViewById(R.id.et_carga_horaria);

        builder.setView(view)
                .setTitle("Adicionar Matéria")
                .setPositiveButton("Adicionar", (dialog, which) -> {
                    String nome = etNome.getText().toString().trim();
                    String codigo = etCodigo.getText().toString().trim();
                    String professor = etProfessor.getText().toString().trim();
                    String cargaHoraria = etCargaHoraria.getText().toString().trim();

                    if (nome.isEmpty() || codigo.isEmpty()) {
                        Toast.makeText(getContext(), "Nome e código são obrigatórios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Materia materia = new Materia();
                    materia.setNome(nome);
                    materia.setCodigo(codigo);
                    materia.setProfessor(professor.isEmpty() ? null : professor);
                    materia.setCargaHoraria(cargaHoraria.isEmpty() ? null : cargaHoraria);
                    materia.setTempoEstudado(0);

                    saveMateria(materia);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void saveMateria(Materia materia) {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        MateriaRepository repository = new MateriaRepository(database.materiaDao());

        Executors.newSingleThreadExecutor().execute(() -> {
            repository.insert(materia);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Matéria adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onMateriaAdded();
                    }
                });
            }
        });
    }
}
