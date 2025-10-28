package com.example.textview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textview.MainActivity;
import com.example.textview.R;
import com.example.textview.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SharedPrefManager
        sharedPrefManager = new SharedPrefManager(this);

        // Check if user is already logged in
        if (sharedPrefManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        // Initialize views
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // Set up click listeners
        btnLogin.setOnClickListener(v -> performLogin());

        findViewById(R.id.tv_signup).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Get input values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("E-mail é obrigatório");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Senha é obrigatória");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Check if user exists
        if (!sharedPrefManager.emailExists(email)) {
            tilEmail.setError("Usuário não encontrado");
            Toast.makeText(this, "Usuário não encontrado. Por favor, cadastre-se.", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate credentials
        if (sharedPrefManager.validateLogin(email, password)) {
            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            tilPassword.setError("Senha incorreta");
            Toast.makeText(this, "Senha incorreta. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
