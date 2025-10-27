package com.example.textview.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.textview.models.StudyTask;

import java.util.List;

@Dao
public interface StudyTaskDao {
    @Insert
    void insert(StudyTask task);

    @Update
    void update(StudyTask task);

    @Delete
    void delete(StudyTask task);

    @Query("SELECT * FROM study_tasks ORDER BY dataCriacao DESC")
    LiveData<List<StudyTask>> getAllTasks();

    @Query("SELECT * FROM study_tasks ORDER BY dataCriacao DESC")
    List<StudyTask> getAllTasksSync();

    @Query("SELECT * FROM study_tasks WHERE materiaId = :materiaId ORDER BY dataCriacao DESC")
    List<StudyTask> getTasksByMateriaSync(long materiaId);

    @Query("SELECT * FROM study_tasks WHERE id = :id")
    LiveData<StudyTask> getTaskById(long id);

    @Query("SELECT * FROM study_tasks WHERE id = :id")
    StudyTask getTaskByIdSync(long id);

    @Query("SELECT COUNT(*) FROM study_tasks WHERE completada = 0")
    int getIncompleteTasksCount();

    @Query("DELETE FROM study_tasks WHERE materiaId = :materiaId")
    void deleteTasksByMateria(long materiaId);
}
