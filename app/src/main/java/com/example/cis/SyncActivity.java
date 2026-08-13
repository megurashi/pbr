package com.example.cis;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.cis.databinding.ActivitySyncBinding;

public class SyncActivity extends AppCompatActivity {

    private ActivitySyncBinding binding;
    private boolean isCancelled = false;
    private final String CHANNEL_ID = "sync_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivitySyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnBatalkan.setOnClickListener(v -> {
            isCancelled = true;
            finish();
        });

        startSyncProcess();
    }

    private void startSyncProcess() {
        // Run a simulated background task for 5 seconds smoothly
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) { // Update 1 percent at a time
                if (isCancelled) return;
                
                final int progress = i;
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    binding.progressBarSync.setProgress(progress);
                    binding.tvProgress.setText(progress + "%");
                });

                try {
                    Thread.sleep(50); // 100 * 50ms = 5000ms (5 seconds)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!isCancelled) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    sendNotification();
                    finish();
                });
            }
        }).start();
    }

    private void sendNotification() {
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean notifEnabled = prefs.getBoolean("notif", true);
        
        if (!notifEnabled) return;

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sync Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check) // using our custom icon
                .setContentTitle("Sinkronisasi Berhasil")
                .setContentText("Semua aktivitas berhasil diperbarui.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            isCancelled = true;
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
