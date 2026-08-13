package com.example.cis;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.cis.data.AppDatabase;
import com.example.cis.data.Task;
import com.example.cis.databinding.ActivityTaskFormBinding;

import java.util.Calendar;
import java.util.concurrent.Executors;

public class TaskFormActivity extends AppCompatActivity {

    private ActivityTaskFormBinding binding;
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityTaskFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tambah Aktivitas");
        }

        taskId = getIntent().getIntExtra("TASK_ID", -1);

        if (taskId != -1) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Aktivitas");
            }
            binding.btnHapus.setVisibility(View.VISIBLE);
            loadTaskData();
        }

        binding.etTanggal.setOnClickListener(v -> showDatePicker());

        binding.btnHapus.setOnClickListener(v -> deleteTask());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + " " + getMonthName(month) + " " + year;
            binding.etTanggal.setText(date);
        }, y, m, d);
        dialog.show();
    }

    private String getMonthName(int month) {
        String[] months = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        return months[month];
    }

    private void loadTaskData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = AppDatabase.getInstance(this).taskDao().getById(taskId);
            if (task != null) {
                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) return;
                    binding.etJudul.setText(task.judul);
                    binding.etDeskripsi.setText(task.deskripsi);
                    binding.etTanggal.setText(task.tanggal);
                });
            }
        });
    }

    private void saveTask() {
        String judul = binding.etJudul.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();
        String tanggal = binding.etTanggal.getText().toString().trim();

        if (judul.isEmpty() || deskripsi.isEmpty() || tanggal.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = new Task();
            task.judul = judul;
            task.deskripsi = deskripsi;
            task.tanggal = tanggal;

            if (taskId == -1) {
                AppDatabase.getInstance(this).taskDao().insert(task);
            } else {
                task.id = taskId;
                AppDatabase.getInstance(this).taskDao().update(task);
            }
            
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed()) finish();
            });
        });
    }

    private void deleteTask() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = new Task();
            task.id = taskId;
            AppDatabase.getInstance(this).taskDao().delete(task);
            
            runOnUiThread(() -> {
                if (!isFinishing() && !isDestroyed()) finish();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
