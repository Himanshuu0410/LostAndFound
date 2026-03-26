package com.example.lostandfound.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lostandfound.DatabaseHelper;
import com.example.lostandfound.R;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout   layoutWelcome, layoutLoginForm;
    private EditText       etEmail, etPassword;
    private Button         btnShowLogin, btnShowRegister, btnLogin;
    private TextView       tvError;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        layoutWelcome   = findViewById(R.id.layoutWelcome);
        layoutLoginForm = findViewById(R.id.layoutLoginForm);
        etEmail         = findViewById(R.id.etEmail);
        etPassword      = findViewById(R.id.etPassword);
        btnShowLogin    = findViewById(R.id.btnShowLogin);
        btnShowRegister = findViewById(R.id.btnShowRegister);
        btnLogin        = findViewById(R.id.btnLogin);
        tvError         = findViewById(R.id.tvError);

        btnShowLogin.setOnClickListener(v -> {
            layoutWelcome.setVisibility(View.GONE);
            layoutLoginForm.setVisibility(View.VISIBLE);
            layoutLoginForm.setAlpha(0f);
            layoutLoginForm.animate().alpha(1f).translationY(0).setDuration(350).start();
        });

        btnShowRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        btnLogin.setOnClickListener(v -> attemptLogin());

        TextView tvGoRegister = findViewById(R.id.tvGoRegister);
        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        TextView tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> {
            layoutLoginForm.setVisibility(View.GONE);
            layoutWelcome.setVisibility(View.VISIBLE);
        });
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showError("Please enter email and password");
            shakeForm();
            return;
        }

        if (db.checkUser(email, password)) {
            // Save session instantly using commit() not apply()
            SharedPreferences prefs = getSharedPreferences("LFSession", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", email);
            editor.putString("name", db.getUserName(email));
            editor.putBoolean("loggedIn", true);
            editor.commit(); // commit() saves instantly, apply() is async

            btnLogin.setText("Signing in…");
            btnLogin.setEnabled(false);
            btnLogin.postDelayed(() -> {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 700);
        } else {
            showError("Invalid email or password");
            shakeForm();
        }
    }

    private void showError(String msg) {
        tvError.setVisibility(View.VISIBLE);
        tvError.setText("⚠  " + msg);
    }

    private void shakeForm() {
        layoutLoginForm.animate()
                .translationX(18).setDuration(60)
                .withEndAction(() -> layoutLoginForm.animate().translationX(-18).setDuration(60)
                        .withEndAction(() -> layoutLoginForm.animate().translationX(0).setDuration(60)));
    }
}