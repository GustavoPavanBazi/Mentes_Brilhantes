package com.example.mentesbrilhantes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private VideoView videoView;
    private static final int SPLASH_DURATION = 3000;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove a action bar para tela cheia
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);
        videoView = findViewById(R.id.splash_video);

        // Configurar o vídeo
        setupVideo();

        // Timer para transição automática
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                navigateToNextScreen();
            }
        }, SPLASH_DURATION);
    }

    private void navigateToNextScreen() {
        Intent intent;

        // Verifica se o usuário está logado
        if (sessionManager.isLoggedIn()) {
            // Usuário logado -> vai para MainActivity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else if (sessionManager.isFirstTime()) {
            // Primeira vez abrindo o app -> vai para RegisterActivity
            intent = new Intent(SplashActivity.this, RegisterActivity.class);
        } else {
            // Não é primeira vez mas não está logado -> vai para LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupVideo() {
        try {
            // Caminho para o vídeo na pasta raw
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.logo);
            videoView.setVideoURI(videoUri);

            // Configurar listener para quando o vídeo estiver preparado
            videoView.setOnPreparedListener(mediaPlayer -> {
                // Configurar para ocupar tela cheia
                mediaPlayer.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

                // Obter dimensões da tela e do vídeo
                adjustVideoSize(mediaPlayer);

                // Iniciar o vídeo
                videoView.start();
            });

            // Listener para quando o vídeo terminar (opcional)
            videoView.setOnCompletionListener(mediaPlayer -> {
                // Você pode adicionar lógica aqui se quiser que a transição
                // aconteça quando o vídeo terminar ao invés de usar timer
            });

            // Listener para erros
            videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
                // Em caso de erro, ir direto para a próxima tela
                navigateToNextScreen();
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            // Em caso de erro, ir direto para a próxima tela
            navigateToNextScreen();
        }
    }

    private void adjustVideoSize(android.media.MediaPlayer mediaPlayer) {
        try {
            // Obter dimensões da tela
            android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            // Obter dimensões do vídeo
            int videoWidth = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();

            // Calcular proporção
            float screenRatio = (float) screenWidth / screenHeight;
            float videoRatio = (float) videoWidth / videoHeight;

            // Ajustar VideoView para ocupar tela cheia
            android.widget.FrameLayout.LayoutParams layoutParams =
                    (android.widget.FrameLayout.LayoutParams) videoView.getLayoutParams();

            if (screenRatio > videoRatio) {
                // Tela mais larga que o vídeo - ajustar pela largura
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth / videoRatio);
            } else {
                // Tela mais alta que o vídeo - ajustar pela altura
                layoutParams.width = (int) (screenHeight * videoRatio);
                layoutParams.height = screenHeight;
            }

            layoutParams.gravity = android.view.Gravity.CENTER;
            videoView.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
            // Se falhar, usar configuração padrão
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pausar o vídeo se a atividade for pausada
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos do vídeo
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
