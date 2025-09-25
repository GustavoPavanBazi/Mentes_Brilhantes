package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class VideosEducativoActivity extends AppCompatActivity {

    private ImageButton btnVideo1, btnVideo2, btnVideo3, btnVideo4, btnSair;
    private FrameLayout frameVideo1, frameVideo2, frameVideo3, frameVideo4; // ← ADICIONAR FRAMES

    private static boolean isAnyButtonProcessing = false;

    // IDs dos vídeos educativos do YouTube
    private final String VIDEO_ID_1 = "dQw4w9WgXcQ";
    private final String VIDEO_ID_2 = "9bZkp7q19f0";
    private final String VIDEO_ID_3 = "jNQXAC9IVRw";
    private final String VIDEO_ID_4 = "oHg5SJYRHA0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_videos_educativo);

        inicializarBotoes();
        configurarListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manterFullscreen();
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
        // ImageButtons
        btnVideo1 = findViewById(R.id.btn_video1);
        btnVideo2 = findViewById(R.id.btn_video2);
        btnVideo3 = findViewById(R.id.btn_video3);
        btnVideo4 = findViewById(R.id.btn_video4);
        btnSair = findViewById(R.id.btn_sair);

        // FrameLayouts (para animar borda + botão juntos)
        frameVideo1 = findViewById(R.id.frame_video1);
        frameVideo2 = findViewById(R.id.frame_video2);
        frameVideo3 = findViewById(R.id.frame_video3);
        frameVideo4 = findViewById(R.id.frame_video4);
    }

    // MÉTODO ATUALIZADO - Anima FrameLayout (borda + conteúdo)
    private void configurarBotaoProtegido(View botao, FrameLayout frame, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isAnyButtonProcessing) {
                            isAnyButtonProcessing = true;
                            isThisButtonActive = true;

                            // ANIMA O FRAMELAYOUT (borda + botão juntos)
                            frame.animate()
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
                            frame.animate()
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
                            frame.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        frame.animate()
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
        // PASSA FRAMELAYOUT PARA CADA BOTÃO
        configurarBotaoProtegido(btnVideo1, frameVideo1, new Runnable() {
            @Override
            public void run() {
                abrirVideo(VIDEO_ID_1, "Alfabeto para Autistas");
            }
        });

        configurarBotaoProtegido(btnVideo2, frameVideo2, new Runnable() {
            @Override
            public void run() {
                abrirVideo(VIDEO_ID_2, "Números e Contagem");
            }
        });

        configurarBotaoProtegido(btnVideo3, frameVideo3, new Runnable() {
            @Override
            public void run() {
                abrirVideo(VIDEO_ID_3, "Cores e Formas");
            }
        });

        configurarBotaoProtegido(btnVideo4, frameVideo4, new Runnable() {
            @Override
            public void run() {
                abrirVideo(VIDEO_ID_4, "Rotina Diária");
            }
        });

        // BOTÃO SAIR (sem FrameLayout, usa método antigo)
        configurarBotaoProtegidoSimples(btnSair, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    // MÉTODO ANTIGO PARA BOTÃO SAIR
    private void configurarBotaoProtegidoSimples(View botao, Runnable acao) {
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

    private void abrirVideo(String videoId, String titulo) {
        Intent intent = new Intent(VideosEducativoActivity.this, VideoPlayerActivity.class);
        intent.putExtra("VIDEO_ID", videoId);
        intent.putExtra("TITULO", titulo);
        startActivity(intent);
    }
}
