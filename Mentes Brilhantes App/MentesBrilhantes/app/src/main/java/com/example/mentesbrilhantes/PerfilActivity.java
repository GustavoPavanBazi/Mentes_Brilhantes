package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private ImageButton btnLogout, btnVoltar;
    private RadioGroup rgVoicePreference;
    private RadioButton rbVoiceFem, rbVoiceMas;
    private SessionManager sessionManager;
    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_perfil);

        sessionManager = new SessionManager(this);

        // Inicializa os componentes
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnLogout = findViewById(R.id.btn_logout);
        btnVoltar = findViewById(R.id.btn_voltar);
        rgVoicePreference = findViewById(R.id.rg_voice_preference);
        rbVoiceFem = findViewById(R.id.rb_voice_fem);
        rbVoiceMas = findViewById(R.id.rb_voice_mas);

        // Carrega dados do usuário
        carregarDadosUsuario();

        // Configura botões
        configurarBotoes();
    }

    private void carregarDadosUsuario() {
        String nomeUsuario = sessionManager.getUserName();
        String emailUsuario = sessionManager.getUserEmail();

        tvUserName.setText(nomeUsuario);
        tvUserEmail.setText(emailUsuario);

        // Carrega preferência de voz
        boolean isVozFeminina = sessionManager.isVozFeminina();
        if (isVozFeminina) {
            rbVoiceFem.setChecked(true);
        } else {
            rbVoiceMas.setChecked(true);
        }

        // Listener para salvar preferência
        rgVoicePreference.setOnCheckedChangeListener((group, checkedId) -> {
            boolean vozFeminina = (checkedId == R.id.rb_voice_fem);
            sessionManager.setVozFeminina(vozFeminina);
        });
    }

    private void configurarBotoes() {
        configurarBotaoProtegido(btnVoltar, () -> finish());

        configurarBotaoProtegido(btnLogout, () -> {
            new AlertDialog.Builder(PerfilActivity.this)
                    .setTitle("Sair da Conta")
                    .setMessage("Deseja realmente sair da sua conta?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

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
                            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
                            return true;
                        } else {
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        if (isThisButtonActive) {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80)
                                    .withEndAction(() -> {
                                        acao.run();
                                        isAnyButtonProcessing = false;
                                        isThisButtonActive = false;
                                    }).start();
                        } else {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
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
