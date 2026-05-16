package com.example.checkit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AccountMenuActivity extends AppCompatActivity {

    private TextView tvGreeting, tvEmail;
    private LinearLayout accountListContainer, collapsibleAccountArea;
    private View headerSwitchAccount, btnAddAccount;
    private ImageView ivSwitchArrow;
    private MaterialButton btnViewProfile;
    private FirebaseAuth mAuth;
    private Gson gson;
    private boolean isAccountAreaExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        mAuth = FirebaseAuth.getInstance();
        gson = new Gson();

        // Hide action bar for full screen feel
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvGreeting = findViewById(R.id.tv_greeting);
        tvEmail = findViewById(R.id.tv_email);
        accountListContainer = findViewById(R.id.accountListContainer);
        collapsibleAccountArea = findViewById(R.id.collapsibleAccountArea);
        headerSwitchAccount = findViewById(R.id.headerSwitchAccount);
        btnAddAccount = findViewById(R.id.btnAddAccount);
        ivSwitchArrow = findViewById(R.id.iv_switch_arrow);
        btnViewProfile = findViewById(R.id.btn_view_profile);

        ImageView ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(v -> finish());

        // Close menu and return to main screen
        ImageView ivMainProfile = findViewById(R.id.iv_main_profile);
        ivMainProfile.setOnClickListener(v -> finish());

        // Navigate to Profile Details
        btnViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AccountMenuActivity.this, UserInfoActivity.class);
            startActivity(intent);
        });

        // Toggle collapsible account section
        headerSwitchAccount.setOnClickListener(v -> toggleAccountArea());

        btnAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AccountMenuActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        MaterialButton btnDevInfo = findViewById(R.id.btn_dev_info);
        btnDevInfo.setOnClickListener(v -> {
            Intent intent = new Intent(AccountMenuActivity.this, DevInfoActivity.class);
            startActivity(intent);
        });
    }

    private void toggleAccountArea() {
        isAccountAreaExpanded = !isAccountAreaExpanded;
        if (isAccountAreaExpanded) {
            collapsibleAccountArea.setVisibility(View.VISIBLE);
            ivSwitchArrow.animate().rotation(180f).setDuration(200).start();
        } else {
            collapsibleAccountArea.setVisibility(View.GONE);
            ivSwitchArrow.animate().rotation(0f).setDuration(200).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
        populateSavedAccounts();
    }

    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String name = prefs.getString("name", "User");
        String email = prefs.getString("email", "user@example.com");

        tvGreeting.setText("Hi, " + name + "!");
        tvEmail.setText(email);
    }

    private void populateSavedAccounts() {
        accountListContainer.removeAllViews();
        
        SharedPreferences allAccountsPrefs = getSharedPreferences("AllAccounts", MODE_PRIVATE);
        String json = allAccountsPrefs.getString("accounts_list", null);
        Type type = new TypeToken<ArrayList<SavedAccount>>() {}.getType();
        List<SavedAccount> savedAccounts = gson.fromJson(json, type);

        if (savedAccounts != null) {
            String currentUserEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "";
            
            for (SavedAccount account : savedAccounts) {
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_saved_account, accountListContainer, false);
                
                TextView tvName = itemView.findViewById(R.id.tv_account_name);
                TextView tvEmailItem = itemView.findViewById(R.id.tv_account_email);
                
                tvName.setText(account.getName());
                tvName.setTextColor(Color.parseColor("#1D1B20"));
                
                tvEmailItem.setText(account.getEmail());
                tvEmailItem.setTextColor(Color.parseColor("#49454F"));
                
                if (account.getEmail().equalsIgnoreCase(currentUserEmail)) {
                    itemView.setEnabled(false);
                    itemView.setAlpha(0.5f);
                } else {
                    itemView.setOnClickListener(v -> switchAccount(account));
                }
                
                accountListContainer.addView(itemView);
            }
        }
    }

    private void switchAccount(SavedAccount account) {
        mAuth.signInWithEmailAndPassword(account.getEmail(), account.getPassword())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("email", account.getEmail());
                        editor.putString("name", account.getName());
                        editor.apply();

                        Toast.makeText(AccountMenuActivity.this, "Switched to " + account.getName(), Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent(AccountMenuActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(AccountMenuActivity.this, "Switch failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}