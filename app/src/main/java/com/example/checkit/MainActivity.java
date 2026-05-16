package com.example.checkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TextView textEmptyState;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            // Fallback: Redirect to login if user session is lost
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        textEmptyState = findViewById(R.id.textEmptyState);

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

        // Initialize Task List
        taskList = new ArrayList<>();

        // Setup RecyclerView
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, this);
        tasksRecyclerView.setAdapter(taskAdapter);

        // Load Task List from Firestore
        loadTasks();

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
        EditText etTaskCategory = bottomSheetView.findViewById(R.id.editTextCategory);
        View iconAddTag = bottomSheetView.findViewById(R.id.iconAddTag);
        View btnSave = bottomSheetView.findViewById(R.id.btn_save_task);

        // If editing, pre-fill the text
        if (position != -1) {
            Task task = taskList.get(position);
            etTaskDetails.setText(task.getTitle());
            if (task.getSubtitle() != null && !task.getSubtitle().isEmpty() && !task.getSubtitle().equals("General")) {
                etTaskCategory.setText(task.getSubtitle());
                etTaskCategory.setVisibility(View.VISIBLE);
            }
        }

        iconAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etTaskCategory.setVisibility(View.VISIBLE);
                etTaskCategory.requestFocus();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = etTaskDetails.getText().toString().trim();
                String taskCategory = etTaskCategory.getText().toString().trim();
                if (taskCategory.isEmpty()) {
                    taskCategory = "General";
                }

                if (!taskTitle.isEmpty()) {
                    if (position == -1) {
                        // Create new task and add to Firestore
                        Task newTask = new Task(taskTitle, taskCategory, false);
                        db.collection("users").document(userId).collection("tasks")
                                .add(newTask)
                                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error adding task", Toast.LENGTH_SHORT).show());
                    } else {
                        // Update existing task in Firestore
                        Task task = taskList.get(position);
                        task.setTitle(taskTitle);
                        task.setSubtitle(taskCategory);
                        db.collection("users").document(userId).collection("tasks").document(task.getTaskId())
                                .set(task)
                                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error updating task", Toast.LENGTH_SHORT).show());
                    }
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void loadTasks() {
        db.collection("users").document(userId).collection("tasks")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Error loading tasks", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        taskList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            Task task = doc.toObject(Task.class);
                            taskList.add(task);
                        }
                        taskAdapter.notifyDataSetChanged();
                        checkEmptyState();
                    }
                });
    }

    private void checkEmptyState() {
        if (taskList.isEmpty()) {
            tasksRecyclerView.setVisibility(View.GONE);
            textEmptyState.setVisibility(View.VISIBLE);
        } else {
            tasksRecyclerView.setVisibility(View.VISIBLE);
            textEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEditClick(int position) {
        showNewTaskBottomSheet(position);
    }

    @Override
    public void onDeleteClick(int position) {
        String taskId = taskList.get(position).getTaskId();
        db.collection("users").document(userId).collection("tasks").document(taskId)
                .delete()
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error deleting task", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onTaskChecked(int position, boolean isChecked) {
        Task task = taskList.get(position);
        task.setCompleted(isChecked);
        String taskId = task.getTaskId();
        
        db.collection("users").document(userId).collection("tasks").document(taskId)
                .set(task)
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error updating status", Toast.LENGTH_SHORT).show());
    }
}
