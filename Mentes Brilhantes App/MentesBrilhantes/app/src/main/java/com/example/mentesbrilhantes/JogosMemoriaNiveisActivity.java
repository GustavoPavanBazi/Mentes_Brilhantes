package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class JogosMemoriaNiveisActivity extends AppCompatActivity {

    private ImageButton btnFacil, btnMedio, btnDificil, btnVoltar;
    private String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_memoria_niveis);

        categoria = getIntent().getStringExtra("CATEGORIA");

        btnFacil = findViewById(R.id.btn_memoria_nivel_facil);
        btnMedio = findViewById(R.id.btn_memoria_nivel_medio);
        btnDificil = findViewById(R.id.btn_memoria_nivel_dificil);
        btnVoltar = findViewById(R.id.btn_voltar);

        configurarBotoes();
    }

    private void configurarBotoes() {
        configurarBotaoComAnimacao(btnFacil, new Runnable() {
            @Override
            public void run() {
                iniciarJogoComDificuldade(2, 2); // 2x2 = 4 cartas
            }
        });

        configurarBotaoComAnimacao(btnMedio, new Runnable() {
            @Override
            public void run() {
                iniciarJogoComDificuldade(4, 4); // 4x4 = 16 cartas
            }
        });

        configurarBotaoComAnimacao(btnDificil, new Runnable() {
            @Override
            public void run() {
                iniciarJogoComDificuldade(4, 6); // 4x6 = 24 cartas
            }
        });

        configurarBotaoComAnimacao(btnVoltar, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    // Inicia o jogo passando colunas, linhas e categoria
    private void iniciarJogoComDificuldade(int cols, int rows) {
        Intent intent = new Intent(JogosMemoriaNiveisActivity.this, JogosMemoriaGameplayActivity.class);
        intent.putExtra("COLS", cols);
        intent.putExtra("ROWS", rows);
        intent.putExtra("CATEGORIA", categoria);
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
