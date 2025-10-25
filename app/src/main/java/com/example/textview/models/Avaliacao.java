package com.example.textview.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "avaliacoes")
public class Avaliacao {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String titulo;
    private long materiaId;
    private String materiaNome;
    private String tipo; // "Prova", "Trabalho", "Seminário", "Projeto"
    private String data; // Format: YYYY-MM-DD
    private String horario; // Format: HH:mm
    private Double nota;
    private String status; // "agendada", "realizada", "cancelada"

    public Avaliacao() {
        this.tipo = "Prova";
        this.status = "agendada";
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

    public long getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(long materiaId) {
        this.materiaId = materiaId;
    }

    public String getMateriaNome() {
        return materiaNome;
    }

    public void setMateriaNome(String materiaNome) {
        this.materiaNome = materiaNome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper method to convert string date to Date object
    public Date getDataAsDate() {
        if (data == null || data.isEmpty()) {
            return new Date();
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return format.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
