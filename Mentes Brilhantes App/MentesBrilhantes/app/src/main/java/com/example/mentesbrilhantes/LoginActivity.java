package com.example.mentesbrilhantes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private ImageButton btnBiometric, btnLoginEmail;
    private SessionManager sessionManager;
    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica se já está logado
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Verifica se tem credenciais salvas e biometria disponível
        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);

        BiometricManager biometricManager = BiometricManager.from(this);
        boolean temBiometria = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                == BiometricManager.BIOMETRIC_SUCCESS;

        // Se não tem credenciais OU não tem biometria, pula para LoginActivity2
        if (email == null || password == null || !temBiometria) {
            Intent intent = new Intent(this, LoginActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Tem tudo necessário, mostra a tela de escolha
        configurarFullscreen();
        setContentView(R.layout.activity_login);

        inicializarBotoes();
        configurarBotoes(email, password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreen();
        isAnyButtonProcessing = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configurarFullscreen();
        }
    }

    private void inicializarBotoes() {
        btnBiometric = findViewById(R.id.btnBiometric);
        btnLoginEmail = findViewById(R.id.btnLoginEmail);
    }

    private void configurarBotoes(String email, String password) {
        // Mostra o botão de biometria
        btnBiometric.setVisibility(View.VISIBLE);
        configurarBiometria(email, password);

        // Botão para login manual por e-mail/senha
        configurarBotaoProtegido(btnLoginEmail, () -> {
            startActivity(new Intent(this, LoginActivity2.class));
        });
    }

    private void configurarBiometria(String email, String password) {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(
                this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        // Chama a API PHP usando RegisterActivity.login
                        RegisterActivity.login(LoginActivity.this, email, password,
                                new LoginManager.LoginCallback() {
                                    @Override
                                    public void onSuccess(JSONObject user) {
                                        // Sucesso - navegação feita pelo RegisterActivity
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

        // Configura o botão com animação
        configurarBotaoProtegido(btnBiometric, () -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }

    // Protege botao contra multiplos cliques simultaneos
    private void configurarBotaoProtegido(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isAnyButtonProcessing) {
                            isAnyButtonProcessing = true;
                            isThisButtonActive = true;
                            v.animate()
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(80)
                                    .start();
                            return true;
                        } else {
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        if (isThisButtonActive) {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            acao.run();
                                            isAnyButtonProcessing = false;
                                            isThisButtonActive = false;
                                        }
                                    })
                                    .start();
                        } else {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(80)
                                .start();

                        if (isThisButtonActive) {
                            isAnyButtonProcessing = false;
                            isThisButtonActive = false;
                        }
                        return true;
                }
                return false;
            }
        });
    }

    // Esconde barras do sistema pra tela ficar fullscreen
    private void configurarFullscreen() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}
