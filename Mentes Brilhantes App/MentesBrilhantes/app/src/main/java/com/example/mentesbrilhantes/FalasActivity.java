package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class FalasActivity extends AppCompatActivity {

    private ImageButton btnSocial, btnLazer, btnComidas, btnBanheiro;
    private ImageButton btnSair, btnAvancar, btnVoltar;
    private int paginaAtual = 1;
    private final int TOTAL_PAGINAS = 2;

    // FLAG GLOBAL COMPARTILHADA
    private static boolean isAnyButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // **CONFIGURAÇÃO FULLSCREEN**
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

        setContentView(R.layout.activity_falas);

        inicializarBotoes();
        configurarListeners();
        mostrarPagina(paginaAtual);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manterFullscreen();
        // Reset da flag quando volta para a tela
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
        btnSocial = findViewById(R.id.btn_social);
        btnLazer = findViewById(R.id.btn_lazer);
        btnComidas = findViewById(R.id.btn_comidas);
        btnBanheiro = findViewById(R.id.btn_banheiro);

        btnSair = findViewById(R.id.btn_sair);
        btnAvancar = findViewById(R.id.btn_avancar);
        btnVoltar = findViewById(R.id.btn_voltar);
    }

    // MÉTODO CORRIGIDO - APENAS O PRIMEIRO BOTÃO EXECUTA AÇÃO
    private void configurarBotaoProtegido(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // SÓ PERMITE SE NENHUM BOTÃO ESTÁ PROCESSANDO
                        if (!isAnyButtonProcessing) {
                            isAnyButtonProcessing = true;
                            isThisButtonActive = true; // Marca ESTE botão como ativo

                            // Animação só no botão ativo
                            v.animate()
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(80)
                                    .start();
                            return true;
                        } else {
                            // Outros botões ignoram completamente
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        // SÓ EXECUTA AÇÃO SE FOR O BOTÃO ATIVO
                        if (isThisButtonActive) {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // EXECUTA AÇÃO APENAS DO BOTÃO ATIVO
                                            acao.run();
                                            // LIBERA FLAGS
                                            isAnyButtonProcessing = false;
                                            isThisButtonActive = false;
                                        }
                                    })
                                    .start();
                        } else {
                            // Outros botões voltam ao normal sem executar ação
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        // Cancela e libera flags
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
        // Todos os botões com proteção total

        configurarBotaoProtegido(btnSocial, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasActivity.this, SocialActivity.class));
                } else {
                    startActivity(new Intent(FalasActivity.this, EmocoesActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnLazer, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasActivity.this, LazerActivity.class));
                } else {
                    startActivity(new Intent(FalasActivity.this, AnimaisActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnComidas, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasActivity.this, ComidasActivity.class));
                } else {
                    startActivity(new Intent(FalasActivity.this, LugaresActivity.class));
                }
            }
        });

        configurarBotaoProtegido(btnBanheiro, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual == 1) {
                    startActivity(new Intent(FalasActivity.this, NecessidadesActivity.class));
                } else {
                    startActivity(new Intent(FalasActivity.this, ComunicacaoActivity.class));
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
}
