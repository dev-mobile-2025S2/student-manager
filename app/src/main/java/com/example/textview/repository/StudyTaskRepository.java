package com.example.textview.repository;

import androidx.lifecycle.LiveData;

import com.example.textview.database.StudyTaskDao;
import com.example.textview.models.StudyTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudyTaskRepository {
    private StudyTaskDao studyTaskDao;
    private LiveData<List<StudyTask>> allTasks;
    private ExecutorService executorService;

    public StudyTaskRepository(StudyTaskDao dao) {
        this.studyTaskDao = dao;
        this.allTasks = dao.getAllTasks();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(StudyTask task) {
        executorService.execute(() -> studyTaskDao.insert(task));
    }

    public void update(StudyTask task) {
        executorService.execute(() -> studyTaskDao.update(task));
    }

    public void delete(StudyTask task) {
        executorService.execute(() -> studyTaskDao.delete(task));
    }

    public LiveData<List<StudyTask>> getAllTasks() {
        return allTasks;
    }

    public List<StudyTask> getAllTasksSync() {
        return studyTaskDao.getAllTasksSync();
    }

    public List<StudyTask> getTasksByMateriaSync(long materiaId) {
        return studyTaskDao.getTasksByMateriaSync(materiaId);
    }

    public LiveData<StudyTask> getTaskById(long id) {
        return studyTaskDao.getTaskById(id);
    }

    public StudyTask getTaskByIdSync(long id) {
        return studyTaskDao.getTaskByIdSync(id);
    }

    public int getIncompleteTasksCount() {
        return studyTaskDao.getIncompleteTasksCount();
    }
}
