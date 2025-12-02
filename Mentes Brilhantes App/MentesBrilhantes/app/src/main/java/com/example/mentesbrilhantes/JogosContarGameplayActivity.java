package com.example.mentesbrilhantes;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class JogosContarGameplayActivity extends AppCompatActivity {

    private ConstraintLayout layoutPrincipal;
    private RelativeLayout areaImagens;
    private TextView tvNumero, tvTimer, tvTentativas;
    private ImageButton btnDiminuir, btnAumentar, btnSair, btnJogarNovamente, btnConfirmar;

    private int numeroAtual = 0;
    private int quantidadeCorreta = 0;
    private int imagemSelecionada = 0;
    private int tentativas = 0;
    private int segundos = 0;

    private Random random;
    private List<ImageView> imagensNaTela;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private boolean timerAtivo = false;
    private boolean jogoFinalizado = false;

    // Array com 10 imagens do jogo
    private int[] imagensDisponiveis = {
            R.drawable.contar_imagem_1,
            R.drawable.contar_imagem_2,
            R.drawable.contar_imagem_3,
            R.drawable.contar_imagem_4,
            R.drawable.contar_imagem_5,
            R.drawable.contar_imagem_6,
            R.drawable.contar_imagem_7,
            R.drawable.contar_imagem_8,
            R.drawable.contar_imagem_9,
            R.drawable.contar_imagem_10
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_contar_gameplay);

        inicializarComponentes();
        configurarBotoes();
        iniciarNovoJogo();
    }

    private void inicializarComponentes() {
        layoutPrincipal = findViewById(R.id.layout_principal);
        areaImagens = findViewById(R.id.area_imagens);
        tvNumero = findViewById(R.id.tv_numero);
        tvTimer = findViewById(R.id.tv_timer);
        tvTentativas = findViewById(R.id.tv_tentativas);
        btnDiminuir = findViewById(R.id.btn_diminuir);
        btnAumentar = findViewById(R.id.btn_aumentar);
        btnConfirmar = findViewById(R.id.btn_confirmar);
        btnSair = findViewById(R.id.btn_sair);
        btnJogarNovamente = findViewById(R.id.btn_jogar_novamente);

        random = new Random();
        imagensNaTela = new ArrayList<>();
    }

    private void configurarBotoes() {
        // Botão Diminuir
        configurarBotaoComAnimacao(btnDiminuir, new Runnable() {
            @Override
            public void run() {
                if (!jogoFinalizado && numeroAtual > 0) {
                    numeroAtual--;
                    atualizarNumero();
                }
            }
        });

        // Botão Aumentar
        configurarBotaoComAnimacao(btnAumentar, new Runnable() {
            @Override
            public void run() {
                if (!jogoFinalizado && numeroAtual < 10) {
                    numeroAtual++;
                    atualizarNumero();
                }
            }
        });

        // Botão Confirmar
        configurarBotaoComAnimacao(btnConfirmar, new Runnable() {
            @Override
            public void run() {
                if (!jogoFinalizado && btnConfirmar.isEnabled()) {
                    verificarResposta();
                }
            }
        });

        // Botão Sair
        configurarBotaoComAnimacao(btnSair, new Runnable() {
            @Override
            public void run() {
                pararTimer();
                finish();
            }
        });

        // Botão Jogar Novamente
        configurarBotaoComAnimacao(btnJogarNovamente, new Runnable() {
            @Override
            public void run() {
                if (btnJogarNovamente.isEnabled()) {
                    iniciarNovoJogo();
                }
            }
        });
    }

    private void configurarBotaoComAnimacao(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Verificar se o botão está habilitado antes de qualquer ação
                if (!v.isEnabled()) {
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.animate()
                                .scaleX(0.90f)
                                .scaleY(0.90f)
                                .setDuration(100)
                                .start();
                        return true;

                    case MotionEvent.ACTION_UP:
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        acao.run();
                                    }
                                })
                                .start();
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                        return true;
                }
                return false;
            }
        });
    }

    private void iniciarNovoJogo() {
        // Resetar flag de jogo finalizado
        jogoFinalizado = false;

        // Habilitar botões de controle
        btnDiminuir.setEnabled(true);
        btnAumentar.setEnabled(true);
        btnConfirmar.setEnabled(true);
        btnJogarNovamente.setEnabled(true);

        // Limpar imagens anteriores
        limparImagens();

        // Resetar valores
        numeroAtual = 0;
        tentativas = 0;
        segundos = 0;

        atualizarNumero();
        atualizarTentativas();
        atualizarTimer();

        // Resetar cor do número
        tvNumero.setTextColor(Color.WHITE);

        // Selecionar imagem aleatória
        imagemSelecionada = imagensDisponiveis[random.nextInt(imagensDisponiveis.length)];

        // Definir quantidade aleatória (1 a 10)
        quantidadeCorreta = random.nextInt(10) + 1;

        // Parar timer anterior e iniciar novo
        pararTimer();
        iniciarTimer();

        // Aguardar o layout estar pronto antes de posicionar as imagens
        areaImagens.post(new Runnable() {
            @Override
            public void run() {
                espalharImagens();
            }
        });
    }

    private void limparImagens() {
        for (ImageView img : imagensNaTela) {
            areaImagens.removeView(img);
        }
        imagensNaTela.clear();
    }

    private void espalharImagens() {
        int larguraArea = areaImagens.getWidth();
        int alturaArea = areaImagens.getHeight();

        if (larguraArea == 0 || alturaArea == 0) {
            return;
        }

        // Tamanho base das imagens (ajustável)
        int tamanhoBase = Math.min(larguraArea, alturaArea) / 5;
        int tamanhoMin = tamanhoBase - 40;
        int tamanhoMax = tamanhoBase + 40;

        List<PosicaoImagem> posicoesOcupadas = new ArrayList<>();

        for (int i = 0; i < quantidadeCorreta; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imagemSelecionada);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Tamanho aleatório
            int tamanho = tamanhoMin + random.nextInt(tamanhoMax - tamanhoMin);

            // Encontrar posição válida (sem sobreposição)
            PosicaoImagem posicao = encontrarPosicaoValida(
                    larguraArea, alturaArea, tamanho, posicoesOcupadas);

            if (posicao != null) {
                // Configurar layout
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        tamanho, tamanho);
                params.leftMargin = posicao.x;
                params.topMargin = posicao.y;
                imageView.setLayoutParams(params);

                // Rotação aleatória (-30 a +30 graus)
                float rotacao = -30 + random.nextFloat() * 60;
                imageView.setRotation(rotacao);

                // Adicionar à tela
                areaImagens.addView(imageView);
                imagensNaTela.add(imageView);

                // Registrar posição ocupada
                posicoesOcupadas.add(posicao);
            }
        }
    }

    private PosicaoImagem encontrarPosicaoValida(int larguraArea, int alturaArea,
                                                 int tamanho, List<PosicaoImagem> ocupadas) {
        int tentativas = 0;
        int maxTentativas = 100;

        while (tentativas < maxTentativas) {
            int x = random.nextInt(Math.max(1, larguraArea - tamanho));
            int y = random.nextInt(Math.max(1, alturaArea - tamanho));

            PosicaoImagem novaPosicao = new PosicaoImagem(x, y, tamanho);

            // Verificar se não sobrepõe com nenhuma imagem existente
            boolean valida = true;
            for (PosicaoImagem ocupada : ocupadas) {
                if (novaPosicao.sobrepoe(ocupada)) {
                    valida = false;
                    break;
                }
            }

            if (valida) {
                return novaPosicao;
            }

            tentativas++;
        }

        // Se não encontrar posição após tentativas, retorna uma posição qualquer
        return new PosicaoImagem(
                random.nextInt(Math.max(1, larguraArea - tamanho)),
                random.nextInt(Math.max(1, alturaArea - tamanho)),
                tamanho
        );
    }

    private void atualizarNumero() {
        tvNumero.setText(String.valueOf(numeroAtual));
    }

    private void verificarResposta() {
        tentativas++;
        atualizarTentativas();

        if (numeroAtual == quantidadeCorreta) {
            // ACERTOU!
            jogoFinalizado = true;
            pararTimer();

            // Desabilitar botões de controle
            btnDiminuir.setEnabled(false);
            btnAumentar.setEnabled(false);
            btnConfirmar.setEnabled(false);
            btnJogarNovamente.setEnabled(false); // Desabilita durante o toast

            mostrarVitoria();
        } else {
            // ERROU!
            mostrarErro();
        }
    }

    private void mostrarVitoria() {
        // Mostrar confetes
        new JogosMemoriaConfetes(JogosContarGameplayActivity.this,
                findViewById(android.R.id.content)).iniciar();

        // Toast de parabéns (tempo reduzido)
        Toast.makeText(JogosContarGameplayActivity.this,
                "🎉 Parabéns! Você acertou! 🎉",
                Toast.LENGTH_SHORT).show();

        // Destacar número correto em verde
        tvNumero.setTextColor(Color.rgb(76, 175, 80));

        // Animar número
        tvNumero.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvNumero.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(300)
                                .start();
                    }
                })
                .start();

        // Reiniciar automaticamente após o Toast sumir (2 segundos - LENGTH_SHORT)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iniciarNovoJogo();
            }
        }, 2000);
    }

    private void mostrarErro() {
        // Desabilitar botão confirmar durante o toast de erro
        btnConfirmar.setEnabled(false);

        // Toast de erro (tempo reduzido)
        Toast.makeText(JogosContarGameplayActivity.this,
                "❌ Tente novamente!",
                Toast.LENGTH_SHORT).show();

        // Destacar número em vermelho temporariamente
        tvNumero.setTextColor(Color.rgb(244, 67, 54));

        // Animar sacudida
        tvNumero.animate()
                .translationX(-20f)
                .setDuration(50)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvNumero.animate()
                                .translationX(20f)
                                .setDuration(50)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvNumero.animate()
                                                .translationX(0f)
                                                .setDuration(50)
                                                .start();
                                    }
                                })
                                .start();
                    }
                })
                .start();

        // Voltar para cor branca e reabilitar botão após o toast sumir (2 segundos)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvNumero.setTextColor(Color.WHITE);
                btnConfirmar.setEnabled(true);
            }
        }, 2000);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararTimer();
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

    // Classe auxiliar para gerenciar posições das imagens
    private static class PosicaoImagem {
        int x, y, tamanho;

        PosicaoImagem(int x, int y, int tamanho) {
            this.x = x;
            this.y = y;
            this.tamanho = tamanho;
        }

        boolean sobrepoe(PosicaoImagem outra) {
            // Margem de segurança para evitar imagens muito próximas
            int margem = 20;

            return !(x + tamanho + margem < outra.x ||
                    outra.x + outra.tamanho + margem < x ||
                    y + tamanho + margem < outra.y ||
                    outra.y + outra.tamanho + margem < y);
        }
    }
}
