package com.example.mentesbrilhantes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

public class LoginActivity2 extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Chama o LoginManager passando email e senha
        LoginManager.login(this, email, password, new LoginManager.LoginCallback() {
            @Override
            public void onSuccess(JSONObject user) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                // Opcional: salvar credenciais para login por biometria depois
                SharedPreferences sp = getSharedPreferences("user_preferences", MODE_PRIVATE);
                sp.edit()
                        .putString("email", email)
                        .putString("password", password)
                        .apply();

                // A navegação para MainActivity já ocorre dentro do LoginManager
                Toast.makeText(LoginActivity2.this, "Login realizado!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity2.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}