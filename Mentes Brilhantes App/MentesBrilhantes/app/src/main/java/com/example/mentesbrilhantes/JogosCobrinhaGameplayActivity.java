package com.example.mentesbrilhantes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;

public class JogosCobrinhaGameplayActivity extends AppCompatActivity {

    private static final String TAG = "CobrinhaGameplay";

    private ConstraintLayout layoutPrincipal;
    private FrameLayout cobrinhaContainer;
    private CobrinhaView cobrinhaView;
    private ImageButton btnVoltar, btnReiniciar;
    private ImageButton btnCima, btnBaixo, btnEsquerda, btnDireita;
    private TextView tvPontos, tvTimer;
    private String dificuldade;

    private int numeroCelulasX;
    private int numeroCelulasY;
    private long velocidade;
    private int pontos = 0;

    private int segundos = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private boolean timerAtivo = false;

    private Handler gameHandler = new Handler();
    private Runnable gameRunnable;
    private boolean jogoAtivo = false;
    private Random random = new Random();

    private List<Posicao> cobra;
    private Direcao direcaoAtual;
    private Posicao comida;

    // Fila de movimentos para memorizar comandos
    private Queue<Direcao> filaDirecoes = new ArrayDeque<>();
    private static final int MAX_FILA_DIRECOES = 3;

    // Cor de fundo do tabuleiro por nível
    private int corFundoTabuleiro;

    // Para detectar swipe contínuo
    private float ultimoX, ultimoY;
    private static final int SWIPE_THRESHOLD = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            configurarFullscreen();
            setContentView(R.layout.activity_jogos_cobrinha_gameplay);

            dificuldade = getIntent().getStringExtra("DIFICULDADE");
            if (dificuldade == null) {
                dificuldade = "FACIL";
            }

            Log.d(TAG, "Iniciando com dificuldade: " + dificuldade);

            inicializarViews();
            definirBackgroundPorNivel();
            definirBotoesPorNivel();
            configurarDificuldade();
            configurarCorFundoTabuleiro();
            configurarBotoes();
            configurarControles();

            cobrinhaContainer.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        cobrinhaView = new CobrinhaView(JogosCobrinhaGameplayActivity.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                        );
                        cobrinhaView.setLayoutParams(params);
                        cobrinhaContainer.addView(cobrinhaView);

                        // Configura touch contínuo no tabuleiro
                        configurarTouchTabuleiro();

