package com.example.textview.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.textview.R;
import com.example.textview.models.Materia;
import com.example.textview.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder> {
    private List<Materia> materias = new ArrayList<>();
    private OnMateriaClickListener listener;

    public interface OnMateriaClickListener {
        void onCronometroClick(Materia materia);
    }

    public MateriaAdapter(OnMateriaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_materia, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        Materia materia = materias.get(position);
        holder.bind(materia);
    }

    @Override
    public int getItemCount() {
        return materias.size();
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
        notifyDataSetChanged();
    }

    class MateriaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNome, tvCodigo, tvTempo, tvProvasCount;
        private Button btnCronometro;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tv_nome);
            tvCodigo = itemView.findViewById(R.id.tv_codigo);
            tvTempo = itemView.findViewById(R.id.tv_tempo);
            tvProvasCount = itemView.findViewById(R.id.tv_provas_count);
            btnCronometro = itemView.findViewById(R.id.btn_cronometro);
        }

        public void bind(Materia materia) {
            tvNome.setText(materia.getNome());
            tvCodigo.setText(materia.getCodigo());
            tvTempo.setText(DateUtils.formatTime(materia.getTempoEstudado()));
            tvProvasCount.setText("0 provas"); // Will be updated with actual count

            btnCronometro.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCronometroClick(materia);
                }
            });
        }
    }
}
