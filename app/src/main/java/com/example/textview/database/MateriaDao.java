package com.example.textview.database;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.textview.models.Materia;
import java.util.List;

@Dao
public interface MateriaDao {

    @Insert
    long insert(Materia materia);

    @Update
    void update(Materia materia);

    @Delete
    void delete(Materia materia);

    @Query("SELECT * FROM materias ORDER BY nome ASC")
    LiveData<List<Materia>> getAllMaterias();

    @Query("SELECT * FROM materias WHERE id = :id")
    LiveData<Materia> getMateriaById(long id);

    @Query("SELECT * FROM materias WHERE id = :id")
    Materia getMateriaByIdSync(long id);

    @Query("SELECT * FROM materias ORDER BY nome ASC")
    List<Materia> getAllMateriasSync();

    @Query("DELETE FROM materias")
    void deleteAll();
}
