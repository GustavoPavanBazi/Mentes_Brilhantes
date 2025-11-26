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

public class FalasLazerActivity extends AppCompatActivity {

    private ImageButton btnAudio1, btnAudio2, btnAudio3, btnAudio4;
    private ImageButton btnSair, btnAvancar, btnVoltar;
    private int paginaAtual = 1;
    private final int TOTAL_PAGINAS = 2;
    private static boolean isAnyButtonProcessing = false;
    private MediaPlayer mediaPlayerAtual = null;
    private String sexoUsuario;
    private String voicePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_falas_lazer);

        // Buscar dados do usuário
        SessionManager sessionManager = new SessionManager(this);
        sexoUsuario = sessionManager.getUserGender();
        voicePreference = sessionManager.getVoicePreference();

        inicializarBotoes();
        configurarListeners();
        mostrarPagina(paginaAtual);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreen();
        isAnyButtonProcessing = false;

        // Atualizar preferência de voz ao retornar para a tela
        SessionManager sessionManager = new SessionManager(this);
        voicePreference = sessionManager.getVoicePreference();
        mostrarPagina(paginaAtual);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayerAtual != null) {
            mediaPlayerAtual.release();
            mediaPlayerAtual = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configurarFullscreen();
        }
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

    // Configura botoes de audio com delay fixo de 1 segundo entre cliques
    private void configurarBotaoAudioComDelay(View botao, Runnable acao) {
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
                                            // Delay fixo de 1 segundo antes de liberar outros botoes
                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d("FalasLazerActivity", "Liberando botões após 1 segundo fixo");
                                                    isAnyButtonProcessing = false;
                                                }
                                            }, 1000);
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

    // Configura botoes de navegacao sem delay
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
        configurarBotaoAudioComDelay(btnAudio1, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioLazer(1, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio2, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioLazer(2, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio3, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioLazer(3, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio4, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioLazer(4, paginaAtual);
            }
        });

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

    // Atualiza imagens dos botoes conforme a pagina E SEXO
    private void mostrarPagina(int pagina) {
        boolean isFeminino = sexoUsuario != null && sexoUsuario.equalsIgnoreCase("feminino");

        switch (pagina) {
            case 1:
                if (isFeminino) {
                    btnAudio1.setImageResource(R.drawable.btn_falas_fem_lazer_assistir);
                    btnAudio2.setImageResource(R.drawable.btn_falas_fem_lazer_brincar);
                    btnAudio3.setImageResource(R.drawable.btn_falas_fem_lazer_dancar);
                    btnAudio4.setImageResource(R.drawable.btn_falas_fem_lazer_desenhar);
                } else {
                    btnAudio1.setImageResource(R.drawable.btn_falas_mas_lazer_assistir);
                    btnAudio2.setImageResource(R.drawable.btn_falas_mas_lazer_brincar);
                    btnAudio3.setImageResource(R.drawable.btn_falas_mas_lazer_dancar);
                    btnAudio4.setImageResource(R.drawable.btn_falas_mas_lazer_desenhar);
                }
                btnAvancar.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.GONE);
                break;

            case 2:
                if (isFeminino) {
                    btnAudio1.setImageResource(R.drawable.btn_falas_fem_lazer_jogar);
                    btnAudio2.setImageResource(R.drawable.btn_falas_fem_lazer_ler);
                    btnAudio3.setImageResource(R.drawable.btn_falas_fem_lazer_musica);
                    btnAudio4.setImageResource(R.drawable.btn_falas_fem_lazer_passear);
                } else {
                    btnAudio1.setImageResource(R.drawable.btn_falas_mas_lazer_jogar);
                    btnAudio2.setImageResource(R.drawable.btn_falas_mas_lazer_ler);
                    btnAudio3.setImageResource(R.drawable.btn_falas_mas_lazer_musica);
                    btnAudio4.setImageResource(R.drawable.btn_falas_mas_lazer_passear);
                }
                btnAvancar.setVisibility(View.GONE);
                btnVoltar.setVisibility(View.VISIBLE);
                break;
        }
    }

    // Seleciona o audio correto baseado no botao, pagina E PREFERÊNCIA DE VOZ
    private void reproduzirAudioLazer(int botao, int pagina) {
        int audioResource = 0;
        boolean usarVozFeminina = voicePreference != null && voicePreference.equalsIgnoreCase("feminino");

        if (pagina == 1) {
            switch (botao) {
                case 1:
                    audioResource = usarVozFeminina ? R.raw.lazer_assistir_fem : R.raw.lazer_assistir_mas;
                    break;
                case 2:
                    audioResource = usarVozFeminina ? R.raw.lazer_brincar_fem : R.raw.lazer_brincar_mas;
                    break;
                case 3:
                    audioResource = usarVozFeminina ? R.raw.lazer_dancar_fem : R.raw.lazer_dancar_mas;
                    break;
                case 4:
                    audioResource = usarVozFeminina ? R.raw.lazer_desenhar_fem : R.raw.lazer_desenhar_mas;
                    break;
            }
        } else {
            switch (botao) {
                case 1:
                    audioResource = usarVozFeminina ? R.raw.lazer_jogar_fem : R.raw.lazer_jogar_mas;
                    break;
                case 2:
                    audioResource = usarVozFeminina ? R.raw.lazer_ler_fem : R.raw.lazer_ler_mas;
                    break;
                case 3:
                    audioResource = usarVozFeminina ? R.raw.lazer_musica_fem : R.raw.lazer_musica_mas;
                    break;
                case 4:
                    audioResource = usarVozFeminina ? R.raw.lazer_passear_fem : R.raw.lazer_passear_mas;
                    break;
            }
        }

        if (audioResource != 0) {
            reproduzirAudioImediato(audioResource);
        }
    }

    // Para audio anterior e toca novo imediatamente
    private void reproduzirAudioImediato(int audioResource) {
        try {
            if (mediaPlayerAtual != null) {
                if (mediaPlayerAtual.isPlaying()) {
                    mediaPlayerAtual.stop();
                }
                mediaPlayerAtual.release();
                mediaPlayerAtual = null;
                Log.d("FalasLazerActivity", "Áudio anterior parado");
            }

            mediaPlayerAtual = MediaPlayer.create(this, audioResource);
            if (mediaPlayerAtual != null) {
                mediaPlayerAtual.start();
                Log.d("FalasLazerActivity", "Novo áudio iniciado imediatamente");

                mediaPlayerAtual.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        if (mediaPlayerAtual == mp) {
                            mediaPlayerAtual = null;
                        }
                        Log.d("FalasLazerActivity", "Áudio terminou e foi liberado");
                    }
                });
            }
        } catch (Exception e) {
            Log.e("FalasLazerActivity", "Erro ao reproduzir áudio: " + e.getMessage());
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
