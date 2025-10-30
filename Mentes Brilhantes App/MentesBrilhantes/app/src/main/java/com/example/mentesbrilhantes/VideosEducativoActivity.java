package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class VideosEducativoActivity extends AppCompatActivity {

    // Declaração de todos os botões de vídeo
    private ImageButton btnVideo1, btnVideo2, btnVideo3, btnVideo4, btnVideo5, btnVideo6, btnVideo7, btnVideo8,
            btnVideo9, btnVideo10, btnVideo11, btnVideo12, btnVideo13, btnVideo14, btnVideo15, btnSair;

    // Declaração de todos os FrameLayouts
    private FrameLayout frameVideo1, frameVideo2, frameVideo3, frameVideo4, frameVideo5, frameVideo6, frameVideo7,
            frameVideo8, frameVideo9, frameVideo10, frameVideo11, frameVideo12, frameVideo13, frameVideo14, frameVideo15;

    private static boolean isAnyButtonProcessing = false;

    // IDs dos vídeos educativos do YouTube
    private final String VIDEO_ID_1 = "H33UFg94PEI";
    private final String VIDEO_ID_2 = "hb9REKMp54Y";
    private final String VIDEO_ID_3 = "_RG-XDQg64U";
    private final String VIDEO_ID_4 = "_NSkoWouWME";
    private final String VIDEO_ID_5 = "4p32dIO_wCE";
    private final String VIDEO_ID_6 = "x3ZFTkfUWz4";
    private final String VIDEO_ID_7 = "V3PBSOu9j44";
    private final String VIDEO_ID_8 = "0kjyR9Q2rwE";
    private final String VIDEO_ID_9 = "MYY_JgfT5uo";
    private final String VIDEO_ID_10 = "oHHd7yPOg34";
    private final String VIDEO_ID_11 = "WxydNj9QYn4";
    private final String VIDEO_ID_12 = "hHUW-cIclck";
    private final String VIDEO_ID_13 = "xc_NBvviLDM";
    private final String VIDEO_ID_14 = "DZCrP1Icq7c";
    private final String VIDEO_ID_15 = "AuRZ3oQ4rEM";

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
        btnVideo5 = findViewById(R.id.btn_video5);
        btnVideo6 = findViewById(R.id.btn_video6);
        btnVideo7 = findViewById(R.id.btn_video7);
        btnVideo8 = findViewById(R.id.btn_video8);
        btnVideo9 = findViewById(R.id.btn_video9);
        btnVideo10 = findViewById(R.id.btn_video10);
        btnVideo11 = findViewById(R.id.btn_video11);
        btnVideo12 = findViewById(R.id.btn_video12);
        btnVideo13 = findViewById(R.id.btn_video13);
        btnVideo14 = findViewById(R.id.btn_video14);
        btnVideo15 = findViewById(R.id.btn_video15);
        btnSair = findViewById(R.id.btn_sair);

        // FrameLayouts (para animar borda + botão juntos)
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
        frameVideo11 = findViewById(R.id.frame_video11);
        frameVideo12 = findViewById(R.id.frame_video12);
        frameVideo13 = findViewById(R.id.frame_video13);
        frameVideo14 = findViewById(R.id.frame_video14);
        frameVideo15 = findViewById(R.id.frame_video15);
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
        // TÍTULOS CORRIGIDOS PARA FICAREM IGUAIS AO XML
        configurarBotaoProtegido(btnVideo1, frameVideo1, () -> abrirVideo(VIDEO_ID_1, "Aprendendo sobre o autismo com o André! | Turma da Mônica"));
        configurarBotaoProtegido(btnVideo2, frameVideo2, () -> abrirVideo(VIDEO_ID_2, "TEA - Autismo explicado para crianças - Uma História sobre Autismo 🧩"));
        configurarBotaoProtegido(btnVideo3, frameVideo3, () -> abrirVideo(VIDEO_ID_3, "GUGUDADA - A Música das Cores (animação infantil)"));
        configurarBotaoProtegido(btnVideo4, frameVideo4, () -> abrirVideo(VIDEO_ID_4, "GUGUDADA - As Partes do Corpo (animação infantil)"));
        configurarBotaoProtegido(btnVideo5, frameVideo5, () -> abrirVideo(VIDEO_ID_5, "GUGUDADA - A Música do Alfabeto (animação infantil)"));
        configurarBotaoProtegido(btnVideo6, frameVideo6, () -> abrirVideo(VIDEO_ID_6, "GUGUDADA - A Música das Frutas (animação infantil)"));
        configurarBotaoProtegido(btnVideo7, frameVideo7, () -> abrirVideo(VIDEO_ID_7, "GUGUDADA - O Trem dos Animais (animação infantil)"));
        configurarBotaoProtegido(btnVideo8, frameVideo8, () -> abrirVideo(VIDEO_ID_8, "GUGUDADA - As Formas Geométricas (animação infantil)"));
        configurarBotaoProtegido(btnVideo9, frameVideo9, () -> abrirVideo(VIDEO_ID_9, "GUGUDADA - Grande e Pequeno (animação infantil)"));
        configurarBotaoProtegido(btnVideo10, frameVideo10, () -> abrirVideo(VIDEO_ID_10, "GUGUDADA - A Música das Vogais (animação infantil)"));
        configurarBotaoProtegido(btnVideo11, frameVideo11, () -> abrirVideo(VIDEO_ID_11, "ABC"));
        configurarBotaoProtegido(btnVideo12, frameVideo12, () -> abrirVideo(VIDEO_ID_12, "Os animais cantam | Video Musical Infantil | Toobys"));
        configurarBotaoProtegido(btnVideo13, frameVideo13, () -> abrirVideo(VIDEO_ID_13, "Cançao das letras | Video Musical Infantil | Toobys"));
        configurarBotaoProtegido(btnVideo14, frameVideo14, () -> abrirVideo(VIDEO_ID_14, "Cançao dos números | Video Musical Infantil | Toobys"));
        configurarBotaoProtegido(btnVideo15, frameVideo15, () -> abrirVideo(VIDEO_ID_15, "Canção das cores | Video Musical Infantil | Toobys"));

        // BOTÃO SAIR (sem FrameLayout, usa método antigo)
        configurarBotaoProtegidoSimples(btnSair, this::finish);
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
        Intent intent = new Intent(VideosEducativoActivity.this, VideoPlayerActivity.class);
        intent.putExtra("VIDEO_ID", videoId);
        intent.putExtra("TITULO", titulo);
        startActivity(intent);
    }
}