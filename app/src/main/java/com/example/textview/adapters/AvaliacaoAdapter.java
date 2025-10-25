package com.example.textview.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textview.R;
import com.example.textview.models.Avaliacao;
import com.example.textview.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class AvaliacaoAdapter extends RecyclerView.Adapter<AvaliacaoAdapter.AvaliacaoViewHolder> {
    private List<Avaliacao> avaliacoes = new ArrayList<>();
    private OnAvaliacaoClickListener listener;
    private boolean showDeleteButton = true;

    public interface OnAvaliacaoClickListener {
        void onDeleteClick(Avaliacao avaliacao);
    }

    public AvaliacaoAdapter(OnAvaliacaoClickListener listener) {
        this.listener = listener;
    }

    public AvaliacaoAdapter(OnAvaliacaoClickListener listener, boolean showDeleteButton) {
        this.listener = listener;
        this.showDeleteButton = showDeleteButton;
    }

    @NonNull
    @Override
    public AvaliacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avaliacao, parent, false);
        return new AvaliacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvaliacaoViewHolder holder, int position) {
        Avaliacao avaliacao = avaliacoes.get(position);
        holder.bind(avaliacao);
    }

    @Override
    public int getItemCount() {
        return avaliacoes.size();
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
        notifyDataSetChanged();
    }

    class AvaliacaoViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitulo, tvTipoBadge, tvMateria, tvData, tvHorario;
        private ImageButton btnDelete;

        public AvaliacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_titulo);
            tvTipoBadge = itemView.findViewById(R.id.tv_tipo_badge);
            tvMateria = itemView.findViewById(R.id.tv_materia);
            tvData = itemView.findViewById(R.id.tv_data);
            tvHorario = itemView.findViewById(R.id.tv_horario);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Avaliacao avaliacao) {
            tvTitulo.setText(avaliacao.getTitulo());
            tvTipoBadge.setText(avaliacao.getTipo());
            tvMateria.setText("📚 " + avaliacao.getMateriaNome());
            tvData.setText(DateUtils.formatToDisplay(avaliacao.getData()));
            tvHorario.setText(avaliacao.getHorario());

            // Show or hide delete button based on adapter configuration
            if (showDeleteButton) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(avaliacao);
                    }
                });
            } else {
                btnDelete.setVisibility(View.GONE);
            }
        }
    }
}
