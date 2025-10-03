package com.example.mentesbrilhantes;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SocialActivity extends AppCompatActivity {

    private ImageButton btnAudio1, btnAudio2, btnAudio3, btnAudio4;
    private ImageButton btnSair, btnAvancar, btnVoltar;
    private int paginaAtual = 1;
    private final int TOTAL_PAGINAS = 2;

    // FLAG GLOBAL COMPARTILHADA - IGUAL À FALAS
    private static boolean isAnyButtonProcessing = false;

    // MEDIAPLAYER ATUAL PARA PARAR SE NECESSÁRIO
    private MediaPlayer mediaPlayerAtual = null;

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

        setContentView(R.layout.activity_social);

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
    protected void onDestroy() {
        super.onDestroy();
        // Para e libera MediaPlayer se existir
        if (mediaPlayerAtual != null) {
            mediaPlayerAtual.release();
            mediaPlayerAtual = null;
        }
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
        btnAudio1 = findViewById(R.id.btn_audio1);
        btnAudio2 = findViewById(R.id.btn_audio2);
        btnAudio3 = findViewById(R.id.btn_audio3);
        btnAudio4 = findViewById(R.id.btn_audio4);

        btnSair = findViewById(R.id.btn_sair);
        btnAvancar = findViewById(R.id.btn_avancar);
        btnVoltar = findViewById(R.id.btn_voltar);
    }

    // MÉTODO PARA BOTÕES DE ÁUDIO - COM DELAY FIXO DE 1 SEGUNDO
    private void configurarBotaoAudioComDelay(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // SÓ PERMITE SE NENHUM BOTÃO ESTÁ PROCESSANDO
                        if (!isAnyButtonProcessing) {
                            isAnyButtonProcessing = true;
                            isThisButtonActive = true;

                            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
                            return true;
                        } else {
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        if (isThisButtonActive) {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // EXECUTA ÁUDIO IMEDIATAMENTE
                                            acao.run();

                                            // DELAY FIXO DE 1 SEGUNDO (INDEPENDENTE DO ÁUDIO)
                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d("SocialActivity", "Liberando botões após 1 segundo fixo");
                                                    isAnyButtonProcessing = false;
                                                }
                                            }, 1000); // EXATAMENTE 1 SEGUNDO

                                            isThisButtonActive = false;
                                        }
                                    }).start();
                        } else {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
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

    // MÉTODO PARA BOTÕES DE NAVEGAÇÃO - SEM DELAY (IGUAL FALAS)
    private void configurarBotaoNavegacao(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isAnyButtonProcessing) {
                            isAnyButtonProcessing = true;
                            isThisButtonActive = true;

                            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
                            return true;
                        } else {
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        if (isThisButtonActive) {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            acao.run();
                                            // NAVEGAÇÃO: LIBERA IMEDIATAMENTE
                                            isAnyButtonProcessing = false;
                                            isThisButtonActive = false;
                                        }
                                    }).start();
                        } else {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
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
        // 4 BOTÕES DE ÁUDIO - COM DELAY FIXO DE 1 SEGUNDO
        configurarBotaoAudioComDelay(btnAudio1, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioSocial(1, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio2, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioSocial(2, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio3, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioSocial(3, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio4, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioSocial(4, paginaAtual);
            }
        });

        // 3 BOTÕES DE NAVEGAÇÃO - SEM DELAY
        configurarBotaoNavegacao(btnSair, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

        configurarBotaoNavegacao(btnAvancar, new Runnable() {
            @Override
            public void run() {
                if (paginaAtual < TOTAL_PAGINAS) {
                    paginaAtual++;
                    mostrarPagina(paginaAtual);
                }
            }
        });

        configurarBotaoNavegacao(btnVoltar, new Runnable() {
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
                btnAudio1.setImageResource(R.drawable.btn_social_oi);
                btnAudio2.setImageResource(R.drawable.btn_social_tchau);
                btnAudio3.setImageResource(R.drawable.btn_social_obrigado);
                btnAudio4.setImageResource(R.drawable.btn_social_porfavor);

                btnAvancar.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.GONE);
                break;

            case 2:
                btnAudio1.setImageResource(R.drawable.btn_social_desculpa);
                btnAudio2.setImageResource(R.drawable.btn_social_tudobem);
                btnAudio3.setImageResource(R.drawable.btn_social_sim);
                btnAudio4.setImageResource(R.drawable.btn_social_nao);

                btnAvancar.setVisibility(View.GONE);
                btnVoltar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void reproduzirAudioSocial(int botao, int pagina) {
        int audioResource = 0;

        if (pagina == 1) {
            switch (botao) {
                case 1: audioResource = R.raw.social_oi; break;
                case 2: audioResource = R.raw.social_tchau; break;
                case 3: audioResource = R.raw.social_obrigado; break;
                case 4: audioResource = R.raw.social_porfavor; break;
            }
        } else {
            switch (botao) {
                case 1: audioResource = R.raw.social_desculpa; break;
                case 2: audioResource = R.raw.social_tudobem; break;
                case 3: audioResource = R.raw.social_sim; break;
                case 4: audioResource = R.raw.social_nao; break;
            }
        }

        if (audioResource != 0) {
            reproduzirAudioImediato(audioResource);
        }
    }

    // REPRODUÇÃO IMEDIATA - PARA ÁUDIO ANTERIOR SE ESTIVER TOCANDO
    private void reproduzirAudioImediato(int audioResource) {
        try {
            // PARA ÁUDIO ANTERIOR SE ESTIVER TOCANDO
            if (mediaPlayerAtual != null) {
                if (mediaPlayerAtual.isPlaying()) {
                    mediaPlayerAtual.stop();
                }
                mediaPlayerAtual.release();
                mediaPlayerAtual = null;
                Log.d("SocialActivity", "Áudio anterior parado");
            }

            // TOCA NOVO ÁUDIO IMEDIATAMENTE
            mediaPlayerAtual = MediaPlayer.create(this, audioResource);

            if (mediaPlayerAtual != null) {
                mediaPlayerAtual.start();
                Log.d("SocialActivity", "Novo áudio iniciado imediatamente");

                // Libera MediaPlayer quando terminar (mas não afeta o delay de 1 segundo)
                mediaPlayerAtual.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        if (mediaPlayerAtual == mp) {
                            mediaPlayerAtual = null;
                        }
                        Log.d("SocialActivity", "Áudio terminou e foi liberado");
                    }
                });
            }

        } catch (Exception e) {
            Log.e("SocialActivity", "Erro ao reproduzir áudio: " + e.getMessage());
        }
    }
}
