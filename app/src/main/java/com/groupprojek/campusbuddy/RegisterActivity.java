package com.groupprojek.campusbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etMatricNo, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link UI elements
        etFullName = findViewById(R.id.etFullName);
        etMatricNo = findViewById(R.id.etMatricNo);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Button register
        btnRegister.setOnClickListener(v -> registerUser());

        // Text link to Login
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String matricNo = etMatricNo.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation
        if (fullName.isEmpty() || matricNo.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase registration
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        // Simpan user info dalam Realtime Database
                        HashMap<String, Object> userData = new HashMap<>();
                        userData.put("fullName", fullName);
                        userData.put("matricNo", matricNo);
                        userData.put("email", email);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .setValue(userData)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        new androidx.appcompat.app.AlertDialog.Builder(RegisterActivity.this)
                                                .setTitle("Registration Successful")
                                                .setMessage("Your account has been created successfully!")
                                                .setPositiveButton("OK", (dialog, which) -> {
                                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    finish();
                                                })
                                                .setCancelable(false)
                                                .show();
                                    }
                                    else {
                                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}