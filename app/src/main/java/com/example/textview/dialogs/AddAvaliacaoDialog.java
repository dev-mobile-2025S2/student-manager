package com.example.textview.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import com.example.textview.models.Avaliacao;
import com.example.textview.models.Materia;
import com.example.textview.repository.AvaliacaoRepository;
import com.example.textview.repository.MateriaRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddAvaliacaoDialog extends DialogFragment {

    public interface OnAvaliacaoAddedListener {
        void onAvaliacaoAdded();
    }

    private OnAvaliacaoAddedListener listener;
    private EditText etTitulo, etData, etHorario;
    private Spinner spinnerMateria, spinnerTipo;
    private List<Materia> materias = new ArrayList<>();
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedTime = Calendar.getInstance();

    public void setOnAvaliacaoAddedListener(OnAvaliacaoAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_avaliacao, null);

        etTitulo = view.findViewById(R.id.et_titulo);
        etData = view.findViewById(R.id.et_data);
        etHorario = view.findViewById(R.id.et_horario);
        spinnerMateria = view.findViewById(R.id.spinner_materia);
        spinnerTipo = view.findViewById(R.id.spinner_tipo);

        setupSpinners();
        setupDateTimePickers();
        loadMaterias();

        builder.setView(view)
                .setTitle("Adicionar Avaliação")
                .setPositiveButton("Adicionar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String data = etData.getText().toString().trim();
                    String horario = etHorario.getText().toString().trim();

                    if (titulo.isEmpty() || data.isEmpty() || horario.isEmpty()) {
                        Toast.makeText(getContext(), "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (spinnerMateria.getSelectedItemPosition() < 0) {
                        Toast.makeText(getContext(), "Selecione uma matéria", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Materia selectedMateria = materias.get(spinnerMateria.getSelectedItemPosition());
                    String tipo = spinnerTipo.getSelectedItem().toString();

                    Avaliacao avaliacao = new Avaliacao();
                    avaliacao.setTitulo(titulo);
                    avaliacao.setMateriaId(selectedMateria.getId());
                    avaliacao.setMateriaNome(selectedMateria.getNome());
                    avaliacao.setTipo(tipo);
                    avaliacao.setData(data);
                    avaliacao.setHorario(horario);
                    avaliacao.setStatus("agendada");

                    saveAvaliacao(avaliacao);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void setupSpinners() {
        // Tipo Spinner
        String[] tipos = {"Prova", "Trabalho", "Seminário", "Projeto"};
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                tipos
        );
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
    }

    private void setupDateTimePickers() {
        etData.setFocusable(false);
        etData.setClickable(true);
        etData.setOnClickListener(v -> showDatePicker());

        etHorario.setFocusable(false);
        etHorario.setClickable(true);
        etHorario.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etData.setText(sdf.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedTime.set(Calendar.MINUTE, minute);
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    etHorario.setText(time);
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void loadMaterias() {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        MateriaRepository repository = new MateriaRepository(database.materiaDao());

        Executors.newSingleThreadExecutor().execute(() -> {
            materias = repository.getAllSync();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    List<String> materiaNames = new ArrayList<>();
                    for (Materia m : materias) {
                        materiaNames.add(m.getNome());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            materiaNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMateria.setAdapter(adapter);
                });
            }
        });
    }

    private void saveAvaliacao(Avaliacao avaliacao) {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        AvaliacaoRepository repository = new AvaliacaoRepository(database.avaliacaoDao());

        Executors.newSingleThreadExecutor().execute(() -> {
            repository.insert(avaliacao);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Avaliação adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onAvaliacaoAdded();
                    }
                });
            }
        });
    }
}
