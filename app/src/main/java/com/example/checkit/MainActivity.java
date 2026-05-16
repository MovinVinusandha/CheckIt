package com.example.checkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("checkit_prefs", Context.MODE_PRIVATE);
        gson = new Gson();

        // Hide action bar for a custom header look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView ivUserProfile = findViewById(R.id.iv_user_profile);
        ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AccountMenuActivity
                Intent intent = new Intent(MainActivity.this, AccountMenuActivity.class);
                startActivity(intent);
            }
        });

        // Load Task List from persistence
        loadTasks();

        // Setup RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        // Initialize FAB and set click listener to show bottom sheet
        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewTaskBottomSheet(-1); // -1 indicates a new task
            }
        });
    }

    private void showNewTaskBottomSheet(final int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_new_task, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText etTaskDetails = bottomSheetView.findViewById(R.id.et_task_details);
        View btnSave = bottomSheetView.findViewById(R.id.btn_save_task);

        // If editing, pre-fill the text
        if (position != -1) {
            etTaskDetails.setText(taskList.get(position).getTitle());
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = etTaskDetails.getText().toString().trim();
                if (!taskTitle.isEmpty()) {
                    if (position == -1) {
                        // Create new task and add to list
                        Task newTask = new Task(taskTitle, "General", false);
                        taskList.add(0, newTask); // Add to top
                        taskAdapter.notifyItemInserted(0);
                        tasksRecyclerView.scrollToPosition(0);
                    } else {
                        // Update existing task
                        Task task = taskList.get(position);
                        task.setTitle(taskTitle);
                        taskAdapter.notifyItemChanged(position);
                    }
                    saveTasks();
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(taskList);
        editor.putString("tasks_list", json);
        editor.apply();
    }

    private void loadTasks() {
        String json = sharedPreferences.getString("tasks_list", null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        taskList = gson.fromJson(json, type);

        if (taskList == null) {
            taskList = new ArrayList<>();
            // Add some initial dummy data if list is empty for the first time
            taskList.add(new Task("Weekly Grocery Run", "Household", false));
            taskList.add(new Task("Morning Meditation", "Daily Routine", false));
            taskList.add(new Task("Check emails", "Work", true));
            saveTasks();
        }
    }

    @Override
    public void onEditClick(int position) {
        showNewTaskBottomSheet(position);
    }

    @Override
    public void onDeleteClick(int position) {
        taskList.remove(position);
        taskAdapter.notifyItemRemoved(position);
        saveTasks();
    }

    @Override
    public void onTaskChecked(int position, boolean isChecked) {
        taskList.get(position).setCompleted(isChecked);
        taskAdapter.notifyItemChanged(position);
        saveTasks();
    }
}
