package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnFalas, btnJogos, btnVideos, btnPerfil;

    // FLAG GLOBAL COMPARTILHADA - apenas uma para TODA a activity
    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // **CONFIGURAÇÃO FULLSCREEN**
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

        setContentView(R.layout.activity_main);

        inicializarBotoes();
        configurarBotoesProtegidos();
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
        btnFalas = findViewById(R.id.btn_falas);
        btnJogos = findViewById(R.id.btn_jogos);
        btnVideos = findViewById(R.id.btn_videos);
        btnPerfil = findViewById(R.id.btn_perfil);
    }

    // MÉTODO CORRIGIDO - APENAS O PRIMEIRO BOTÃO EXECUTA AÇÃO
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

    private void configurarBotoesProtegidos() {
        // Todos os botões com proteção total

        configurarBotaoProtegido(btnFalas, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, FalasActivity.class));
            }
        });

        configurarBotaoProtegido(btnJogos, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, JogosActivity.class));
            }
        });

        configurarBotaoProtegido(btnVideos, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, VideosActivity.class));
            }
        });

        configurarBotaoProtegido(btnPerfil, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
            }
        });
    }
}
