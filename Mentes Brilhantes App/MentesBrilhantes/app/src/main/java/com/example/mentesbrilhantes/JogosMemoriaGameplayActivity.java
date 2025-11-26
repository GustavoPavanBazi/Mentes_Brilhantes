package com.example.mentesbrilhantes;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class JogosMemoriaGameplayActivity extends AppCompatActivity {

    private ConstraintLayout layoutPrincipal;
    private GridLayout gridLayout;
    private ImageButton btnVoltar, btnReiniciar;
    private TextView tvTentativas, tvTimer;
    private int cols, rows;
    private String categoria;
    private String dificuldade;
    private int tentativas = 0;
    private int paresEncontrados = 0;
    private int totalPares;

    // Timer
    private int segundos = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private boolean timerAtivo = false;

    private JogosMemoriaCartao primeiraCarta = null;
    private JogosMemoriaCartao segundaCarta = null;
    private boolean podeClicar = false; // Começa false por causa do preview

    private List<JogosMemoriaCartao> cartas = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_memoria_gameplay);

        cols = getIntent().getIntExtra("COLS", 2);
        rows = getIntent().getIntExtra("ROWS", 2);
        categoria = getIntent().getStringExtra("CATEGORIA");

        // Determina a dificuldade baseado no tamanho do grid
        if (cols == 2 && rows == 2) {
            dificuldade = "FACIL";
        } else if (cols == 4 && rows == 4) {
            dificuldade = "MEDIO";
        } else {
            dificuldade = "DIFICIL";
        }

        totalPares = (cols * rows) / 2;

        layoutPrincipal = findViewById(R.id.layout_principal);
        gridLayout = findViewById(R.id.grid_memoria);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnReiniciar = findViewById(R.id.btn_reiniciar);
        tvTentativas = findViewById(R.id.tv_tentativas);
        tvTimer = findViewById(R.id.tv_timer);

        // Define o background e botões de acordo com o nível
        definirBackgroundPorNivel();
        definirBotoesPorNivel();

        configurarGrid();
        criarCartas();
        configurarBotoes();
        atualizarTentativas();

        // Inicia o preview das cartas
        iniciarPreviewCartas();
    }

    private void iniciarPreviewCartas() {
        // Determina o tempo de preview baseado na dificuldade
        int tempoPreview;
        switch (dificuldade) {
            case "FACIL":
                tempoPreview = 1000; // 1 segundo
                break;
            case "MEDIO":
                tempoPreview = 2000; // 2 segundos
                break;
            case "DIFICIL":
                tempoPreview = 3000; // 3 segundos
                break;
            default:
                tempoPreview = 3000;
                break;
        }

        // Mostra todas as cartas viradas
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarTodasCartas();
            }
        }, 300); // Pequeno delay para garantir que tudo foi carregado

        // Depois do tempo de preview, vira todas as cartas de volta
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                esconderTodasCartas();
                // Libera o jogo e inicia o timer
                podeClicar = true;
                iniciarTimer();
            }
        }, 300 + tempoPreview);
    }

    private void mostrarTodasCartas() {
        for (JogosMemoriaCartao carta : cartas) {
            carta.mostrarPreview();
        }
    }

    private void esconderTodasCartas() {
        for (JogosMemoriaCartao carta : cartas) {
            carta.esconderPreview();
        }
    }

    private void definirBackgroundPorNivel() {
        int backgroundResource;

        switch (dificuldade) {
            case "FACIL":
                backgroundResource = R.drawable.bg_jogos_memoria_gameplay_facil;
                break;
            case "MEDIO":
                backgroundResource = R.drawable.bg_jogos_memoria_gameplay_medio;
                break;
            case "DIFICIL":
                backgroundResource = R.drawable.bg_jogos_memoria_gameplay_dificil;
                break;
            default:
                backgroundResource = R.drawable.bg_jogos_memoria_gameplay_dificil;
                break;
        }

        layoutPrincipal.setBackgroundResource(backgroundResource);
    }

    private void definirBotoesPorNivel() {
        switch (dificuldade) {
            case "FACIL":
                btnVoltar.setImageResource(R.drawable.btn_jogos_sair_verde);
                btnReiniciar.setImageResource(R.drawable.btn_jogar_novamente_verde);
                break;
            case "MEDIO":
                btnVoltar.setImageResource(R.drawable.btn_jogos_sair_amarelo);
                btnReiniciar.setImageResource(R.drawable.btn_jogar_novamente_amarelo);
                break;
            case "DIFICIL":
                btnVoltar.setImageResource(R.drawable.btn_jogos_sair_vermelho);
                btnReiniciar.setImageResource(R.drawable.btn_jogar_novamente_vermelho);
                break;
            default:
                btnVoltar.setImageResource(R.drawable.btn_jogos_sair_vermelho);
                btnReiniciar.setImageResource(R.drawable.btn_jogar_novamente_vermelho);
                break;
        }
    }

    private void configurarGrid() {
        gridLayout.setColumnCount(cols);
        gridLayout.setRowCount(rows);
    }

    private void iniciarTimer() {
        timerAtivo = true;
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timerAtivo) {
                    segundos++;
                    atualizarTimer();
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void pararTimer() {
        timerAtivo = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void atualizarTimer() {
        int minutos = segundos / 60;
        int segs = segundos % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutos, segs));
    }

    private void criarCartas() {
        List<Integer> imagensIds = obterImagensCategoria();

        // RANDOMIZA a lista de imagens disponíveis
        Collections.shuffle(imagensIds);

        List<Integer> paresIds = new ArrayList<>();

        // Cria pares de imagens (pega apenas as primeiras necessárias da lista embaralhada)
        for (int i = 0; i < totalPares; i++) {
            int imagemId = imagensIds.get(i);
            paresIds.add(imagemId);
            paresIds.add(imagemId);
        }

        // Embaralha as cartas (posições dos pares)
        Collections.shuffle(paresIds);

        // Obtém a imagem do verso baseado na dificuldade
        int versoId = obterVerso();

        // Cria as views das cartas
        for (int i = 0; i < cols * rows; i++) {
            JogosMemoriaCartao carta = new JogosMemoriaCartao(this, paresIds.get(i), versoId, i);
            carta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCartaClicada(carta);
                }
            });
            cartas.add(carta);
            gridLayout.addView(carta);
        }
    }

    private int obterVerso() {
        switch (dificuldade) {
            case "FACIL":
                return R.drawable.memoria_verso_facil;
            case "MEDIO":
                return R.drawable.memoria_verso_medio;
            case "DIFICIL":
                return R.drawable.memoria_verso_dificil;
            default:
                return R.drawable.memoria_verso_dificil;
        }
    }

    private List<Integer> obterImagensCategoria() {
        List<Integer> imagens = new ArrayList<>();

        switch (categoria) {
            case "ANIMAIS":
                imagens.add(R.drawable.memoria_animal_1);
                imagens.add(R.drawable.memoria_animal_2);
                imagens.add(R.drawable.memoria_animal_3);
                imagens.add(R.drawable.memoria_animal_4);
                imagens.add(R.drawable.memoria_animal_5);
                imagens.add(R.drawable.memoria_animal_6);
                imagens.add(R.drawable.memoria_animal_7);
                imagens.add(R.drawable.memoria_animal_8);
                imagens.add(R.drawable.memoria_animal_9);
                imagens.add(R.drawable.memoria_animal_10);
                imagens.add(R.drawable.memoria_animal_11);
                imagens.add(R.drawable.memoria_animal_12);
                break;
            case "LUGARES":
                imagens.add(R.drawable.memoria_lugar_1);
                imagens.add(R.drawable.memoria_lugar_2);
                imagens.add(R.drawable.memoria_lugar_3);
                imagens.add(R.drawable.memoria_lugar_4);
                imagens.add(R.drawable.memoria_lugar_5);
                imagens.add(R.drawable.memoria_lugar_6);
                imagens.add(R.drawable.memoria_lugar_7);
                imagens.add(R.drawable.memoria_lugar_8);
                imagens.add(R.drawable.memoria_lugar_9);
                imagens.add(R.drawable.memoria_lugar_10);
                imagens.add(R.drawable.memoria_lugar_11);
                imagens.add(R.drawable.memoria_lugar_12);
                break;
            case "COMIDAS":
                imagens.add(R.drawable.memoria_comida_1);
                imagens.add(R.drawable.memoria_comida_2);
                imagens.add(R.drawable.memoria_comida_3);
                imagens.add(R.drawable.memoria_comida_4);
                imagens.add(R.drawable.memoria_comida_5);
                imagens.add(R.drawable.memoria_comida_6);
                imagens.add(R.drawable.memoria_comida_7);
                imagens.add(R.drawable.memoria_comida_8);
                imagens.add(R.drawable.memoria_comida_9);
                imagens.add(R.drawable.memoria_comida_10);
                imagens.add(R.drawable.memoria_comida_11);
                imagens.add(R.drawable.memoria_comida_12);
                break;
        }

        return imagens;
    }

    private void onCartaClicada(JogosMemoriaCartao carta) {
        if (!podeClicar || carta.isVirada() || carta.isEncontrada()) {
            return;
        }

        carta.virar();

        if (primeiraCarta == null) {
            primeiraCarta = carta;
        } else if (segundaCarta == null) {
            segundaCarta = carta;
            podeClicar = false;
            verificarPar();
        }
    }

    private void verificarPar() {
        tentativas++;
        atualizarTentativas();

        if (primeiraCarta.getImagemId() == segundaCarta.getImagemId()) {
            // Par encontrado!
            primeiraCarta.marcarComoEncontrada();
            segundaCarta.marcarComoEncontrada();
            paresEncontrados++;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    primeiraCarta = null;
                    segundaCarta = null;
                    podeClicar = true;

                    if (paresEncontrados == totalPares) {
                        jogoCompleto();
                    }
                }
            }, 500);
        } else {
            // Par errado, vira as cartas de volta
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Verifica se as cartas ainda existem antes de desvirar
                    if (primeiraCarta != null && primeiraCarta.getParent() != null) {
                        primeiraCarta.desvirar();
                    }
                    if (segundaCarta != null && segundaCarta.getParent() != null) {
                        segundaCarta.desvirar();
                    }
                    primeiraCarta = null;
                    segundaCarta = null;
                    podeClicar = true;
                }
            }, 1000);
        }
    }

    private void atualizarTentativas() {
        tvTentativas.setText(String.valueOf(tentativas));
    }

    private void jogoCompleto() {
        pararTimer();
        new JogosMemoriaConfetes(this, findViewById(android.R.id.content)).iniciar();
    }

    private void reiniciarJogo() {
        // Remove TODOS os callbacks pendentes do handler
        handler.removeCallbacksAndMessages(null);

        pararTimer();
        segundos = 0;
        tentativas = 0;
        paresEncontrados = 0;
        primeiraCarta = null;
        segundaCarta = null;
        podeClicar = false; // Bloqueia até o preview terminar
        cartas.clear();
        gridLayout.removeAllViews();
        criarCartas();
        atualizarTentativas();
        atualizarTimer();

        // Inicia o preview novamente
        iniciarPreviewCartas();
    }

    private void configurarBotoes() {
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove callbacks pendentes antes de sair
                handler.removeCallbacksAndMessages(null);
                pararTimer();
                finish();
            }
        });

        btnReiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reiniciarJogo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pararTimer();
        // Remove callbacks quando sai da tela
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararTimer();
        // Remove callbacks ao destruir a activity
        handler.removeCallbacksAndMessages(null);
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
