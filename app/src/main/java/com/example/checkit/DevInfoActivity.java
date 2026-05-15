package com.example.checkit;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class DevInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_info);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView ivBackProfile = findViewById(R.id.iv_back_profile);
        ivBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to previous screen
                finish();
            }
        });
    }
}