package com.example.mentesbrilhantes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private Button btnBiometric, btnLoginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btnBiometric = findViewById(R.id.btnBiometric);
        btnLoginEmail = findViewById(R.id.btnLoginEmail);

        SharedPreferences sharedPreferences   = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        if(email == null || password == null){
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else {
            // Configura a biometria
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG |
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    != BiometricManager.BIOMETRIC_SUCCESS) {
                btnBiometric.setEnabled(false);
            }

            Executor executor = ContextCompat.getMainExecutor(this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(
                    this,
                    executor,
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);

                            // 1) Ler credenciais atuais do SharedPreferences
                            SharedPreferences sp = getSharedPreferences("user_preferences", MODE_PRIVATE);
                            String email = sp.getString("email", null);
                            String senha = sp.getString("password", null);

                            if (email == null || senha == null) {
                                Toast.makeText(LoginActivity.this,
                                        "Credenciais não encontradas. Faça login por e-mail primeiro.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            // 2) Chamar a API PHP usando o LoginManager
                            LoginManager.login(LoginActivity.this, email, senha,
                                    new LoginManager.LoginCallback() {
                                        @Override
                                        public void onSuccess(JSONObject user) {
                                            String name = user.optString("name", "Usuário");
                                            String mail = user.optString("email", email);
                                            Toast.makeText(LoginActivity.this,
                                                    "Bem-vindo, " + name, Toast.LENGTH_SHORT).show();

                                            // Abre próxima tela
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.putExtra("USER_NAME", name);
                                            intent.putExtra("USER_EMAIL", mail);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            Toast.makeText(LoginActivity.this,
                                                    errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Toast.makeText(LoginActivity.this,
                                    "Biometria não reconhecida", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Login Biométrico")
                    .setSubtitle("Autentique-se para entrar")
                    .setAllowedAuthenticators(
                            BiometricManager.Authenticators.BIOMETRIC_STRONG |
                                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build();

            // Botão: inicia a autenticação biométrica
            btnBiometric.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));

            // Opcional: botão para login manual por e-mail/senha
            btnLoginEmail.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity2.class));
                finish();
            });
        }
    }
}