package com.example.checkit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class UserInfoActivity extends AppCompatActivity {

    private TextView tvName, tvTitle, tvEmail, tvTimezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvName = findViewById(R.id.tv_user_name);
        tvTitle = findViewById(R.id.tv_job_title);
        tvEmail = findViewById(R.id.user_email_value);
        tvTimezone = findViewById(R.id.timezone_value);

        MaterialButton btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        MaterialButton btnSignOut = findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase Sign-out
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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
        tvName.setText(prefs.getString("name", "Your Name"));
        tvTitle.setText(prefs.getString("title", "Your Title"));
        tvEmail.setText(prefs.getString("email", "yourname@example.com"));
        tvTimezone.setText(prefs.getString("timezone", "UTC"));
    }
}