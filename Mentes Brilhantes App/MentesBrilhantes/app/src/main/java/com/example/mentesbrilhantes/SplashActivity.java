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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.splash_video);
        setupVideo();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToMainActivity();
            }
        }, SPLASH_DURATION);
    }

    // Configura e inicia o video da splash screen
    private void setupVideo() {
        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.logo);
            videoView.setVideoURI(videoUri);

            videoView.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                adjustVideoSize(mediaPlayer);
                videoView.start();
            });

            videoView.setOnCompletionListener(mediaPlayer -> {
                // Pode adicionar logica aqui se quiser transicao ao fim do video
            });

            videoView.setOnErrorListener((mediaPlayer, what, extra) -> {
                goToMainActivity();
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            goToMainActivity();
        }
    }

    // Ajusta tamanho do video pra preencher tela toda
    private void adjustVideoSize(android.media.MediaPlayer mediaPlayer) {
        try {
            android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            int videoWidth = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();

            float screenRatio = (float) screenWidth / screenHeight;
            float videoRatio = (float) videoWidth / videoHeight;

            android.widget.FrameLayout.LayoutParams layoutParams =
                    (android.widget.FrameLayout.LayoutParams) videoView.getLayoutParams();

            if (screenRatio > videoRatio) {
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth / videoRatio);
            } else {
                layoutParams.width = (int) (screenHeight * videoRatio);
                layoutParams.height = screenHeight;
            }

            layoutParams.gravity = android.view.Gravity.CENTER;
            videoView.setLayoutParams(layoutParams);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}