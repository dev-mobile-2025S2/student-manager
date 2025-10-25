package com.example.textview.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.textview.R;
import com.example.textview.database.AppDatabase;
import com.example.textview.models.Materia;
import com.example.textview.repository.MateriaRepository;

import java.util.Locale;
import java.util.concurrent.Executors;

public class StudyTimerDialog extends DialogFragment {

    private Materia materia;
    private TextView tvMateriaNome, tvTimer, tvStatus;
    private Button btnStartPause, btnReset;
    private int segundos = 0;
    private boolean rodando = false;
    private Handler handler;
    private Runnable timerRunnable;
    private MateriaRepository repository;

    public static StudyTimerDialog newInstance(Materia materia) {
        StudyTimerDialog dialog = new StudyTimerDialog();
        Bundle args = new Bundle();
        args.putLong("materia_id", materia.getId());
        args.putString("materia_nome", materia.getNome());
        args.putInt("tempo_estudado", materia.getTempoEstudado());
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_study_timer, null);

        tvMateriaNome = view.findViewById(R.id.tv_materia_nome);
        tvTimer = view.findViewById(R.id.tv_timer);
        tvStatus = view.findViewById(R.id.tv_status);
        btnStartPause = view.findViewById(R.id.btn_start_pause);
        btnReset = view.findViewById(R.id.btn_reset);

        // Load materia data
        if (getArguments() != null) {
            long materiaId = getArguments().getLong("materia_id");
            String materiaNome = getArguments().getString("materia_nome");
            segundos = getArguments().getInt("tempo_estudado", 0);

            tvMateriaNome.setText(materiaNome);
            updateTimerDisplay();

            loadMateria(materiaId);
        }

        handler = new Handler(Looper.getMainLooper());
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (rodando) {
                    segundos++;
                    updateTimerDisplay();
                    handler.postDelayed(this, 1000);
                }
            }
        };

        btnStartPause.setOnClickListener(v -> toggleTimer());
        btnReset.setOnClickListener(v -> resetTimer());

        builder.setView(view)
                .setTitle("Cronômetro de Estudos")
                .setNegativeButton("Fechar", (dialog, which) -> {
                    if (rodando) {
                        stopAndSave();
                    }
                    dialog.dismiss();
                });

        return builder.create();
    }

    private void loadMateria(long materiaId) {
        AppDatabase database = AppDatabase.getInstance(requireContext());
        repository = new MateriaRepository(database.materiaDao());

        Executors.newSingleThreadExecutor().execute(() -> {
            materia = repository.getMateriaByIdSync(materiaId);
        });
    }

    private void toggleTimer() {
        if (rodando) {
            // Pause
            rodando = false;
            handler.removeCallbacks(timerRunnable);
            btnStartPause.setText("Iniciar");
            tvStatus.setText("Pausado");
            saveTime();
        } else {
            // Start
            rodando = true;
            handler.post(timerRunnable);
            btnStartPause.setText("Pausar");
            tvStatus.setText("Estudando...");
        }
    }

    private void resetTimer() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Resetar Cronômetro")
                .setMessage("Deseja resetar o cronômetro? O tempo estudado será zerado.")
                .setPositiveButton("Sim", (dialog, which) -> {
                    rodando = false;
                    handler.removeCallbacks(timerRunnable);
                    segundos = 0;
                    updateTimerDisplay();
                    btnStartPause.setText("Iniciar");
                    tvStatus.setText("Pausado");
                    if (materia != null) {
                        materia.setTempoEstudado(0);
                        repository.update(materia);
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void updateTimerDisplay() {
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int segs = segundos % 60;
        String timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segs);
        tvTimer.setText(timeString);
    }

    private void saveTime() {
        if (materia != null) {
            materia.setTempoEstudado(segundos);
            repository.update(materia);
        }
    }

    private void stopAndSave() {
        rodando = false;
        handler.removeCallbacks(timerRunnable);
        saveTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rodando) {
            stopAndSave();
        }
    }
}
