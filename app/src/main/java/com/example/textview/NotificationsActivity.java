package com.example.textview;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textview.adapters.AvaliacaoAdapter;
import com.example.textview.database.AppDatabase;
import com.example.textview.models.Avaliacao;
import com.example.textview.repository.AvaliacaoRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class NotificationsActivity extends AppCompatActivity {

    private TextView tvNotificationSummary, tvNotificationSubtitle, tvEmptyState;
    private RecyclerView recyclerNotifications;
    private AvaliacaoAdapter avaliacaoAdapter;
    private AvaliacaoRepository avaliacaoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        setupToolbar();
        initDatabase();
        setupRecyclerView();
        loadNotifications();
    }

    private void initViews() {
        tvNotificationSummary = findViewById(R.id.tv_notification_summary);
        tvNotificationSubtitle = findViewById(R.id.tv_notification_subtitle);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        recyclerNotifications = findViewById(R.id.recycler_notifications);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initDatabase() {
        AppDatabase database = AppDatabase.getInstance(this);
        avaliacaoRepository = new AvaliacaoRepository(database.avaliacaoDao());
    }

    private void setupRecyclerView() {
        avaliacaoAdapter = new AvaliacaoAdapter(avaliacao -> {
            // Click handler can be implemented here if needed
        });
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotifications.setAdapter(avaliacaoAdapter);
    }

    private void loadNotifications() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Avaliacao> allAvaliacoes = avaliacaoRepository.getAllSync();
            List<Avaliacao> upcomingAvaliacoes = new ArrayList<>();

            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();

            // Get evaluations in next 7 days
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            long weekFromNow = calendar.getTimeInMillis();

            int urgentCount = 0;
            int upcomingCount = 0;

            for (Avaliacao avaliacao : allAvaliacoes) {
                long avaliacaoTime = avaliacao.getDataAsDate().getTime();
                if (avaliacaoTime >= currentTime) {
                    if (avaliacaoTime <= weekFromNow) {
                        upcomingAvaliacoes.add(avaliacao);
                        upcomingCount++;

                        // Check if urgent (within 2 days)
                        Calendar urgentCalendar = Calendar.getInstance();
                        urgentCalendar.add(Calendar.DAY_OF_MONTH, 2);
                        if (avaliacaoTime <= urgentCalendar.getTimeInMillis()) {
                            urgentCount++;
                        }
                    }
                }
            }

            // Sort by date
            upcomingAvaliacoes.sort((a1, a2) -> a1.getData().compareTo(a2.getData()));

            final int finalUrgent = urgentCount;
            final int finalUpcoming = upcomingCount;
            final List<Avaliacao> finalList = upcomingAvaliacoes;

            runOnUiThread(() -> {
                updateSummary(finalUrgent, finalUpcoming);
                if (finalList.isEmpty()) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    recyclerNotifications.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    recyclerNotifications.setVisibility(View.VISIBLE);
                    avaliacaoAdapter.setAvaliacoes(finalList);
                }
            });
        });
    }

    private void updateSummary(int urgentCount, int upcomingCount) {
        if (urgentCount > 0) {
            String summary = urgentCount == 1 ?
                    getString(R.string.urgent_evaluation, urgentCount) :
                    getString(R.string.urgent_evaluations_plural, urgentCount);
            tvNotificationSummary.setText(summary);
            tvNotificationSubtitle.setText("Prepare-se para suas próximas avaliações!");
        } else if (upcomingCount > 0) {
            String summary = upcomingCount == 1 ?
                    getString(R.string.upcoming_evaluation, upcomingCount) :
                    getString(R.string.upcoming_evaluations_plural, upcomingCount);
            tvNotificationSummary.setText(summary);
            tvNotificationSubtitle.setText("Você tem avaliações esta semana");
        } else {
            tvNotificationSummary.setText(R.string.all_clear);
            tvNotificationSubtitle.setText(R.string.no_upcoming_evaluations);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
