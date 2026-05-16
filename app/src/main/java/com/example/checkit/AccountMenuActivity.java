package com.example.checkit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class AccountMenuActivity extends AppCompatActivity {

    private TextView tvGreeting, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        // Hide action bar for full screen feel
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvGreeting = findViewById(R.id.tv_greeting);
        tvEmail = findViewById(R.id.tv_email);

        ImageView ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView ivMainProfile = findViewById(R.id.iv_main_profile);
        ivMainProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountMenuActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });

        MaterialButton btnDevInfo = findViewById(R.id.btn_dev_info);
        btnDevInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountMenuActivity.this, DevInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = prefs.getString("name", "User");
        String email = prefs.getString("email", "user@example.com");

        tvGreeting.setText("Hi, " + name + "!");
        tvEmail.setText(email);
    }
}