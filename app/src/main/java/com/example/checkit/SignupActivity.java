package com.example.checkit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnSignup;
    private TextView tvLoginPrompt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
        tvLoginPrompt = findViewById(R.id.tv_login_prompt);

        // Signup button click listener
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        // Login prompt click listener
        tvLoginPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to Login screen
                finish();
            }
        });
    }

    private void signUpUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Input Validation
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase User Creation
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Extract default name from email
                            String defaultName = email.split("@")[0];
                            
                            // Save email and name to SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", email);
                            editor.putString("name", defaultName);
                            editor.apply();

                            // Update AllAccounts list
                            SharedPreferences allAccountsPrefs = getSharedPreferences("AllAccounts", MODE_PRIVATE);
                            Gson gson = new Gson();
                            String json = allAccountsPrefs.getString("accounts_list", null);
                            Type type = new TypeToken<ArrayList<SavedAccount>>() {}.getType();
                            List<SavedAccount> savedAccounts = gson.fromJson(json, type);

                            if (savedAccounts == null) {
                                savedAccounts = new ArrayList<>();
                            }

                            // Check for duplicates
                            boolean exists = false;
                            for (SavedAccount account : savedAccounts) {
                                if (account.getEmail().equalsIgnoreCase(email)) {
                                    exists = true;
                                    // Update password if changed
                                    account.setPassword(password);
                                    break;
                                }
                            }

                            if (!exists) {
                                savedAccounts.add(new SavedAccount(email, password, defaultName));
                            }

                            String updatedJson = gson.toJson(savedAccounts);
                            allAccountsPrefs.edit().putString("accounts_list", updatedJson).apply();

                            Toast.makeText(SignupActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}