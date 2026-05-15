package com.example.checkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

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

        // Handle the "Save" button in the bottom sheet
        View btnSave = bottomSheetView.findViewById(R.id.btn_save_task);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to save the task would go here
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }
}