package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnFalas, btnJogos, btnVideos, btnPerfil;
    private static boolean isAnyButtonProcessing = false;
    private SessionManager sessionManager;
    private long pressedTime;

    // Variável para controlar se já mostrou a saudação
    private static boolean saudacaoMostrada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // Mostra saudação apenas na primeira vez
        if (!saudacaoMostrada) {
            mostrarSaudacao();
            saudacaoMostrada = true;
        }

        inicializarBotoes();
        configurarBotoesProtegidos();
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

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(this, "Pressione novamente para sair", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    private void mostrarSaudacao() {
        String nomeUsuario = sessionManager.getUserName();
        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        String saudacao;

        if (horaAtual >= 0 && horaAtual < 12) {
            saudacao = "Bom dia";
        } else if (horaAtual >= 12 && horaAtual < 18) {
            saudacao = "Boa tarde";
        } else {
            saudacao = "Boa noite";
        }

        String mensagem = saudacao + ", " + nomeUsuario + "!";
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    private void inicializarBotoes() {
        btnFalas = findViewById(R.id.btn_falas);
        btnJogos = findViewById(R.id.btn_jogos);
        btnVideos = findViewById(R.id.btn_videos);
        btnPerfil = findViewById(R.id.btn_perfil);
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

    private void configurarBotoesProtegidos() {
        configurarBotaoProtegido(btnFalas, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, FalasInicioActivity.class));
            }
        });

        configurarBotaoProtegido(btnJogos, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, JogosInicioActivity.class));
            }
        });

        configurarBotaoProtegido(btnVideos, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, VideosActivity.class));
            }
        });

        configurarBotaoProtegido(btnPerfil, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
            }
        });
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
