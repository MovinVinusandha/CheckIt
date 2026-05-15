package com.example.checkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class AccountMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        // Hide action bar for full screen feel
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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
}