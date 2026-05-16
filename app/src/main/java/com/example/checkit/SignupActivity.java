package com.example.checkit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etUsername, etPassword, etConfirmPassword;
    private MaterialButton btnSignup;
    private TextView tvLoginPrompt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
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
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Input Validation
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Check if username is unique in Firestore
        db.collection("users").whereEqualTo("username", username).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Username already exists
                                Toast.makeText(SignupActivity.this, "Username is already taken.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Username is available, now 2. Check if email is unique in Firestore
                                db.collection("users").whereEqualTo("email", email).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    if (!task.getResult().isEmpty()) {
                                                        // Email already exists
                                                        Toast.makeText(SignupActivity.this, "Email is already registered. Please log in.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Both unique, proceed with Firebase Auth
                                                        createUser(email, username, password);
                                                    }
                                                } else {
                                                    Toast.makeText(SignupActivity.this, "Error checking email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Error checking username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser(String email, String username, String password) {
        // Firebase User Creation
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserToFirestore(email, username, password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String email, String username, String password) {
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);

        db.collection("users").document(userId).set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // After Firestore is updated, run the remaining success logic
                        
                        // Extract default name from email (or use username)
                        String defaultName = username;
                        
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
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
