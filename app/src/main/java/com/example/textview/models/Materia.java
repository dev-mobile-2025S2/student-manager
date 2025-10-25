package com.example.textview.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "materias")
public class Materia {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String nome;
    private String codigo;
    private String cor;
    private String professor;
    private String cargaHoraria;
    private int tempoEstudado; // in seconds

    public Materia() {
        this.cor = "#1D4ED8"; // Default blue color
        this.tempoEstudado = 0;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(String cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public int getTempoEstudado() {
        return tempoEstudado;
    }

    public void setTempoEstudado(int tempoEstudado) {
        this.tempoEstudado = tempoEstudado;
    }
}
