package com.example.checkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Initialize Task List with dummy data
        taskList = new ArrayList<>();
        taskList.add(new Task("Weekly Grocery Run", "Household", false));
        taskList.add(new Task("Morning Meditation", "Daily Routine", false));
        taskList.add(new Task("Check emails", "Work", true));

        // Setup RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        tasksRecyclerView.setAdapter(taskAdapter);

        // Initialize FAB and set click listener to show bottom sheet
        FloatingActionButton fabAddTask = findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewTaskBottomSheet();
            }
        });
    }

    private void showNewTaskBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_new_task, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        EditText etTaskDetails = bottomSheetView.findViewById(R.id.et_task_details);
        View btnSave = bottomSheetView.findViewById(R.id.btn_save_task);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = etTaskDetails.getText().toString().trim();
                if (!taskTitle.isEmpty()) {
                    // Create new task and add to list
                    Task newTask = new Task(taskTitle, "General", false);
                    taskList.add(0, newTask); // Add to top
                    taskAdapter.notifyItemInserted(0);
                    tasksRecyclerView.scrollToPosition(0);
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }
}
