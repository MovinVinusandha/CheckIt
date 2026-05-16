package com.example.checkit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvSignupPrompt, textForgotPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Hide action bar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        etEmail = findViewById(R.id.et_username); // Using the username field as email
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignupPrompt = findViewById(R.id.tv_signup_prompt);
        textForgotPassword = findViewById(R.id.textForgotPassword);

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Forgot Password click listener
        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPasswordDialog();
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

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your registered email address to receive a password reset link.");

        // Programmatically create EditText
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("email@example.com");

        // Add padding using a container
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(48, 20, 48, 0); // left, top, right, bottom
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Reset link sent! Check your email.", Toast.LENGTH_SHORT).show();
                            } else {
                                String error = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void loginUser() {
        String loginInput = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Input Validation
        if (loginInput.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loginInput.contains("@")) {
            // It's an email
            performSignIn(loginInput, password);
        } else {
            // It's a username, resolve to email first
            db.collection("users").whereEqualTo("username", loginInput).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                String foundEmail = task.getResult().getDocuments().get(0).getString("email");
                                performSignIn(foundEmail, password);
                            } else {
                                Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void performSignIn(String email, String password) {
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
                            String error = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                            Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
