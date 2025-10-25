package com.example.textview.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.textview.models.Avaliacao;
import java.util.List;

@Dao
public interface AvaliacaoDao {

    @Insert
    long insert(Avaliacao avaliacao);

    @Update
    void update(Avaliacao avaliacao);

    @Delete
    void delete(Avaliacao avaliacao);

    @Query("SELECT * FROM avaliacoes ORDER BY data DESC, horario DESC")
    LiveData<List<Avaliacao>> getAllAvaliacoes();

    @Query("SELECT * FROM avaliacoes WHERE status = 'agendada' ORDER BY data ASC, horario ASC")
    LiveData<List<Avaliacao>> getAvaliacoesAgendadas();

    @Query("SELECT * FROM avaliacoes WHERE materiaId = :materiaId ORDER BY data DESC")
    LiveData<List<Avaliacao>> getAvaliacoesByMateria(long materiaId);

    @Query("SELECT * FROM avaliacoes WHERE id = :id")
    LiveData<Avaliacao> getAvaliacaoById(long id);

    @Query("SELECT * FROM avaliacoes WHERE data = :date ORDER BY horario ASC")
    LiveData<List<Avaliacao>> getAvaliacoesByDate(String date);

    @Query("SELECT * FROM avaliacoes ORDER BY data DESC, horario DESC")
    List<Avaliacao> getAllAvaliacoesSync();

    @Query("SELECT * FROM avaliacoes WHERE status = 'agendada' ORDER BY data ASC, horario ASC")
    List<Avaliacao> getAvaliacoesAgendadasSync();

    @Query("DELETE FROM avaliacoes WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM avaliacoes")
    void deleteAll();
}
