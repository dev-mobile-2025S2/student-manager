package com.example.textview.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textview.R;
import com.example.textview.models.StudyTask;

import java.util.ArrayList;
import java.util.List;

public class StudyTaskAdapter extends RecyclerView.Adapter<StudyTaskAdapter.StudyTaskViewHolder> {
    private List<StudyTask> tasks = new ArrayList<>();
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onTaskChecked(StudyTask task, boolean checked);
        void onTimerClick(StudyTask task);
        void onDeleteClick(StudyTask task);
        String getMateriaNome(long materiaId);
    }

    public StudyTaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_study_task, parent, false);
        return new StudyTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyTaskViewHolder holder, int position) {
        StudyTask task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<StudyTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    class StudyTaskViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkboxCompleted;
        private TextView tvTitulo, tvDescricao, tvMateria, tvPrioridade;
        private ImageButton btnTimer, btnDelete;

        public StudyTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxCompleted = itemView.findViewById(R.id.checkbox_completed);
            tvTitulo = itemView.findViewById(R.id.tv_task_titulo);
            tvDescricao = itemView.findViewById(R.id.tv_task_descricao);
            tvMateria = itemView.findViewById(R.id.tv_task_materia);
            tvPrioridade = itemView.findViewById(R.id.tv_task_prioridade);
            btnTimer = itemView.findViewById(R.id.btn_timer);
            btnDelete = itemView.findViewById(R.id.btn_delete_task);
        }

        public void bind(StudyTask task) {
            tvTitulo.setText(task.getTitulo());

            // Mostrar descrição se existir
            if (task.getDescricao() != null && !task.getDescricao().isEmpty()) {
                tvDescricao.setText(task.getDescricao());
                tvDescricao.setVisibility(View.VISIBLE);
            } else {
                tvDescricao.setVisibility(View.GONE);
            }

            // Nome da matéria
            if (listener != null) {
                String materiaNome = listener.getMateriaNome(task.getMateriaId());
                tvMateria.setText(materiaNome != null ? materiaNome : "Sem matéria");
            }

            // Prioridade
            tvPrioridade.setText(getPrioridadeText(task.getPrioridade()));
            tvPrioridade.setTextColor(getPrioridadeColor(task.getPrioridade()));

            // Checkbox de completada - Remove listener antes de setar o estado
            checkboxCompleted.setOnCheckedChangeListener(null);
            checkboxCompleted.setChecked(task.isCompletada());

            // Aplicar estilo de riscado se completada
            if (task.isCompletada()) {
                tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitulo.setAlpha(0.5f);
            } else {
                tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitulo.setAlpha(1.0f);
            }

            // Listeners - Adiciona listener depois de setar o estado
            checkboxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskChecked(task, isChecked);
                }
            });

            btnTimer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTimerClick(task);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(task);
                }
            });
        }

        private String getPrioridadeText(String prioridade) {
            switch (prioridade.toLowerCase()) {
                case "alta":
                    return "Alta";
                case "media":
                    return "Média";
                case "baixa":
                    return "Baixa";
                default:
                    return "Média";
            }
        }

        private int getPrioridadeColor(String prioridade) {
            switch (prioridade.toLowerCase()) {
                case "alta":
                    return itemView.getContext().getColor(R.color.red_600);
                case "media":
                    return itemView.getContext().getColor(R.color.orange_600);
                case "baixa":
                    return itemView.getContext().getColor(R.color.green_600);
                default:
                    return itemView.getContext().getColor(R.color.text_secondary);
            }
        }
    }
}
