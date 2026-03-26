package com.example.lostandfound.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lostandfound.DatabaseHelper;
import com.example.lostandfound.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText       etFullName, etEmail, etPassword, etConfirmPass;
    private Button         btnRegister;
    private TextView       tvError, tvBack;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db            = new DatabaseHelper(this);
        etFullName    = findViewById(R.id.etFullName);
        etEmail       = findViewById(R.id.etEmail);
        etPassword    = findViewById(R.id.etPassword);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnRegister   = findViewById(R.id.btnRegister);
        tvError       = findViewById(R.id.tvError);
        tvBack        = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name     = etFullName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etConfirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showError("Please fill in all fields");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirm)) {
            showError("Passwords do not match");
            return;
        }

        boolean success = db.registerUser(name, email, password);

        if (success) {
            Toast.makeText(this, "Account created! Please log in.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            showError("Email already registered. Try logging in.");
        }
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText("⚠  " + msg);
    }
}
