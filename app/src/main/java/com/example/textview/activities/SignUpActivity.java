package com.example.textview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textview.MainActivity;
import com.example.textview.R;
import com.example.textview.models.User;
import com.example.textview.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnSignUp;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize SharedPrefManager
        sharedPrefManager = new SharedPrefManager(this);

        // Initialize views
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        btnSignUp = findViewById(R.id.btn_signup);

        // Set up click listeners
        btnSignUp.setOnClickListener(v -> performSignUp());

        findViewById(R.id.tv_login).setOnClickListener(v -> {
            finish(); // Go back to login
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish(); // Go back
        });
    }

    private void performSignUp() {
        // Clear previous errors
        tilFullName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Get input values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Nome completo é obrigatório");
            isValid = false;
        } else if (fullName.length() < 3) {
            tilFullName.setError("Nome deve ter pelo menos 3 caracteres");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("E-mail é obrigatório");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            isValid = false;
        } else if (sharedPrefManager.emailExists(email)) {
            tilEmail.setError("Este e-mail já está cadastrado");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Senha é obrigatória");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Senha deve ter pelo menos 6 caracteres");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Confirmação de senha é obrigatória");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("As senhas não coincidem");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Create new user
        User newUser = new User(fullName, email, password);

        // Register user (but don't log in yet)
        sharedPrefManager.registerUser(newUser);

        Toast.makeText(this, "Cadastro realizado com sucesso! Faça login para continuar.", Toast.LENGTH_LONG).show();

        // Navigate to login activity
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
