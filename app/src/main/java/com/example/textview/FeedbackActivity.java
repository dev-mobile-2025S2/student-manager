package com.example.textview;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class FeedbackActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etFeedbackName, etFeedbackEmail, etFeedbackMessage;
    private MaterialButton btnSendFeedback, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etFeedbackName = findViewById(R.id.et_feedback_name);
        etFeedbackEmail = findViewById(R.id.et_feedback_email);
        etFeedbackMessage = findViewById(R.id.et_feedback_message);
        btnSendFeedback = findViewById(R.id.btn_send_feedback);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCancel.setOnClickListener(v -> finish());

        btnSendFeedback.setOnClickListener(v -> {
            String message = etFeedbackMessage.getText() != null ?
                    etFeedbackMessage.getText().toString().trim() : "";

            if (message.isEmpty()) {
                Toast.makeText(this, "Por favor, escreva uma mensagem", Toast.LENGTH_SHORT).show();
                etFeedbackMessage.requestFocus();
                return;
            }

            sendFeedback();
        });
    }

    private void sendFeedback() {
        String name = etFeedbackName.getText() != null ?
                etFeedbackName.getText().toString().trim() : "Anônimo";
        String email = etFeedbackEmail.getText() != null ?
                etFeedbackEmail.getText().toString().trim() : "Não informado";
        String message = etFeedbackMessage.getText() != null ?
                etFeedbackMessage.getText().toString().trim() : "";

        // TODO: In a real application, you would send this feedback to a backend server
        // For now, we'll just show a success message

        new AlertDialog.Builder(this)
                .setTitle("Feedback Enviado!")
                .setMessage("Obrigado pelo seu feedback! Sua opinião é muito importante para nós.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Clear fields
                    etFeedbackName.setText("");
                    etFeedbackEmail.setText("");
                    etFeedbackMessage.setText("");
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
