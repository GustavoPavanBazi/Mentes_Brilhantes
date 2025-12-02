package com.example.mentesbrilhantes;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Locale;
import java.util.Random;

public class JogosPegueGameplayActivity extends AppCompatActivity {

    private ConstraintLayout layoutPrincipal;
    private FrameLayout areaJogo;
    private ImageView imgBarata;
    private ImageButton btnVoltar, btnReiniciar;
    private TextView tvTimer, tvVida;
    private String dificuldade;

    // Configurações do jogo
    private int vidas;
    private int vidasIniciais;
    private long intervaloMovimento;
    private int tamanhoEggman;

    // Timer
    private int segundos = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private boolean timerAtivo = false;

    // Movimentação
    private Handler movimentoHandler = new Handler();
    private Runnable movimentoRunnable;
    private boolean jogoAtivo = false;
    private Random random = new Random();

    // Dimensões da área de jogo
    private int areaWidth;
    private int areaHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_pegue_gameplay);

        dificuldade = getIntent().getStringExtra("DIFICULDADE");

        layoutPrincipal = findViewById(R.id.layout_principal);
        areaJogo = findViewById(R.id.area_jogo);
        imgBarata = findViewById(R.id.img_barata);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnReiniciar = findViewById(R.id.btn_reiniciar);
        tvTimer = findViewById(R.id.tv_timer);
        tvVida = findViewById(R.id.tv_vida);

        definirBackgroundPorNivel();
        definirBotoesPorNivel();
        configurarDificuldade();
        configurarBarata();
        configurarBotoes();

        // Aguarda o layout estar pronto
        areaJogo.post(new Runnable() {
            @Override
            public void run() {
                areaWidth = areaJogo.getWidth();
                areaHeight = areaJogo.getHeight();
                iniciarJogo();
            }
        });
    }

    private void configurarDificuldade() {
        switch (dificuldade) {
            case "FACIL":
                vidas = 3;
                intervaloMovimento = 1500;
                tamanhoEggman = 340; // GRANDE
                break;
            case "MEDIO":
                vidas = 5;
                intervaloMovimento = 800;
                tamanhoEggman = 280; // MÉDIO
                break;
            case "DIFICIL":
                vidas = 10;
                intervaloMovimento = 400;
                tamanhoEggman = 200; // PEQUENO
                break;
            default:
                vidas = 3;
                intervaloMovimento = 1500;
                tamanhoEggman = 340;
                break;
        }
        vidasIniciais = vidas;
        atualizarVida();

        // Aplica o tamanho do Eggman
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imgBarata.getLayoutParams();
        params.width = tamanhoEggman;
        params.height = tamanhoEggman;
        imgBarata.setLayoutParams(params);
    }

    private void definirBackgroundPorNivel() {
        int backgroundResource;
        switch (dificuldade) {
            case "FACIL":
                backgroundResource = R.drawable.bg_jogos_gameplay_facil;
                break;
            case "MEDIO":
                backgroundResource = R.drawable.bg_jogos_gameplay_medio;
                break;
            case "DIFICIL":
                backgroundResource = R.drawable.bg_jogos_gameplay_dificil;
                break;
            default:
                backgroundResource = R.drawable.bg_jogos_gameplay_dificil;
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

    private void configurarBarata() {
        imgBarata.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && jogoAtivo) {
                    barataClicada();
                    return true;
                }
                return false;
            }
        });
    }

    private void barataClicada() {
        vidas--;
        atualizarVida();

        // Animação de clique
        imgBarata.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        imgBarata.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                    }
                })
                .start();

        if (vidas <= 0) {
            jogoCompleto();
        } else {
            // Move imediatamente para nova posição
            moverBarata();
        }
    }

    private void iniciarJogo() {
        jogoAtivo = true;
        segundos = 0;
        atualizarTimer();
        iniciarTimer();
        iniciarMovimentoBarata();

        // Restaura a imagem normal do Eggman
        imgBarata.setImageResource(R.drawable.pegue_eggman);
        imgBarata.setVisibility(View.VISIBLE);

        // Posiciona Eggman em posição inicial aleatória
        moverBarata();
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

    private void atualizarVida() {
        tvVida.setText(String.valueOf(vidas));
    }

    private void iniciarMovimentoBarata() {
        movimentoRunnable = new Runnable() {
            @Override
            public void run() {
                if (jogoAtivo) {
                    moverBarata();
                    movimentoHandler.postDelayed(this, intervaloMovimento);
                }
            }
        };
        movimentoHandler.postDelayed(movimentoRunnable, intervaloMovimento);
    }

    private void pararMovimentoBarata() {
        jogoAtivo = false;
        movimentoHandler.removeCallbacks(movimentoRunnable);
    }

    private void moverBarata() {
        // Calcula limites dentro da área de jogo com margem de segurança
        int margemSeguranca = 20;
        int maxX = Math.max(margemSeguranca, areaWidth - tamanhoEggman - margemSeguranca);
        int maxY = Math.max(margemSeguranca, areaHeight - tamanhoEggman - margemSeguranca);

        int novoX = margemSeguranca + random.nextInt(Math.max(1, maxX - margemSeguranca));
        int novoY = margemSeguranca + random.nextInt(Math.max(1, maxY - margemSeguranca));

        // Anima o movimento
        imgBarata.animate()
                .x(novoX)
                .y(novoY)
                .setDuration(200)
                .start();
    }

    private void jogoCompleto() {
        pararTimer();
        pararMovimentoBarata();

        // Troca para a imagem de derrota
        imgBarata.setImageResource(R.drawable.pegue_eggman_derrota);

        // Centraliza a imagem na área de jogo (não na tela toda)
        centralizarImagemDerrota();

        // Mostra mensagem de vitória
        String mensagemVitoria = gerarMensagemVitoria();
        Toast.makeText(this, mensagemVitoria, Toast.LENGTH_LONG).show();

        // Pequeno delay antes dos confetes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Mostra confetes de vitória
                new JogosMemoriaConfetes(JogosPegueGameplayActivity.this,
                        findViewById(android.R.id.content)).iniciar();
            }
        }, 500);
    }

    private void centralizarImagemDerrota() {
        // Calcula o centro da área de jogo
        // Ajusta o tamanho da imagem de derrota para caber bem na tela
        int tamanhoImagemDerrota;

        // Define tamanho da imagem de derrota baseado no espaço disponível
        int espacoDisponivelWidth = areaWidth - 40; // 40px de margem total
        int espacoDisponivelHeight = areaHeight - 40;

        // Usa o menor valor entre largura e altura disponível para garantir que caiba
        int tamanhoMaximo = Math.min(espacoDisponivelWidth, espacoDisponivelHeight);

        // Define tamanho baseado no nível, mas respeitando o máximo
        switch (dificuldade) {
            case "FACIL":
                tamanhoImagemDerrota = Math.min(350, tamanhoMaximo);
                break;
            case "MEDIO":
                tamanhoImagemDerrota = Math.min(280, tamanhoMaximo);
                break;
            case "DIFICIL":
                tamanhoImagemDerrota = Math.min(220, tamanhoMaximo);
                break;
            default:
                tamanhoImagemDerrota = Math.min(280, tamanhoMaximo);
                break;
        }

        // Atualiza o tamanho da imagem
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imgBarata.getLayoutParams();
        params.width = tamanhoImagemDerrota;
        params.height = tamanhoImagemDerrota;
        imgBarata.setLayoutParams(params);

        // Calcula posição central
        int centerX = (areaWidth - tamanhoImagemDerrota) / 2;
        int centerY = (areaHeight - tamanhoImagemDerrota) / 2;

        // Anima para o centro
        imgBarata.animate()
                .x(centerX)
                .y(centerY)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(500)
                .start();
    }

    private String gerarMensagemVitoria() {
        int minutos = segundos / 60;
        int segs = segundos % 60;
        String tempo = String.format(Locale.getDefault(), "%02d:%02d", minutos, segs);

        switch (dificuldade) {
            case "FACIL":
                return "🎉 VITÓRIA! Você derrotou o Eggman em " + tempo + "! Nível Fácil concluído!";
            case "MEDIO":
                return "🏆 INCRÍVEL! Você derrotou o Eggman em " + tempo + "! Nível Médio dominado!";
            case "DIFICIL":
                return "⭐ FANTÁSTICO! Você derrotou o Eggman em " + tempo + "! Você é um mestre!";
            default:
                return "🎉 PARABÉNS! Você venceu em " + tempo + "!";
        }
    }

    private void reiniciarJogo() {
        pararTimer();
        pararMovimentoBarata();

        segundos = 0;
        configurarDificuldade();
        atualizarTimer();
        atualizarVida();

        imgBarata.setScaleX(1.0f);
        imgBarata.setScaleY(1.0f);

        // Aguarda um momento antes de reiniciar
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciarJogo();
            }
        }, 300);
    }

    private void configurarBotoes() {
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pararTimer();
                pararMovimentoBarata();
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
        pararMovimentoBarata();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararTimer();
        pararMovimentoBarata();
        timerHandler.removeCallbacksAndMessages(null);
        movimentoHandler.removeCallbacksAndMessages(null);
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