                        iniciarJogo();
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao criar view do jogo", e);
                        Toast.makeText(JogosCobrinhaGameplayActivity.this,
                                "Erro ao iniciar jogo", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Erro no onCreate", e);
            Toast.makeText(this, "Erro ao carregar jogo: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void configurarTouchTabuleiro() {
        cobrinhaView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ultimoX = event.getX();
                        ultimoY = event.getY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - ultimoX;
                        float deltaY = event.getY() - ultimoY;

                        // Detecta movimento horizontal
                        if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                            if (deltaX > 0) {
                                adicionarDirecaoNaFila(Direcao.DIREITA);
                            } else {
                                adicionarDirecaoNaFila(Direcao.ESQUERDA);
                            }
                            ultimoX = event.getX();
                            ultimoY = event.getY();
                        }
                        // Detecta movimento vertical
                        else if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                            if (deltaY > 0) {
                                adicionarDirecaoNaFila(Direcao.BAIXO);
                            } else {
                                adicionarDirecaoNaFila(Direcao.CIMA);
                            }
                            ultimoX = event.getX();
                            ultimoY = event.getY();
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        return true;
                }
                return false;
            }
        });
    }

    private void adicionarDirecaoNaFila(Direcao novaDirecao) {
        // Pega a última direção (da fila ou atual)
        Direcao ultimaDirecao = filaDirecoes.isEmpty() ? direcaoAtual : ((ArrayDeque<Direcao>) filaDirecoes).peekLast();

        // Não permite direção oposta
        if (saoOpostas(ultimaDirecao, novaDirecao)) {
            return;
        }

        // Não adiciona se for igual à última
        if (ultimaDirecao == novaDirecao) {
            return;
        }

        // Limita o tamanho da fila
        if (filaDirecoes.size() < MAX_FILA_DIRECOES) {
            filaDirecoes.add(novaDirecao);
            Log.d(TAG, "Direção adicionada: " + novaDirecao + ", Fila: " + filaDirecoes.size());
        }
    }

    private boolean saoOpostas(Direcao d1, Direcao d2) {
        if (d1 == null || d2 == null) return false;
        return (d1 == Direcao.CIMA && d2 == Direcao.BAIXO) ||
                (d1 == Direcao.BAIXO && d2 == Direcao.CIMA) ||
                (d1 == Direcao.ESQUERDA && d2 == Direcao.DIREITA) ||
                (d1 == Direcao.DIREITA && d2 == Direcao.ESQUERDA);
    }

    private void inicializarViews() {
        layoutPrincipal = findViewById(R.id.layout_principal);
        cobrinhaContainer = findViewById(R.id.cobrinha_view);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnReiniciar = findViewById(R.id.btn_reiniciar);
        btnCima = findViewById(R.id.btn_cima);
        btnBaixo = findViewById(R.id.btn_baixo);
        btnEsquerda = findViewById(R.id.btn_esquerda);
        btnDireita = findViewById(R.id.btn_direita);
        tvPontos = findViewById(R.id.tv_pontos);
        tvTimer = findViewById(R.id.tv_timer);

        if (cobrinhaContainer == null) {
            throw new RuntimeException("cobrinha_view não encontrado no layout");
        }
    }

    private void configurarDificuldade() {
        switch (dificuldade) {
            case "FACIL":
                numeroCelulasX = 7;
                numeroCelulasY = 7;
                velocidade = 500;
                break;
            case "MEDIO":
                numeroCelulasX = 7;
                numeroCelulasY = 10;
                velocidade = 350;
                break;
            case "DIFICIL":
                numeroCelulasX = 10;
                numeroCelulasY = 15;
                velocidade = 200;
                break;
            default:
                numeroCelulasX = 7;
                numeroCelulasY = 7;
                velocidade = 500;
                break;
        }

        Log.d(TAG, "Grade: " + numeroCelulasX + "x" + numeroCelulasY + ", Velocidade: " + velocidade);
    }

    private void configurarCorFundoTabuleiro() {
        switch (dificuldade) {
            case "FACIL":
                corFundoTabuleiro = Color.parseColor("#76ff71");
                break;
            case "MEDIO":
                corFundoTabuleiro = Color.parseColor("#ffe962");
                break;
            case "DIFICIL":
                corFundoTabuleiro = Color.parseColor("#f97171");
                break;
            default:
                corFundoTabuleiro = Color.parseColor("#76ff71");
                break;
        }
    }

    private void definirBackgroundPorNivel() {
        if (layoutPrincipal == null) return;

        try {
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
                    layoutPrincipal.setBackgroundColor(Color.parseColor("#4CAF50"));
                    return;
            }
            layoutPrincipal.setBackgroundResource(backgroundResource);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao definir background, usando cor padrão", e);
            layoutPrincipal.setBackgroundColor(Color.parseColor("#4CAF50"));
        }
    }

    private void definirBotoesPorNivel() {
        try {
            int corBotaoSair, corBotaoReiniciar, corBotaoDirecao;

            switch (dificuldade) {
                case "FACIL":
                    corBotaoSair = R.drawable.btn_jogos_sair_verde;
                    corBotaoReiniciar = R.drawable.btn_jogar_novamente_verde;
                    corBotaoDirecao = R.drawable.btn_direcao_verde;
                    break;
                case "MEDIO":
                    corBotaoSair = R.drawable.btn_jogos_sair_amarelo;
                    corBotaoReiniciar = R.drawable.btn_jogar_novamente_amarelo;
                    corBotaoDirecao = R.drawable.btn_direcao_amarelo;
                    break;
                case "DIFICIL":
                    corBotaoSair = R.drawable.btn_jogos_sair_vermelho;
                    corBotaoReiniciar = R.drawable.btn_jogar_novamente_vermelho;
                    corBotaoDirecao = R.drawable.btn_direcao_vermelho;
                    break;
                default:
                    return;
            }

            if (btnVoltar != null) btnVoltar.setImageResource(corBotaoSair);
            if (btnReiniciar != null) btnReiniciar.setImageResource(corBotaoReiniciar);
            if (btnCima != null) btnCima.setBackgroundResource(corBotaoDirecao);
            if (btnBaixo != null) btnBaixo.setBackgroundResource(corBotaoDirecao);
            if (btnEsquerda != null) btnEsquerda.setBackgroundResource(corBotaoDirecao);
            if (btnDireita != null) btnDireita.setBackgroundResource(corBotaoDirecao);

            // Aplica rotação das setas
            if (btnCima != null) btnCima.setRotation(0);
            if (btnBaixo != null) btnBaixo.setRotation(180);
            if (btnEsquerda != null) btnEsquerda.setRotation(270);
            if (btnDireita != null) btnDireita.setRotation(90);

        } catch (Exception e) {
            Log.e(TAG, "Erro ao definir imagens dos botões", e);
        }
    }

    private void iniciarJogo() {
        jogoAtivo = true;
        pontos = 0;
        segundos = 0;
        filaDirecoes.clear();
        atualizarPontos();
        atualizarTimer();
        iniciarTimer();

        cobra = new ArrayList<>();
        int centroX = numeroCelulasX / 2;
        int centroY = numeroCelulasY / 2;
        cobra.add(new Posicao(centroX, centroY));
        cobra.add(new Posicao(centroX, centroY + 1));
        cobra.add(new Posicao(centroX, centroY + 2));

        direcaoAtual = Direcao.CIMA;

        gerarComida();

        if (cobrinhaView != null) {
            cobrinhaView.setCorFundo(corFundoTabuleiro);
            cobrinhaView.desenhar(cobra, comida);
        }

        iniciarLoop();
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
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void atualizarTimer() {
        if (tvTimer != null) {
            int minutos = segundos / 60;
            int segs = segundos % 60;
            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutos, segs));
        }
    }

    private void gerarComida() {
        int tentativas = 0;
        do {
            int x = random.nextInt(numeroCelulasX);
            int y = random.nextInt(numeroCelulasY);
            comida = new Posicao(x, y);
            tentativas++;
        } while (cobra.contains(comida) && tentativas < 100);
    }

    private void iniciarLoop() {
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                if (jogoAtivo) {
                    atualizarJogo();
                    gameHandler.postDelayed(this, velocidade);
                }
            }
        };
        gameHandler.postDelayed(gameRunnable, velocidade);
    }

    private void atualizarJogo() {
        // Pega próxima direção da fila, se houver
        if (!filaDirecoes.isEmpty()) {
            Direcao proximaDirecao = filaDirecoes.poll();
            if (!saoOpostas(direcaoAtual, proximaDirecao)) {
                direcaoAtual = proximaDirecao;
            }
        }

        moverCobra();
        verificarColisoes();
        if (cobrinhaView != null) {
            cobrinhaView.desenhar(cobra, comida);
        }
    }

    private void moverCobra() {
        Posicao cabeca = cobra.get(0);
        Posicao novaCabeca = null;

        switch (direcaoAtual) {
            case CIMA:
                novaCabeca = new Posicao(cabeca.x, cabeca.y - 1);
                break;
            case BAIXO:
                novaCabeca = new Posicao(cabeca.x, cabeca.y + 1);
                break;
            case ESQUERDA:
                novaCabeca = new Posicao(cabeca.x - 1, cabeca.y);
                break;
            case DIREITA:
                novaCabeca = new Posicao(cabeca.x + 1, cabeca.y);
                break;
        }

        cobra.add(0, novaCabeca);

        if (novaCabeca.equals(comida)) {
            pontos++;
            atualizarPontos();
            gerarComida();
        } else {
            cobra.remove(cobra.size() - 1);
        }
    }

    private void verificarColisoes() {
        Posicao cabeca = cobra.get(0);

        if (cabeca.x < 0 || cabeca.x >= numeroCelulasX ||
                cabeca.y < 0 || cabeca.y >= numeroCelulasY) {
            gameOver();
            return;
        }

        for (int i = 1; i < cobra.size(); i++) {
            if (cabeca.equals(cobra.get(i))) {
                gameOver();
                return;
            }
        }
    }

    private void gameOver() {
        pararJogo();
        pararTimer();

        String mensagem = gerarMensagemGameOver();
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        new JogosMemoriaConfetes(JogosCobrinhaGameplayActivity.this,
                                findViewById(android.R.id.content)).iniciar();
                    } catch (Exception e) {
                        Log.w(TAG, "Classe JogosMemoriaConfetes não encontrada");
                    }
                }
            }, 500);
        } catch (Exception e) {
            Log.w(TAG, "Erro ao tentar mostrar confetes", e);
        }
    }

    private String gerarMensagemGameOver() {
        int minutos = segundos / 60;
        int segs = segundos % 60;
        String tempo = String.format(Locale.getDefault(), "%02d:%02d", minutos, segs);

        switch (dificuldade) {
            case "FACIL":
                return "🎉 Você fez " + pontos + " pontos em " + tempo + "!";
            case "MEDIO":
                return "🏆 Incrível! " + pontos + " pontos em " + tempo + "!";
            case "DIFICIL":
                return "⭐ Fantástico! " + pontos + " pontos em " + tempo + "!";
            default:
                return "🎉 Você fez " + pontos + " pontos!";
        }
    }

    private void pararJogo() {
        jogoAtivo = false;
        if (gameHandler != null && gameRunnable != null) {
            gameHandler.removeCallbacks(gameRunnable);
        }
    }

    private void atualizarPontos() {
        if (tvPontos != null) {
            tvPontos.setText(String.valueOf(pontos));
        }
    }

    private void configurarControles() {
        configurarBotaoDirecao(btnCima, Direcao.CIMA);
        configurarBotaoDirecao(btnBaixo, Direcao.BAIXO);
        configurarBotaoDirecao(btnEsquerda, Direcao.ESQUERDA);
        configurarBotaoDirecao(btnDireita, Direcao.DIREITA);
    }

    private void configurarBotaoDirecao(ImageButton btn, final Direcao direcao) {
        if (btn == null) return;

        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        adicionarDirecaoNaFila(direcao);
                        v.setAlpha(0.6f);
                        v.setScaleX(0.9f);
                        v.setScaleY(0.9f);
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.setAlpha(1.0f);
                        v.setScaleX(1.0f);
                        v.setScaleY(1.0f);
                        return true;
                }
                return false;
            }
        });
    }

    private void configurarBotoes() {
        if (btnVoltar != null) {
            btnVoltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pararJogo();
                    pararTimer();
                    finish();
                }
            });
        }

        if (btnReiniciar != null) {
            btnReiniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reiniciarJogo();
                }
            });
        }
    }

    private void reiniciarJogo() {
        pararJogo();
        pararTimer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciarJogo();
            }
        }, 300);
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pararJogo();
        pararTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararJogo();
        pararTimer();
        if (gameHandler != null) {
            gameHandler.removeCallbacksAndMessages(null);
        }
        if (timerHandler != null) {
            timerHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configurarFullscreen();
        }
    }

    private void configurarFullscreen() {
        try {
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
        } catch (Exception e) {
            Log.e(TAG, "Erro ao configurar fullscreen", e);
        }
    }

    private enum Direcao {
        CIMA, BAIXO, ESQUERDA, DIREITA
    }

    private static class Posicao {
        int x, y;

        Posicao(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Posicao) {
                Posicao p = (Posicao) obj;
                return x == p.x && y == p.y;
            }
            return false;
        }
    }

    private class CobrinhaView extends View {
        private Paint paintCobra, paintCabeca, paintComida, paintFundo, paintGrade, paintTabuleiro;
        private List<Posicao> cobra;
        private Posicao comida;
        private int corFundo;

        public CobrinhaView(android.content.Context context) {
            super(context);
            inicializarPaint();
        }

        private void inicializarPaint() {
            // Cobra verde
            paintCobra = new Paint();
            paintCobra.setColor(Color.parseColor("#4CAF50"));
            paintCobra.setStyle(Paint.Style.FILL);
            paintCobra.setAntiAlias(true);

            // Cabeça verde claro
            paintCabeca = new Paint();
            paintCabeca.setColor(Color.parseColor("#81C784"));
            paintCabeca.setStyle(Paint.Style.FILL);
            paintCabeca.setAntiAlias(true);

            // Comida vermelha
            paintComida = new Paint();
            paintComida.setColor(Color.parseColor("#FF5252"));
            paintComida.setStyle(Paint.Style.FILL);
            paintComida.setAntiAlias(true);

            // Fundo (cor por nível)
            paintFundo = new Paint();
            paintFundo.setStyle(Paint.Style.FILL);

            // Tabuleiro escuro (quadradinhos)
            paintTabuleiro = new Paint();
            paintTabuleiro.setColor(Color.parseColor("#263238"));
            paintTabuleiro.setStyle(Paint.Style.FILL);

            // Grade cinza escuro
            paintGrade = new Paint();
            paintGrade.setColor(Color.parseColor("#37474F"));
            paintGrade.setStyle(Paint.Style.STROKE);
            paintGrade.setStrokeWidth(2);
        }

        public void setCorFundo(int cor) {
            this.corFundo = cor;
        }

        public void desenhar(List<Posicao> cobra, Posicao comida) {
            if (cobra != null) {
                this.cobra = new ArrayList<>(cobra);
            }
            this.comida = comida;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            try {
                int viewWidth = getWidth();
                int viewHeight = getHeight();

                // Fundo da view com cor do nível (verde/amarelo/vermelho)
                paintFundo.setColor(corFundo);
                canvas.drawRect(0, 0, viewWidth, viewHeight, paintFundo);

                if (cobra == null || comida == null) {
                    return;
                }

                // Calcula tamanho da célula baseado na LARGURA para preencher horizontalmente
                int cellSize = viewWidth / numeroCelulasX;

                // Dimensões exatas do tabuleiro
                int totalWidth = cellSize * numeroCelulasX;
                int totalHeight = cellSize * numeroCelulasY;

                // Se a altura ultrapassar a view, recalcula baseado na altura
                if (totalHeight > viewHeight) {
                    cellSize = viewHeight / numeroCelulasY;
                    totalWidth = cellSize * numeroCelulasX;
                    totalHeight = cellSize * numeroCelulasY;
                }

                // Centraliza o tabuleiro
                int offsetX = (viewWidth - totalWidth) / 2;
                int offsetY = (viewHeight - totalHeight) / 2;

                // Desenha tabuleiro escuro (quadradinhos)
                canvas.drawRect(offsetX, offsetY, offsetX + totalWidth, offsetY + totalHeight, paintTabuleiro);

                // Desenha grade
                for (int i = 0; i <= numeroCelulasX; i++) {
                    float x = offsetX + i * cellSize;
                    canvas.drawLine(x, offsetY, x, offsetY + totalHeight, paintGrade);
                }
                for (int i = 0; i <= numeroCelulasY; i++) {
                    float y = offsetY + i * cellSize;
                    canvas.drawLine(offsetX, y, offsetX + totalWidth, y, paintGrade);
                }

                // Desenha comida (maçã vermelha)
                float comidaX = offsetX + comida.x * cellSize + cellSize / 2f;
                float comidaY = offsetY + comida.y * cellSize + cellSize / 2f;
                float raioComida = cellSize / 2f - 4;
                canvas.drawCircle(comidaX, comidaY, raioComida, paintComida);

                // Desenha cobra
                for (int i = 0; i < cobra.size(); i++) {
                    Posicao seg = cobra.get(i);
                    Paint paint = (i == 0) ? paintCabeca : paintCobra;

                    float left = offsetX + seg.x * cellSize + 3;
                    float top = offsetY + seg.y * cellSize + 3;
                    float right = offsetX + (seg.x + 1) * cellSize - 3;
                    float bottom = offsetY + (seg.y + 1) * cellSize - 3;

                    RectF rect = new RectF(left, top, right, bottom);
                    canvas.drawRoundRect(rect, 8, 8, paint);
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro ao desenhar", e);
            }
        }
    }
}
