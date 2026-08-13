package com.example.cis;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cis.data.AppDatabase;
import com.example.cis.data.Task;
import com.example.cis.databinding.FragmentTaskBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskFragment extends Fragment {

    private FragmentTaskBinding binding;
    private TaskAdapter adapter;
    private List<Task> taskList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TaskAdapter(taskList, task -> {
            // Edit Task
            Intent intent = new Intent(requireContext(), TaskFormActivity.class);
            intent.putExtra("TASK_ID", task.id);
            startActivity(intent);
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        android.content.Context context = getContext();
        if (context == null) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> tasks = AppDatabase.getInstance(context).taskDao().getAll();
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                if (!isAdded() || binding == null) return;
                
                taskList.clear();
                taskList.addAll(tasks);
                adapter.notifyDataSetChanged();
                
                if (taskList.isEmpty()) {
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmpty.setVisibility(View.GONE);
                }
            });
        });
    }

    public void onAddClicked() {
        Intent intent = new Intent(requireContext(), TaskFormActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
