package com.example.textview.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "study_tasks",
        foreignKeys = @ForeignKey(entity = Materia.class,
                parentColumns = "id",
                childColumns = "materiaId",
                onDelete = ForeignKey.CASCADE))
public class StudyTask {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String titulo;
    private String descricao;
    private long materiaId;
    private boolean completada;
    private String prioridade; // "baixa", "media", "alta"
    private long dataCriacao;

    public StudyTask() {
        this.completada = false;
        this.prioridade = "media";
        this.dataCriacao = System.currentTimeMillis();
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(long materiaId) {
        this.materiaId = materiaId;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
