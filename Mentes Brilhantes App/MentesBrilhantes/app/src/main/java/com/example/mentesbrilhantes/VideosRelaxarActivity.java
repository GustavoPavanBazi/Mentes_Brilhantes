package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class VideosRelaxarActivity extends AppCompatActivity {

    // Declaração dos 10 botões de música
    private ImageButton btnVideo1, btnVideo2, btnVideo3, btnVideo4, btnVideo5,
            btnVideo6, btnVideo7, btnVideo8, btnVideo9, btnVideo10, btnSair;

    // Declaração dos 10 FrameLayouts
    private FrameLayout frameVideo1, frameVideo2, frameVideo3, frameVideo4, frameVideo5,
            frameVideo6, frameVideo7, frameVideo8, frameVideo9, frameVideo10;

    private static boolean isAnyButtonProcessing = false;

    // IDs dos 10 vídeos musicais do YouTube
    private final String VIDEO_ID_1 = "";
    private final String VIDEO_ID_2 = "";
    private final String VIDEO_ID_3 = "";
    private final String VIDEO_ID_4 = "";
    private final String VIDEO_ID_5 = "";
    private final String VIDEO_ID_6 = "";
    private final String VIDEO_ID_7 = "";
    private final String VIDEO_ID_8 = "";
    private final String VIDEO_ID_9 = "";
    private final String VIDEO_ID_10 = "";

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

        setContentView(R.layout.activity_videos_musicas);

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
        btnVideo5 = findViewById(R.id.btn_video5);
        btnVideo6 = findViewById(R.id.btn_video6);
        btnVideo7 = findViewById(R.id.btn_video7);
        btnVideo8 = findViewById(R.id.btn_video8);
        btnVideo9 = findViewById(R.id.btn_video9);
        btnVideo10 = findViewById(R.id.btn_video10);
        btnSair = findViewById(R.id.btn_sair);

        // FrameLayouts
        frameVideo1 = findViewById(R.id.frame_video1);
        frameVideo2 = findViewById(R.id.frame_video2);
        frameVideo3 = findViewById(R.id.frame_video3);
        frameVideo4 = findViewById(R.id.frame_video4);
        frameVideo5 = findViewById(R.id.frame_video5);
        frameVideo6 = findViewById(R.id.frame_video6);
        frameVideo7 = findViewById(R.id.frame_video7);
        frameVideo8 = findViewById(R.id.frame_video8);
        frameVideo9 = findViewById(R.id.frame_video9);
        frameVideo10 = findViewById(R.id.frame_video10);
    }

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
        // Configurar os 10 vídeos musicais
        configurarBotaoProtegido(btnVideo1, frameVideo1, () -> abrirVideo(VIDEO_ID_1, ""));
        configurarBotaoProtegido(btnVideo2, frameVideo2, () -> abrirVideo(VIDEO_ID_2, ""));
        configurarBotaoProtegido(btnVideo3, frameVideo3, () -> abrirVideo(VIDEO_ID_3, ""));
        configurarBotaoProtegido(btnVideo4, frameVideo4, () -> abrirVideo(VIDEO_ID_4, ""));
        configurarBotaoProtegido(btnVideo5, frameVideo5, () -> abrirVideo(VIDEO_ID_5, ""));
        configurarBotaoProtegido(btnVideo6, frameVideo6, () -> abrirVideo(VIDEO_ID_6, ""));
        configurarBotaoProtegido(btnVideo7, frameVideo7, () -> abrirVideo(VIDEO_ID_7, ""));
        configurarBotaoProtegido(btnVideo8, frameVideo8, () -> abrirVideo(VIDEO_ID_8, ""));
        configurarBotaoProtegido(btnVideo9, frameVideo9, () -> abrirVideo(VIDEO_ID_9, ""));
        configurarBotaoProtegido(btnVideo10, frameVideo10, () -> abrirVideo(VIDEO_ID_10, ""));

        // BOTÃO SAIR
        configurarBotaoProtegidoSimples(btnSair, this::finish);
    }

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
                                    .withEndAction(() -> {
                                        acao.run();
                                        isAnyButtonProcessing = false;
                                        isThisButtonActive = false;
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
        Intent intent = new Intent(VideosRelaxarActivity.this, VideoPlayerActivity.class);
        intent.putExtra("VIDEO_ID", videoId);
        intent.putExtra("TITULO", titulo);
        startActivity(intent);
    }
}
