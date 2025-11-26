package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class JogosJogodavelhaNiveisActivity extends AppCompatActivity {

    private ImageButton btnFacil, btnMedio, btnDificil, btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_jogodavelha_niveis);

        btnFacil = findViewById(R.id.btn_bot_facil);
        btnMedio = findViewById(R.id.btn_bot_medio);
        btnDificil = findViewById(R.id.btn_bot_dificil);
        btnVoltar = findViewById(R.id.btn_voltar);

        configurarBotoes();
    }

    private void configurarBotoes() {
        configurarBotaoComAnimacao(btnFacil, new Runnable() {
            @Override
            public void run() {
                iniciarJogoComBot("EASY");
            }
        });

        configurarBotaoComAnimacao(btnMedio, new Runnable() {
            @Override
            public void run() {
                iniciarJogoComBot("MEDIUM");
            }
        });

        configurarBotaoComAnimacao(btnDificil, new Runnable() {
            @Override
            public void run() {
                iniciarJogoComBot("HARD");
            }
        });

        configurarBotaoComAnimacao(btnVoltar, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    // Inicia o jogo passando o modo bot e a dificuldade escolhida
    private void iniciarJogoComBot(String difficulty) {
        Intent intent = new Intent(JogosJogodavelhaNiveisActivity.this, JogosJogodavelhaGameplayActivity.class);
        intent.putExtra("MODE", "BOT");
        intent.putExtra("DIFFICULTY", difficulty);
        startActivity(intent);
    }

    // Adiciona animacao de escala no toque do botao
    private void configurarBotaoComAnimacao(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate()
                                .scaleX(0.95f)
                                .scaleY(0.95f)
                                .setDuration(100)
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        acao.run();
                                    }
                                })
                                .start();
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
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
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configurarFullscreen();
        }
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