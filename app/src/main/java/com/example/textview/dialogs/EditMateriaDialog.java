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

public class EditMateriaDialog extends DialogFragment {

    private static final String ARG_MATERIA_ID = "materia_id";
    private static final String ARG_MATERIA_NOME = "materia_nome";
    private static final String ARG_MATERIA_CODIGO = "materia_codigo";
    private static final String ARG_MATERIA_PROFESSOR = "materia_professor";
    private static final String ARG_MATERIA_CARGA = "materia_carga";
    private static final String ARG_MATERIA_TEMPO = "materia_tempo";

    public interface OnMateriaUpdatedListener {
        void onMateriaUpdated();
    }

    private OnMateriaUpdatedListener listener;
    private EditText etNome, etCodigo, etProfessor, etCargaHoraria;
    private long materiaId;
    private int tempoEstudado;

    public static EditMateriaDialog newInstance(Materia materia) {
        EditMateriaDialog dialog = new EditMateriaDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_MATERIA_ID, materia.getId());
        args.putString(ARG_MATERIA_NOME, materia.getNome());
        args.putString(ARG_MATERIA_CODIGO, materia.getCodigo());
        args.putString(ARG_MATERIA_PROFESSOR, materia.getProfessor());
        args.putString(ARG_MATERIA_CARGA, materia.getCargaHoraria());
        args.putInt(ARG_MATERIA_TEMPO, materia.getTempoEstudado());
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnMateriaUpdatedListener(OnMateriaUpdatedListener listener) {
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

        // Load existing data
        if (getArguments() != null) {
            materiaId = getArguments().getLong(ARG_MATERIA_ID);
            tempoEstudado = getArguments().getInt(ARG_MATERIA_TEMPO);
            etNome.setText(getArguments().getString(ARG_MATERIA_NOME));
            etCodigo.setText(getArguments().getString(ARG_MATERIA_CODIGO));
            etProfessor.setText(getArguments().getString(ARG_MATERIA_PROFESSOR, ""));
            etCargaHoraria.setText(getArguments().getString(ARG_MATERIA_CARGA, ""));
        }

        builder.setView(view)
                .setTitle("Editar Matéria")
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nome = etNome.getText().toString().trim();
                    String codigo = etCodigo.getText().toString().trim();
                    String professor = etProfessor.getText().toString().trim();
                    String cargaHoraria = etCargaHoraria.getText().toString().trim();

                    if (nome.isEmpty() || codigo.isEmpty()) {
                        Toast.makeText(getContext(), "Nome e código são obrigatórios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Materia materia = new Materia();
                    materia.setId(materiaId);
                    materia.setNome(nome);
                    materia.setCodigo(codigo);
                    materia.setProfessor(professor.isEmpty() ? null : professor);
                    materia.setCargaHoraria(cargaHoraria.isEmpty() ? null : cargaHoraria);
                    materia.setTempoEstudado(tempoEstudado);

                    updateMateria(materia);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void updateMateria(Materia materia) {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        MateriaRepository repository = new MateriaRepository(database.materiaDao());

        Executors.newSingleThreadExecutor().execute(() -> {
            repository.update(materia);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Matéria atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onMateriaUpdated();
                    }
                });
            }
        });
    }
}
