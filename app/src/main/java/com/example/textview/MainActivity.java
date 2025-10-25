package com.example.textview;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.textview.fragments.CalendarFragment;
import com.example.textview.fragments.HomeFragment;
import com.example.textview.fragments.ProfileFragment;
import com.example.textview.fragments.SubjectsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigation;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupDrawer();
        setupBottomNavigation();

        // Load initial fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        navView = findViewById(R.id.nav_view);

        ImageButton btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        ImageButton btnNotifications = findViewById(R.id.btn_notifications);
        btnNotifications.setOnClickListener(v -> {
            // TODO: Implement notifications
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
                // TODO: Open settings
            } else if (id == R.id.nav_about) {
                // TODO: Show about dialog
            } else if (id == R.id.nav_logout) {
                // TODO: Handle logout
                // Restart app or go to login
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
