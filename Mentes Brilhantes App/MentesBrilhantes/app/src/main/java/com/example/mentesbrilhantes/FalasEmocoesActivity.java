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

public class FalasEmocoesActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_falas_emocoes);

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

        // Atualizar ao retornar
        SessionManager sessionManager = new SessionManager(this);
        sexoUsuario = sessionManager.getUserGender();
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
                                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d("FalasEmocoesActivity", "Liberando botões após 1 segundo fixo");
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
                reproduzirAudioEmocoes(1, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio2, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioEmocoes(2, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio3, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioEmocoes(3, paginaAtual);
            }
        });

        configurarBotaoAudioComDelay(btnAudio4, new Runnable() {
            @Override
            public void run() {
                reproduzirAudioEmocoes(4, paginaAtual);
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

    // MODIFICADO - Atualiza imagens conforme SEXO do usuário
    private void mostrarPagina(int pagina) {
        boolean isFeminino = sexoUsuario != null && sexoUsuario.equalsIgnoreCase("feminino");

        switch (pagina) {
            case 1:
                if (isFeminino) {
                    btnAudio1.setImageResource(R.drawable.btn_falas_fem_emocoes_assustada);
                    btnAudio2.setImageResource(R.drawable.btn_falas_fem_emocoes_brava);
                    btnAudio3.setImageResource(R.drawable.btn_falas_fem_emocoes_cansada);
                    btnAudio4.setImageResource(R.drawable.btn_falas_fem_emocoes_doente);
                } else {
                    btnAudio1.setImageResource(R.drawable.btn_falas_mas_emocoes_assustado);
                    btnAudio2.setImageResource(R.drawable.btn_falas_mas_emocoes_bravo);
                    btnAudio3.setImageResource(R.drawable.btn_falas_mas_emocoes_cansado);
                    btnAudio4.setImageResource(R.drawable.btn_falas_mas_emocoes_doente);
                }
                btnAvancar.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.GONE);
                break;

            case 2:
                if (isFeminino) {
                    btnAudio1.setImageResource(R.drawable.btn_falas_fem_emocoes_feliz);
                    btnAudio2.setImageResource(R.drawable.btn_falas_fem_emocoes_nervosa);
                    btnAudio3.setImageResource(R.drawable.btn_falas_fem_emocoes_timida);
                    btnAudio4.setImageResource(R.drawable.btn_falas_fem_emocoes_triste);
                } else {
                    btnAudio1.setImageResource(R.drawable.btn_falas_mas_emocoes_feliz);
                    btnAudio2.setImageResource(R.drawable.btn_falas_mas_emocoes_nervoso);
                    btnAudio3.setImageResource(R.drawable.btn_falas_mas_emocoes_timido);
                    btnAudio4.setImageResource(R.drawable.btn_falas_mas_emocoes_triste);
                }
                btnAvancar.setVisibility(View.GONE);
                btnVoltar.setVisibility(View.VISIBLE);
                break;
        }
    }

    // CORRIGIDO - Áudio baseado na PREFERÊNCIA DE VOZ e SEXO do usuário
    private void reproduzirAudioEmocoes(int botao, int pagina) {
        int audioResource = 0;
        boolean usarVozFeminina = voicePreference != null && voicePreference.equalsIgnoreCase("feminino");
        boolean usuarioFeminino = sexoUsuario != null && sexoUsuario.equalsIgnoreCase("feminino");

        if (pagina == 1) {
            switch (botao) {
                case 1: // Assustado/Assustada
                    if (usuarioFeminino) {
                        audioResource = usarVozFeminina ? R.raw.emocoes_assustada_fem : R.raw.emocoes_assustada_mas;
                    } else {
                        audioResource = usarVozFeminina ? R.raw.emocoes_assustado_fem : R.raw.emocoes_assustado_mas;
                    }
                    break;
                case 2: // Bravo/Brava
                    if (usuarioFeminino) {
                        audioResource = usarVozFeminina ? R.raw.emocoes_brava_fem : R.raw.emocoes_brava_mas;
                    } else {
                        audioResource = usarVozFeminina ? R.raw.emocoes_bravo_fem : R.raw.emocoes_bravo_mas;
                    }
                    break;
                case 3: // Cansado/Cansada
                    if (usuarioFeminino) {
                        audioResource = usarVozFeminina ? R.raw.emocoes_cansada_fem : R.raw.emocoes_cansada_mas;
                    } else {
                        audioResource = usarVozFeminina ? R.raw.emocoes_cansado_fem : R.raw.emocoes_cansado_mas;
                    }
                    break;
                case 4: // Doente (não varia)
                    audioResource = usarVozFeminina ? R.raw.emocoes_doente_fem : R.raw.emocoes_doente_mas;
                    break;
            }
        } else {
            switch (botao) {
                case 1: // Feliz (não varia)
                    audioResource = usarVozFeminina ? R.raw.emocoes_feliz_fem : R.raw.emocoes_feliz_mas;
                    break;
                case 2: // Nervoso/Nervosa
                    if (usuarioFeminino) {
                        audioResource = usarVozFeminina ? R.raw.emocoes_nervosa_fem : R.raw.emocoes_nervosa_mas;
                    } else {
                        audioResource = usarVozFeminina ? R.raw.emocoes_nervoso_fem : R.raw.emocoes_nervoso_mas;
                    }
                    break;
                case 3: // Tímido/Tímida
                    if (usuarioFeminino) {
                        audioResource = usarVozFeminina ? R.raw.emocoes_timida_fem : R.raw.emocoes_timida_mas;
                    } else {
                        audioResource = usarVozFeminina ? R.raw.emocoes_timido_fem : R.raw.emocoes_timido_mas;
                    }
                    break;
                case 4: // Triste (não varia)
                    audioResource = usarVozFeminina ? R.raw.emocoes_triste_fem : R.raw.emocoes_triste_mas;
                    break;
            }
        }

        if (audioResource != 0) {
            reproduzirAudioImediato(audioResource);
        }
    }

    private void reproduzirAudioImediato(int audioResource) {
        try {
            if (mediaPlayerAtual != null) {
                if (mediaPlayerAtual.isPlaying()) {
                    mediaPlayerAtual.stop();
                }
                mediaPlayerAtual.release();
                mediaPlayerAtual = null;
                Log.d("FalasEmocoesActivity", "Áudio anterior parado");
            }

            mediaPlayerAtual = MediaPlayer.create(this, audioResource);
            if (mediaPlayerAtual != null) {
                mediaPlayerAtual.start();
                Log.d("FalasEmocoesActivity", "Novo áudio iniciado imediatamente");

                mediaPlayerAtual.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        if (mediaPlayerAtual == mp) {
                            mediaPlayerAtual = null;
                        }
                        Log.d("FalasEmocoesActivity", "Áudio terminou e foi liberado");
                    }
                });
            }
        } catch (Exception e) {
            Log.e("FalasEmocoesActivity", "Erro ao reproduzir áudio: " + e.getMessage());
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
