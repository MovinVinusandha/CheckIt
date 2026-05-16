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

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignupPrompt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etEmail = findViewById(R.id.et_username); // Using the username field as email
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignupPrompt = findViewById(R.id.tv_signup_prompt);

        // Set HTML text for signup prompt
        tvSignupPrompt.setText(Html.fromHtml(getString(R.string.signup_prompt), Html.FROM_HTML_MODE_LEGACY));

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Signup prompt click listener
        tvSignupPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignupActivity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Input Validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Sign In
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Save email to SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", email);

                            // Check if name exists or is placeholder
                            String existingName = prefs.getString("name", "Alex Rivers");
                            String defaultName = email.split("@")[0];
                            if (existingName.equals("Alex Rivers") || existingName.isEmpty()) {
                                editor.putString("name", defaultName);
                            }
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

                            Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}