package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class JogosConectarNiveisActivity extends AppCompatActivity {

    private ImageButton btnFacil, btnMedio, btnDificil, btnVoltar;
    private String categoria;
    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_conectar_niveis);

        categoria = getIntent().getStringExtra("CATEGORIA");

        btnFacil = findViewById(R.id.btn_conectar_nivel_facil);
        btnMedio = findViewById(R.id.btn_conectar_nivel_medio);
        btnDificil = findViewById(R.id.btn_conectar_nivel_dificil);
        btnVoltar = findViewById(R.id.btn_voltar);

        configurarListeners();
    }

    private void configurarListeners() {
        configurarBotaoProtegido(btnFacil, new Runnable() {
            @Override
            public void run() {
                abrirJogo(2); // MUDOU DE 3 PARA 2
            }
        });

        configurarBotaoProtegido(btnMedio, new Runnable() {
            @Override
            public void run() {
                abrirJogo(4); // MUDOU DE 6 PARA 4
            }
        });

        configurarBotaoProtegido(btnDificil, new Runnable() {
            @Override
            public void run() {
                abrirJogo(6); // MUDOU DE 8 PARA 6
            }
        });

        configurarBotaoProtegido(btnVoltar, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    private void abrirJogo(int quantidade) {
        Intent intent = new Intent(JogosConectarNiveisActivity.this, JogosConectarGameplayActivity.class);
        intent.putExtra("CATEGORIA", categoria);
        intent.putExtra("QUANTIDADE", quantidade);
        startActivity(intent);
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
