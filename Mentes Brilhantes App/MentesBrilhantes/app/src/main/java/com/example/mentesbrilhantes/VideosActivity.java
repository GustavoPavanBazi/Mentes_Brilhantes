package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class VideosActivity extends AppCompatActivity {

    private ImageButton btnEducativo, btnDesenho, btnRelaxante, btnRotina, btnSair;

    // FLAG GLOBAL COMPARTILHADA - IGUAL À FALAS
    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // **CONFIGURAÇÃO FULLSCREEN - IGUAL À FALAS**
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        setContentView(R.layout.activity_videos);

        inicializarBotoes();
        configurarListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manterFullscreen();
        // Reset da flag quando volta para a tela
        isAnyButtonProcessing = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            manterFullscreen();
        }
    }

    private void manterFullscreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void inicializarBotoes() {
        btnEducativo = findViewById(R.id.btn_educativo);
        btnDesenho = findViewById(R.id.btn_desenho);
        btnRelaxante = findViewById(R.id.btn_relaxante);
        btnRotina = findViewById(R.id.btn_rotina);
        btnSair = findViewById(R.id.btn_sair);
    }

    // MÉTODO PROTEGIDO - IGUAL À FALAS ACTIVITY
    private void configurarBotaoProtegido(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // SÓ PERMITE SE NENHUM BOTÃO ESTÁ PROCESSANDO
                        if (!isAnyButtonProcessing) {
                            isAnyButtonProcessing = true;
                            isThisButtonActive = true; // Marca ESTE botão como ativo

                            // Animação só no botão ativo
                            v.animate()
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(80)
                                    .start();
                            return true;
                        } else {
                            // Outros botões ignoram completamente
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        // SÓ EXECUTA AÇÃO SE FOR O BOTÃO ATIVO
                        if (isThisButtonActive) {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // EXECUTA AÇÃO APENAS DO BOTÃO ATIVO
                                            acao.run();
                                            // LIBERA FLAGS
                                            isAnyButtonProcessing = false;
                                            isThisButtonActive = false;
                                        }
                                    })
                                    .start();
                        } else {
                            // Outros botões voltam ao normal sem executar ação
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        // Cancela e libera flags
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

    private void configurarListeners() {
        // Todos os botões com proteção total - IGUAL À FALAS

        configurarBotaoProtegido(btnEducativo, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(VideosActivity.this, VideosEducativoActivity.class));
            }
        });

        configurarBotaoProtegido(btnDesenho, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(VideosActivity.this, VideosDesenhosActivity.class));
            }
        });

        configurarBotaoProtegido(btnRelaxante, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(VideosActivity.this, VideosRelaxarActivity.class));
            }
        });

        configurarBotaoProtegido(btnRotina, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(VideosActivity.this, VideosRotinaActivity.class));
            }
        });

        configurarBotaoProtegido(btnSair, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }
}
