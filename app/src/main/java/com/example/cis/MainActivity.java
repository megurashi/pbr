package com.example.cis;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import com.example.cis.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentMenuId = R.id.navigation_dashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            currentMenuId = item.getItemId();
            Fragment selectedFragment = null;
            String title = "";

            if (item.getItemId() == R.id.navigation_dashboard) {
                selectedFragment = new DashboardFragment();
                title = "Task Reminder";
            } else if (item.getItemId() == R.id.navigation_tasks) {
                selectedFragment = new TaskFragment();
                title = "Data Aktivitas";
            } else if (item.getItemId() == R.id.navigation_settings) {
                selectedFragment = new SettingsFragment();
                title = "Pengaturan";
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
                invalidateOptionsMenu(); // Refresh toolbar menu
                return true;
            }
            return false;
        });

        // Default open Dashboard
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // We will inflate a menu containing both + and 3 dots, and show/hide based on fragment
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem addMenu = menu.findItem(R.id.action_add);
        MenuItem syncMenu = menu.findItem(R.id.action_sync);
        
        if (currentMenuId == R.id.navigation_dashboard) {
            if(addMenu != null) addMenu.setVisible(false);
            if(syncMenu != null) syncMenu.setVisible(true);
        } else if (currentMenuId == R.id.navigation_tasks) {
            if(addMenu != null) addMenu.setVisible(true);
            if(syncMenu != null) syncMenu.setVisible(false);
        } else {
            if(addMenu != null) addMenu.setVisible(false);
            if(syncMenu != null) syncMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            // Fragment will handle or we broadcast to fragment
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if(currentFragment instanceof TaskFragment) {
                ((TaskFragment) currentFragment).onAddClicked();
            }
            return true;
        } else if (item.getItemId() == R.id.action_sync) {
            // Dropdown option clicked -> open sync
            android.content.Intent intent = new android.content.Intent(this, SyncActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
