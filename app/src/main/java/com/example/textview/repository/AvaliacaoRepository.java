package com.example.textview.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.textview.database.AppDatabase;
import com.example.textview.database.AvaliacaoDao;
import com.example.textview.models.Avaliacao;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AvaliacaoRepository {
    private AvaliacaoDao avaliacaoDao;
    private LiveData<List<Avaliacao>> allAvaliacoes;
    private ExecutorService executorService;

    public AvaliacaoRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        avaliacaoDao = database.avaliacaoDao();
        allAvaliacoes = avaliacaoDao.getAllAvaliacoes();
        executorService = Executors.newSingleThreadExecutor();
    }

    public AvaliacaoRepository(AvaliacaoDao dao) {
        this.avaliacaoDao = dao;
        this.allAvaliacoes = dao.getAllAvaliacoes();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Avaliacao avaliacao) {
        executorService.execute(() -> avaliacaoDao.insert(avaliacao));
    }

    public void update(Avaliacao avaliacao) {
        executorService.execute(() -> avaliacaoDao.update(avaliacao));
    }

    public void delete(Avaliacao avaliacao) {
        executorService.execute(() -> avaliacaoDao.delete(avaliacao));
    }

    public LiveData<List<Avaliacao>> getAllAvaliacoes() {
        return allAvaliacoes;
    }

    public LiveData<List<Avaliacao>> getAvaliacoesAgendadas() {
        return avaliacaoDao.getAvaliacoesAgendadas();
    }

    public LiveData<List<Avaliacao>> getAvaliacoesByMateria(long materiaId) {
        return avaliacaoDao.getAvaliacoesByMateria(materiaId);
    }

    public LiveData<List<Avaliacao>> getAvaliacoesByDate(String date) {
        return avaliacaoDao.getAvaliacoesByDate(date);
    }

    public List<Avaliacao> getAllSync() {
        return avaliacaoDao.getAllAvaliacoesSync();
    }

    public List<Avaliacao> getAgendadasSync() {
        return avaliacaoDao.getAvaliacoesAgendadasSync();
    }

    public void deleteById(long id) {
        executorService.execute(() -> avaliacaoDao.deleteById(id));
    }
}
