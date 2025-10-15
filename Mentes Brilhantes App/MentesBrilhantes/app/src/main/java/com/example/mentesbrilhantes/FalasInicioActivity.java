package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class FalasInicioActivity extends AppCompatActivity {

    private ImageButton btnSocial, btnLazer, btnComidas, btnBanheiro;
    private ImageButton btnSair, btnAvancar, btnVoltar;
    private int paginaAtual = 1;
    private final int TOTAL_PAGINAS = 2;

    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_falas_inicio);

        inicializarBotoes();
        configurarListeners();
        mostrarPagina(paginaAtual);
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

    private void inicializarBotoes() {
        btnSocial = findViewById(R.id.btn_social);
        btnLazer = findViewById(R.id.btn_lazer);
        btnComidas = findViewById(R.id.btn_comidas);
        btnBanheiro = findViewById(R.id.btn_banheiro);
        btnSair = findViewById(R.id.btn_sair);
        btnAvancar = findViewById(R.id.btn_avancar);
        btnVoltar = findViewById(R.id.btn_voltar);
    }

    // Protege botao contra multiplos cliques simultaneos
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

    private void configurarListeners() {
        configurarBotaoProtegido(btnSocial, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasInicioActivity.this, FalasSocialActivity.class));
                } else {
                    startActivity(new Intent(FalasInicioActivity.this, FalasEmocoesActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnLazer, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasInicioActivity.this, FalasLazerActivity.class));
                } else {
                    startActivity(new Intent(FalasInicioActivity.this, FalasAnimaisActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnComidas, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasInicioActivity.this, FalasComidasActivity.class));
                } else {
                    startActivity(new Intent(FalasInicioActivity.this, FalasLugaresActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnBanheiro, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasInicioActivity.this, FalasNecessidadesActivity.class));
                } else {
                    startActivity(new Intent(FalasInicioActivity.this, FalasComunicacaoActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnSair, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

        configurarBotaoProtegido(btnAvancar, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual < TOTAL_PAGINAS) {
                    paginaAtual++;
                    mostrarPagina(paginaAtual);
                }
            }
        });

        configurarBotaoProtegido(btnVoltar, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual > 1) {
                    paginaAtual--;
                    mostrarPagina(paginaAtual);
                }
            }
        });
    }

    // Atualiza imagens dos botoes conforme a pagina
    private void mostrarPagina(int pagina) {
        switch (pagina) {
            case 1:
                btnSocial.setImageResource(R.drawable.btn_social);
                btnLazer.setImageResource(R.drawable.btn_lazer);
                btnComidas.setImageResource(R.drawable.btn_comidas);
                btnBanheiro.setImageResource(R.drawable.btn_necessidades);
                btnAvancar.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.GONE);
                break;

            case 2:
                btnSocial.setImageResource(R.drawable.btn_emocoes);
                btnLazer.setImageResource(R.drawable.btn_animais);
                btnComidas.setImageResource(R.drawable.btn_lugares);
                btnBanheiro.setImageResource(R.drawable.btn_comunicacao);
                btnAvancar.setVisibility(View.GONE);
                btnVoltar.setVisibility(View.VISIBLE);
                break;
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