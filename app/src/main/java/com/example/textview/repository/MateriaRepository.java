package com.example.textview.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.textview.database.AppDatabase;
import com.example.textview.database.MateriaDao;
import com.example.textview.models.Materia;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MateriaRepository {
    private MateriaDao materiaDao;
    private LiveData<List<Materia>> allMaterias;
    private ExecutorService executorService;

    public MateriaRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        materiaDao = database.materiaDao();
        allMaterias = materiaDao.getAllMaterias();
        executorService = Executors.newSingleThreadExecutor();
    }

    public MateriaRepository(MateriaDao dao) {
        this.materiaDao = dao;
        this.allMaterias = dao.getAllMaterias();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Materia materia) {
        executorService.execute(() -> materiaDao.insert(materia));
    }

    public void update(Materia materia) {
        executorService.execute(() -> materiaDao.update(materia));
    }

    public void delete(Materia materia) {
        executorService.execute(() -> materiaDao.delete(materia));
    }

    public LiveData<List<Materia>> getAllMaterias() {
        return allMaterias;
    }

    public LiveData<Materia> getMateriaById(long id) {
        return materiaDao.getMateriaById(id);
    }

    public List<Materia> getAllSync() {
        return materiaDao.getAllMateriasSync();
    }

    public Materia getMateriaByIdSync(long id) {
        return materiaDao.getMateriaByIdSync(id);
    }
}
