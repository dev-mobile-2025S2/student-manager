package com.example.textview.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textview.R;
import com.example.textview.models.Materia;
import com.example.textview.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class MateriaTempoAdapter extends RecyclerView.Adapter<MateriaTempoAdapter.MateriaTempoViewHolder> {
    private List<Materia> materias = new ArrayList<>();

    @NonNull
    @Override
    public MateriaTempoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_materia_tempo, parent, false);
        return new MateriaTempoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaTempoViewHolder holder, int position) {
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

    static class MateriaTempoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNome, tvCodigo, tvTempo;
        private View viewIndicator;

        public MateriaTempoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tv_materia_nome);
            tvCodigo = itemView.findViewById(R.id.tv_materia_codigo);
            tvTempo = itemView.findViewById(R.id.tv_materia_tempo);
            viewIndicator = itemView.findViewById(R.id.view_materia_indicator);
        }

        public void bind(Materia materia) {
            tvNome.setText(materia.getNome());
            tvCodigo.setText(materia.getCodigo());
            tvTempo.setText(DateUtils.formatTime(materia.getTempoEstudado()));
        }
    }
}
