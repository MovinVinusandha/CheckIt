package com.example.checkit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etJobTitle, etEmail;
    private AutoCompleteTextView actTimezone;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etFullName = findViewById(R.id.et_full_name);
        etJobTitle = findViewById(R.id.et_job_title);
        etEmail = findViewById(R.id.et_email);
        actTimezone = findViewById(R.id.act_timezone);

        // Make email read-only
        etEmail.setEnabled(false);
        etEmail.setFocusable(false);

        // Pre-fill data
        etFullName.setText(prefs.getString("name", "Alex Rivers"));
        etJobTitle.setText(prefs.getString("title", "Senior Design Architect"));
        etEmail.setText(prefs.getString("email", "alexrivers@gmail.com"));
        actTimezone.setText(prefs.getString("timezone", "Pacific Standard Time (GMT-8)"), false);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        MaterialButton btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        MaterialButton btnDiscard = findViewById(R.id.btn_discard);
        btnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveProfile() {
        String name = etFullName.getText().toString().trim();
        String title = etJobTitle.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String timezone = actTimezone.getText().toString().trim();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("title", title);
        editor.putString("email", email);
        editor.putString("timezone", timezone);
        editor.apply();

        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}