package com.example.mentesbrilhantes;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JogosConectarGameplayActivity extends AppCompatActivity {

    private ConstraintLayout layoutPrincipal;
    private GridLayout gridImagens, gridPalavras;
    private ImageButton btnVoltar, btnReiniciar;
    private TextView tvTimer, tvTentativas;
    private String categoria;
    private int quantidade;
    private String dificuldade;

    private Map<String, Integer> palavrasImagens;
    private Map<ImageView, String> imagensMap;
    private Map<ContornoTextView, String> palavrasMap;
    private Map<ContornoTextView, Integer> palavrasPosicoesOriginais;
    private Map<ImageView, ContornoTextView> palavrasCorretasMap;
    private List<String> palavrasSelecionadas;
    private List<ContornoTextView> palavrasViews;

    private float palavraInicialX, palavraInicialY;
    private ViewGroup palavraParentOriginal;

    private int tentativas = 0;
    private int segundos = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private boolean timerAtivo = false;

    private Handler cooldownHandler = new Handler();
    private boolean emCooldown = false;
    private ValueAnimator animacaoCooldown;

    private ContornoTextView palavraAtualmenteArrastando = null;

    private Handler monitorHandler = new Handler();
    private Runnable monitorRunnable;
    private boolean monitorAtivo = false;

    private long ultimoToqueTempo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_conectar_gameplay);

        categoria = getIntent().getStringExtra("CATEGORIA");
        quantidade = getIntent().getIntExtra("QUANTIDADE", 2);

        if (quantidade == 2) {
            dificuldade = "FACIL";
        } else if (quantidade == 4) {
            dificuldade = "MEDIO";
        } else {
            dificuldade = "DIFICIL";
        }

        layoutPrincipal = findViewById(R.id.layout_principal);
        gridImagens = findViewById(R.id.grid_imagens);
        gridPalavras = findViewById(R.id.grid_palavras);
        btnVoltar = findViewById(R.id.btn_voltar);
        btnReiniciar = findViewById(R.id.btn_reiniciar);
        tvTimer = findViewById(R.id.tv_timer);
        tvTentativas = findViewById(R.id.tv_tentativas);

        imagensMap = new HashMap<>();
        palavrasMap = new HashMap<>();
        palavrasCorretasMap = new HashMap<>();
        palavrasPosicoesOriginais = new HashMap<>();
        palavrasViews = new ArrayList<>();

        definirBackgroundPorNivel();
        definirBotoesPorNivel();
        inicializarPalavrasImagens();
        criarJogo();
        configurarBotoes();

        tentativas = 0;
        segundos = 0;
        atualizarTentativas();
        atualizarTimer();
        iniciarTimer();
        iniciarMonitor();
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

    private void inicializarPalavrasImagens() {
        palavrasImagens = new HashMap<>();

        switch (categoria) {
            case "ANIMAIS":
                palavrasImagens.put("ELEFANTE", R.drawable.conectar_animal_1);
                palavrasImagens.put("PEIXE", R.drawable.conectar_animal_2);
                palavrasImagens.put("JACARÉ", R.drawable.conectar_animal_3);
                palavrasImagens.put("VACA", R.drawable.conectar_animal_4);
                palavrasImagens.put("COBRA", R.drawable.conectar_animal_5);
                palavrasImagens.put("CAVALO", R.drawable.conectar_animal_6);
                palavrasImagens.put("HAMSTER", R.drawable.conectar_animal_7);
                palavrasImagens.put("PORCO", R.drawable.conectar_animal_8);
                palavrasImagens.put("CACHORRO", R.drawable.conectar_animal_9);
                palavrasImagens.put("GALINHA", R.drawable.conectar_animal_10);
                palavrasImagens.put("PÁSSARO", R.drawable.conectar_animal_11);
                palavrasImagens.put("GATO", R.drawable.conectar_animal_12);
                break;

            case "LUGARES":
                palavrasImagens.put("CASA", R.drawable.conectar_lugar_1);
                palavrasImagens.put("CARRO", R.drawable.conectar_lugar_2);
                palavrasImagens.put("CASTELO", R.drawable.conectar_lugar_3);
                palavrasImagens.put("ESCOLA", R.drawable.conectar_lugar_4);
                palavrasImagens.put("MERCADO", R.drawable.conectar_lugar_5);
                palavrasImagens.put("PISCINA", R.drawable.conectar_lugar_6);
                palavrasImagens.put("HOSPITAL", R.drawable.conectar_lugar_7);
                palavrasImagens.put("VALE", R.drawable.conectar_lugar_8);
                palavrasImagens.put("PARQUE", R.drawable.conectar_lugar_9);
                palavrasImagens.put("MUSEU", R.drawable.conectar_lugar_10);
                palavrasImagens.put("PRAIA", R.drawable.conectar_lugar_11);
                palavrasImagens.put("CAMPO", R.drawable.conectar_lugar_12);
                break;

            case "COMIDAS":
                palavrasImagens.put("ARROZ", R.drawable.conectar_comida_1);
                palavrasImagens.put("PÃO", R.drawable.conectar_comida_2);
                palavrasImagens.put("SUCO", R.drawable.conectar_comida_3);
                palavrasImagens.put("SALGADO", R.drawable.conectar_comida_4);
                palavrasImagens.put("LANCHE", R.drawable.conectar_comida_5);
                palavrasImagens.put("CHOCOLATE", R.drawable.conectar_comida_6);
                palavrasImagens.put("FRUTAS", R.drawable.conectar_comida_7);
                palavrasImagens.put("GELATINA", R.drawable.conectar_comida_8);
                palavrasImagens.put("VERDURAS", R.drawable.conectar_comida_9);
                palavrasImagens.put("PIZZA", R.drawable.conectar_comida_10);
                palavrasImagens.put("DOCES", R.drawable.conectar_comida_11);
                palavrasImagens.put("NUGGETS", R.drawable.conectar_comida_12);
                break;
        }
    }

    private void criarJogo() {
        List<String> todasPalavras = new ArrayList<>(palavrasImagens.keySet());
        Collections.shuffle(todasPalavras);
        palavrasSelecionadas = todasPalavras.subList(0, quantidade);

        if (dificuldade.equals("FACIL")) {
            gridImagens.setColumnCount(1);
        } else {
            gridImagens.setColumnCount(2);
        }
        gridImagens.removeAllViews();

        for (String palavra : palavrasSelecionadas) {
            LinearLayout containerImagem = criarContainerImagem(palavra);
            gridImagens.addView(containerImagem);
        }

        if (dificuldade.equals("FACIL")) {
            gridPalavras.setColumnCount(1);
        } else {
            gridPalavras.setColumnCount(2);
        }
        gridPalavras.removeAllViews();

        List<String> palavrasEmbaralhadas = new ArrayList<>(palavrasSelecionadas);
        Collections.shuffle(palavrasEmbaralhadas);

        palavrasViews.clear();
        int index = 0;
        for (String palavra : palavrasEmbaralhadas) {
            ContornoTextView tvPalavra = criarPalavra(palavra);
            palavrasPosicoesOriginais.put(tvPalavra, index);
            palavrasViews.add(tvPalavra);
            gridPalavras.addView(tvPalavra);
            index++;
        }
    }
    private LinearLayout criarContainerImagem(String palavra) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(android.view.Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

        if (dificuldade.equals("DIFICIL")) {
            params.setMargins(3, 8, 3, 8); // 3dp nas laterais = pequeno espaço entre imagens
            container.setPadding(0, 0, 0, 0);
        } else {
            params.setMargins(16, 16, 16, 16);
            container.setPadding(12, 12, 12, 12);
        }

        container.setLayoutParams(params);

        FrameLayout imageContainer = new FrameLayout(this);
        LinearLayout.LayoutParams imageContainerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );

        if (dificuldade.equals("MEDIO")) {
            imageContainerParams.setMargins(4, 0, 4, -40);
        } else if (dificuldade.equals("DIFICIL")) {
            imageContainerParams.setMargins(0, 0, 0, 0);
        } else {
            imageContainerParams.setMargins(4, 0, 4, 4);
        }

        imageContainer.setLayoutParams(imageContainerParams);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(palavrasImagens.get(palavra));

        if (dificuldade.equals("DIFICIL")) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        imageView.setAdjustViewBounds(false);
        imageView.setPadding(0, 0, 0, 0);

        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        imageView.setLayoutParams(imageParams);

        imageContainer.addView(imageView);
        container.addView(imageContainer);
        imagensMap.put(imageView, palavra);

        FrameLayout palavraContainer = new FrameLayout(this);
        palavraContainer.setTag("palavra_container");

        int alturaFixaPalavra;
        int margemLateralPalavra;
        int margemTopPalavra;

        switch (dificuldade) {
            case "FACIL":
                alturaFixaPalavra = 130;
                margemLateralPalavra = 190;
                margemTopPalavra = 4;
                break;
            case "MEDIO":
                alturaFixaPalavra = 120;
                margemLateralPalavra = 16;
                margemTopPalavra = -40;
                break;
            case "DIFICIL":
                alturaFixaPalavra = 100;
                margemLateralPalavra = 23; // 23dp = pequeno espaço nas laterais do texto
                margemTopPalavra = 2;
                break;
            default:
                alturaFixaPalavra = 100;
                margemLateralPalavra = 16;
                margemTopPalavra = 4;
                break;
        }

        LinearLayout.LayoutParams palavraContainerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                alturaFixaPalavra
        );
        palavraContainerParams.setMargins(margemLateralPalavra, margemTopPalavra, margemLateralPalavra, 0);
        palavraContainer.setLayoutParams(palavraContainerParams);

        container.addView(palavraContainer);

        container.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return onDragImagem(v, event, imageView, palavraContainer);
            }
        });

        return container;
    }

    private ContornoTextView criarPalavra(String palavra) {
        ContornoTextView textView = new ContornoTextView(this);
        textView.setText(palavra);

        switch (dificuldade) {
            case "FACIL":
                textView.setTextSize(24);
                textView.setPadding(20, 14, 20, 14);
                textView.setMinWidth(250);
                break;
            case "MEDIO":
                textView.setTextSize(18);
                textView.setPadding(14, 14, 14, 14);
                textView.setMinWidth(150);
                break;
            case "DIFICIL":
                textView.setTextSize(21);
                textView.setPadding(18, 16, 18, 16);
                textView.setMinWidth(150);
                break;
            default:
                textView.setTextSize(15);
                textView.setPadding(14, 8, 14, 8);
                textView.setMinWidth(150);
                break;
        }

        textView.setGravity(android.view.Gravity.CENTER);
        textView.setIncludeFontPadding(false);
        textView.setMaxLines(2);

        textView.setStrokeWidth(6f);
        textView.setStrokeColor(Color.BLACK);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(15f);
        drawable.setColor(Color.WHITE);
        drawable.setStroke(4, Color.parseColor("#2C3E50"));
        textView.setBackground(drawable);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(12, 12, 12, 12);
        textView.setLayoutParams(params);

        palavrasMap.put(textView, palavra);

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ultimoToqueTempo = System.currentTimeMillis();

                    if (palavrasCorretasMap.containsValue(textView)) {
                        return false;
                    }

                    if (emCooldown) {
                        return false;
                    }

                    if (v.getVisibility() != View.VISIBLE) {
                        return false;
                    }

                    palavraAtualmenteArrastando = textView;
                    iniciarCooldown();

                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    boolean dragIniciado = v.startDragAndDrop(data, shadowBuilder, v, 0);

                    if (dragIniciado) {
                        v.setVisibility(View.INVISIBLE);
                    } else {
                        palavraAtualmenteArrastando = null;
                        emCooldown = false;
                    }

                    return true;
                }
                return false;
            }
        });

        return textView;
    }

    private void iniciarMonitor() {
        monitorAtivo = true;
        monitorRunnable = new Runnable() {
            @Override
            public void run() {
                if (monitorAtivo) {
                    long tempoAtual = System.currentTimeMillis();

                    if (palavraAtualmenteArrastando != null &&
                            (tempoAtual - ultimoToqueTempo) > 2000) {

                        palavraAtualmenteArrastando.setVisibility(View.VISIBLE);
                        palavraAtualmenteArrastando.setAlpha(1.0f);
                        palavraAtualmenteArrastando = null;
                        emCooldown = false;

                        if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
                            animacaoCooldown.cancel();
                        }

                        atualizarCorPalavras(false);
                        restaurarAlphaPalavras();
                    }

                    for (ContornoTextView palavra : palavrasViews) {
                        if (palavra.getVisibility() == View.INVISIBLE &&
                                !palavrasCorretasMap.containsValue(palavra) &&
                                palavraAtualmenteArrastando != palavra) {

                            palavra.setVisibility(View.VISIBLE);
                            palavra.setAlpha(1.0f);
                        }
                    }

                    monitorHandler.postDelayed(this, 100);
                }
            }
        };
        monitorHandler.postDelayed(monitorRunnable, 100);
    }

    private void pararMonitor() {
        monitorAtivo = false;
        monitorHandler.removeCallbacks(monitorRunnable);
    }

    private void iniciarCooldown() {
        cooldownHandler.removeCallbacksAndMessages(null);
        if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
            animacaoCooldown.cancel();
        }

        emCooldown = true;
        atualizarCorPalavras(true);
        iniciarAnimacaoCarregamento();

        cooldownHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                emCooldown = false;

                if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
                    animacaoCooldown.cancel();
                }

                atualizarCorPalavras(false);
                restaurarAlphaPalavras();
            }
        }, 1000);
    }

    private void iniciarAnimacaoCarregamento() {
        animacaoCooldown = ValueAnimator.ofFloat(0.4f, 1.0f);
        animacaoCooldown.setDuration(500);
        animacaoCooldown.setRepeatMode(ValueAnimator.REVERSE);
        animacaoCooldown.setRepeatCount(ValueAnimator.INFINITE);
        animacaoCooldown.setInterpolator(new AccelerateDecelerateInterpolator());

        animacaoCooldown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();

                for (ContornoTextView palavra : palavrasViews) {
                    if (!palavrasCorretasMap.containsValue(palavra)) {
                        palavra.setAlpha(alpha);
                    }
                }
            }
        });

        animacaoCooldown.start();
    }

    private void restaurarAlphaPalavras() {
        for (ContornoTextView palavra : palavrasViews) {
            if (!palavrasCorretasMap.containsValue(palavra)) {
                palavra.setAlpha(1.0f);
            }
        }
    }

    private void atualizarCorPalavras(boolean bloqueado) {
        for (ContornoTextView palavra : palavrasViews) {
            if (!palavrasCorretasMap.containsValue(palavra)) {
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(15f);

                if (bloqueado) {
                    drawable.setColor(Color.parseColor("#CCCCCC"));
                    drawable.setStroke(4, Color.parseColor("#999999"));
                } else {
                    drawable.setColor(Color.WHITE);
                    drawable.setStroke(4, Color.parseColor("#2C3E50"));
                }

                palavra.setBackground(drawable);
            }
        }
    }

    private boolean onDragImagem(View container, DragEvent event, ImageView imageView, FrameLayout palavraContainer) {
        ContornoTextView palavraArrastada = (ContornoTextView) event.getLocalState();

        if (palavraArrastada == null) {
            return false;
        }

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                container.setAlpha(0.7f);
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                container.setAlpha(1.0f);
                return true;

            case DragEvent.ACTION_DROP:
                container.setAlpha(1.0f);

                String palavraTexto = palavrasMap.get(palavraArrastada);
                String imagemPalavra = imagensMap.get(imageView);

                tentativas++;
                atualizarTentativas();

                if (palavraTexto != null && palavraTexto.equals(imagemPalavra)) {
                    // CORRETO!
                    Integer posicaoOriginal = palavrasPosicoesOriginais.get(palavraArrastada);
                    if (posicaoOriginal != null && palavraArrastada.getParent() != null) {
                        View espacoVazio = new View(JogosConectarGameplayActivity.this);
                        GridLayout.LayoutParams emptyParams = new GridLayout.LayoutParams();
                        emptyParams.width = 0;
                        emptyParams.height = palavraArrastada.getHeight();
                        emptyParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                        emptyParams.setMargins(12, 12, 12, 12);
                        espacoVazio.setLayoutParams(emptyParams);

                        gridPalavras.removeView(palavraArrastada);
                        gridPalavras.addView(espacoVazio, posicaoOriginal);
                    }

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(15f);
                    drawable.setColor(Color.parseColor("#27AE60"));
                    drawable.setStroke(4, Color.parseColor("#1E8449"));
                    palavraArrastada.setBackground(drawable);

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.gravity = android.view.Gravity.CENTER;
                    palavraArrastada.setLayoutParams(params);

                    palavraArrastada.setVisibility(View.VISIBLE);
                    palavraArrastada.setAlpha(1.0f);

                    palavraContainer.removeAllViews();
                    palavraContainer.addView(palavraArrastada);

                    palavraArrastada.post(new Runnable() {
                        @Override
                        public void run() {
                            palavraArrastada.setVisibility(View.VISIBLE);
                            palavraArrastada.setAlpha(1.0f);
                            palavraArrastada.requestLayout();
                        }
                    });

                    palavrasCorretasMap.put(imageView, palavraArrastada);
                    palavraArrastada.setOnTouchListener(null);
                    palavraAtualmenteArrastando = null;

                    verificarVitoria();
                } else {
                    // ERRADO! Mostra mensagem
                    palavraArrastada.setVisibility(View.VISIBLE);
                    palavraArrastada.setAlpha(1.0f);
                    palavraAtualmenteArrastando = null;

                    // Mostra Toast informando que está incorreto
                    android.widget.Toast.makeText(
                            JogosConectarGameplayActivity.this,
                            "Ops! Essa não é a imagem correta.",
                            android.widget.Toast.LENGTH_SHORT
                    ).show();
                }

                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                if (!palavrasCorretasMap.containsValue(palavraArrastada)) {
                    palavraArrastada.setVisibility(View.VISIBLE);
                    palavraArrastada.setAlpha(1.0f);
                }

                palavraAtualmenteArrastando = null;
                container.setAlpha(1.0f);
                return true;

            default:
                return false;
        }
    }


    private void verificarVitoria() {
        if (palavrasCorretasMap.size() == quantidade) {
            pararTimer();
            pararMonitor();

            if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
                animacaoCooldown.cancel();
            }

            new JogosMemoriaConfetes(this, findViewById(android.R.id.content)).iniciar();
        }
    }

    private void reiniciarJogo() {
        imagensMap.clear();
        palavrasMap.clear();
        palavrasCorretasMap.clear();
        palavrasPosicoesOriginais.clear();
        palavrasViews.clear();

        emCooldown = false;
        palavraAtualmenteArrastando = null;
        ultimoToqueTempo = 0;
        cooldownHandler.removeCallbacksAndMessages(null);
        if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
            animacaoCooldown.cancel();
        }

        pararTimer();
        pararMonitor();
        segundos = 0;
        tentativas = 0;
        atualizarTimer();
        atualizarTentativas();

        criarJogo();
        iniciarTimer();
        iniciarMonitor();
    }

    private void configurarBotoes() {
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pararTimer();
                pararMonitor();
                cooldownHandler.removeCallbacksAndMessages(null);
                if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
                    animacaoCooldown.cancel();
                }
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

    private void atualizarTentativas() {
        tvTentativas.setText("Tentativas: " + tentativas);
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
        pararMonitor();
        if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
            animacaoCooldown.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararTimer();
        pararMonitor();
        cooldownHandler.removeCallbacksAndMessages(null);
        if (animacaoCooldown != null && animacaoCooldown.isRunning()) {
            animacaoCooldown.cancel();
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

    public static class ContornoTextView extends AppCompatTextView {
        private float strokeWidth = 8f;
        private int strokeColor = Color.BLACK;

        public ContornoTextView(Context context) {
            super(context);
            init();
        }

        public ContornoTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ContornoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        private void init() {
            setTextColor(Color.WHITE);
        }

        public void setStrokeWidth(float width) {
            this.strokeWidth = width;
            invalidate();
        }

        public void setStrokeColor(int color) {
            this.strokeColor = color;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = getPaint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            setTextColor(strokeColor);
            super.onDraw(canvas);

            paint.setStyle(Paint.Style.FILL);
            setTextColor(Color.WHITE);
            super.onDraw(canvas);
        }
    }
}
