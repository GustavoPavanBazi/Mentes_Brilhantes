package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class JogosInicioActivity extends AppCompatActivity {

    private ImageButton btnQuebraCabeca, btnJogoDaVelha, btnComidas, btnBanheiro;
    private ImageButton btnSair, btnAvancar, btnVoltar;
    private int paginaAtual = 1;
    private final int TOTAL_PAGINAS = 2;

    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_inicio);

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
        btnQuebraCabeca = findViewById(R.id.btn_jogos_quebra_cabeca);
        btnJogoDaVelha = findViewById(R.id.btn_jogos_jogo_da_velha);
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
        configurarBotaoProtegido(btnQuebraCabeca, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(JogosInicioActivity.this, JogosQuebracabecaNiveisActivity.class));
                } else {
                    startActivity(new Intent(JogosInicioActivity.this, FalasEmocoesActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnJogoDaVelha, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(JogosInicioActivity.this, JogosJogodavelhaEscolhaActivity.class));
                } else {
                    startActivity(new Intent(JogosInicioActivity.this, FalasAnimaisActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnComidas, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(JogosInicioActivity.this, FalasComidasActivity.class));
                } else {
                    startActivity(new Intent(JogosInicioActivity.this, FalasLugaresActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnBanheiro, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(JogosInicioActivity.this, FalasNecessidadesActivity.class));
                } else {
                    startActivity(new Intent(JogosInicioActivity.this, FalasComunicacaoActivity.class));
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
                btnQuebraCabeca.setImageResource(R.drawable.btn_jogos_quebra_cabeca);
                btnJogoDaVelha.setImageResource(R.drawable.btn_jogos_jogo_da_velha);
                btnComidas.setImageResource(R.drawable.btn_comidas);
                btnBanheiro.setImageResource(R.drawable.btn_necessidades);
                btnAvancar.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.GONE);
                break;

            case 2:
                btnQuebraCabeca.setImageResource(R.drawable.btn_emocoes);
                btnJogoDaVelha.setImageResource(R.drawable.btn_animais);
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