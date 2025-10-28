package com.example.textview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.textview.activities.LoginActivity;
import com.example.textview.fragments.AboutFragment;
import com.example.textview.fragments.CalendarFragment;
import com.example.textview.fragments.HomeFragment;
import com.example.textview.fragments.ProfileFragment;
import com.example.textview.fragments.StudyFragment;
import com.example.textview.fragments.SubjectsFragment;
import com.example.textview.models.User;
import com.example.textview.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigation;
    private NavigationView navView;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = new SharedPrefManager(this);

        initViews();
        setupDrawer();
        setupBottomNavigation();
        setupBackPressedHandler();

        // Load initial fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data every time activity resumes
        loadUserDataInDrawer();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        navView = findViewById(R.id.nav_view);

        ImageButton btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        ImageButton btnNotifications = findViewById(R.id.btn_notifications);
        btnNotifications.setOnClickListener(v -> {
            // Open Notifications Activity
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        });
    }

    private void setupDrawer() {
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Handle drawer navigation items
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                bottomNavigation.setSelectedItemId(R.id.navigation_home);
            } else if (id == R.id.nav_subjects) {
                loadFragment(new SubjectsFragment());
                bottomNavigation.setSelectedItemId(R.id.navigation_subjects);
            } else if (id == R.id.nav_register_exam) {
                // TODO: Open register exam dialog/activity
                loadFragment(new CalendarFragment());
                bottomNavigation.setSelectedItemId(R.id.navigation_calendar);
            } else if (id == R.id.nav_calendar) {
                loadFragment(new CalendarFragment());
                bottomNavigation.setSelectedItemId(R.id.navigation_calendar);
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                bottomNavigation.setSelectedItemId(R.id.navigation_profile);
            } else if (id == R.id.nav_settings) {
                // Open settings - will be implemented as a dialog or new screen
                Toast.makeText(this, "Configurações em desenvolvimento", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about) {
                // Open About Fragment
                loadFragment(new AboutFragment());
            } else if (id == R.id.nav_logout) {
                // Handle logout with confirmation dialog
                showLogoutDialog();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.navigation_subjects) {
                fragment = new SubjectsFragment();
            } else if (id == R.id.navigation_study) {
                fragment = new StudyFragment();
            } else if (id == R.id.navigation_calendar) {
                fragment = new CalendarFragment();
            } else if (id == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            }

            return loadFragment(fragment);
        });
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da Conta")
                .setMessage("Deseja realmente sair da sua conta?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void performLogout() {
        sharedPrefManager.logout();

        Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadUserDataInDrawer() {
        // Post to ensure header view is fully inflated
        navView.post(() -> {
            View headerView = navView.getHeaderView(0);
            if (headerView != null) {
                TextView tvUserInitial = headerView.findViewById(R.id.tv_user_initial);
                TextView tvUserName = headerView.findViewById(R.id.tv_user_name);
                TextView tvUserEmail = headerView.findViewById(R.id.tv_user_email);

                User user = sharedPrefManager.getUser();
                if (user != null) {
                    tvUserName.setText(user.getFullName());
                    tvUserEmail.setText(user.getEmail());
                    tvUserInitial.setText(user.getInitial());
                } else {
                    tvUserName.setText("Estudante");
                    tvUserEmail.setText("estudante@email.com");
                    tvUserInitial.setText("E");
                }
            }
        });
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }
}
