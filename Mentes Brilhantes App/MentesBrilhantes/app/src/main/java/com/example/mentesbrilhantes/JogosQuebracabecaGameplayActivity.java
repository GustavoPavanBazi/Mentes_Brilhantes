package com.example.mentesbrilhantes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class JogosQuebracabecaGameplayActivity extends AppCompatActivity {

    public ArrayList<JogosQuebracabecaPecas> pieces;
    private int cols = 3;
    private int rows = 5;
    private int totalPieces;

    private RelativeLayout layoutMain;
    private LinearLayout piecesContainer;
    private HorizontalScrollView scrollView;
    private ImageView imageView;
    private JogosQuebracabecaGrade gridOverlay;

    private int boardLeft;
    private int boardTop;
    private int boardRight;
    private int boardBottom;

    private JogosQuebracabecaConfetes jogosQuebracabecaConfetes;
    private TextView txtParabens;
    private RelativeLayout victoryScreen;
    private RelativeLayout containerBottom;
    private ImageButton btnJogarNovamente;
    private ImageButton btnSairJogo;

    private TextView txtTimer;
    private TextView txtPiecesCounter;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime;
    private long elapsedTime;
    private boolean isTimerRunning;
    private int placedPieces;

    private int[] puzzleImages = {
            R.drawable.fundo_jogos_quebracabeca1,
            R.drawable.fundo_jogos_quebracabeca2,
            R.drawable.fundo_jogos_quebracabeca3,
            R.drawable.fundo_jogos_quebracabeca4,
            R.drawable.fundo_jogos_quebracabeca5,
            R.drawable.fundo_jogos_quebracabeca6,
            R.drawable.fundo_jogos_quebracabeca7,
            R.drawable.fundo_jogos_quebracabeca8,
            R.drawable.fundo_jogos_quebracabeca9,
            R.drawable.fundo_jogos_quebracabeca10
    };

    private int currentImageResource;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("COLS") && intent.hasExtra("ROWS")) {
            cols = intent.getIntExtra("COLS", 3);
            rows = intent.getIntExtra("ROWS", 5);
        }

        totalPieces = cols * rows;

        configurarFullscreen();
        setContentView(R.layout.activity_jogos_quebracabeca_gameplay);

        layoutMain = findViewById(R.id.layout_main);
        piecesContainer = findViewById(R.id.piecesContainer);
        scrollView = findViewById(R.id.horizontalScrollContainer);
        imageView = findViewById(R.id.imageView);
        gridOverlay = findViewById(R.id.gridOverlay);
        containerBottom = findViewById(R.id.containerBottom);

        jogosQuebracabecaConfetes = findViewById(R.id.confettiView);
        txtParabens = findViewById(R.id.txt_parabens);
        victoryScreen = findViewById(R.id.victoryScreen);
        btnJogarNovamente = findViewById(R.id.btn_jogar_novamente);
        btnSairJogo = findViewById(R.id.btn_sair_jogo);

        txtTimer = findViewById(R.id.txt_timer);
        txtPiecesCounter = findViewById(R.id.txt_pieces_counter);

        boardLeft = 0;
        boardTop = 0;
        boardRight = 0;
        boardBottom = 0;

        random = new Random();

        configurarBotoesVitoria();
        inicializarTimer();
        iniciarJogo();
    }

    private int sortearImagemAleatoria() {
        int randomIndex = random.nextInt(puzzleImages.length);
        return puzzleImages[randomIndex];
    }

    // Configura timer do jogo
    private void inicializarTimer() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isTimerRunning) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    int seconds = (int) (elapsedTime / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;

                    txtTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                    timerHandler.postDelayed(this, 500);
                }
            }
        };
    }

    private void iniciarTimer() {
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
        isTimerRunning = true;
        timerHandler.post(timerRunnable);
    }

    private void pararTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetarTimer() {
        pararTimer();
        txtTimer.setText("00:00");
    }

    // Atualiza contador de pecas encaixadas
    private void atualizarContadorPecas() {
        txtPiecesCounter.setText(String.format(Locale.getDefault(), "%d / %d", placedPieces, totalPieces));

        if (placedPieces == totalPieces) {
            txtPiecesCounter.setTextColor(Color.rgb(76, 175, 80));
        } else if (placedPieces > totalPieces / 2) {
            txtPiecesCounter.setTextColor(Color.rgb(255, 152, 0));
        } else {
            txtPiecesCounter.setTextColor(Color.BLACK);
        }
    }

    private void configurarBotoesVitoria() {
        configurarBotaoComAnimacao(btnJogarNovamente, new Runnable() {
            @Override
            public void run() {
                reiniciarJogo();
            }
        });

        configurarBotaoComAnimacao(btnSairJogo, new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    // Adiciona animacao de escala no toque do botao
    private void configurarBotaoComAnimacao(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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

    // Inicia novo jogo com imagem aleatoria
    private void iniciarJogo() {
        placedPieces = 0;
        atualizarContadorPecas();
        resetarTimer();

        currentImageResource = sortearImagemAleatoria();
        imageView.setImageResource(currentImageResource);

        imageView.post(new Runnable() {
            @Override
            public void run() {
                setupGridOverlay();

                pieces = splitImage(imageView);
                Collections.shuffle(pieces);

                piecesContainer.removeAllViews();

                for (JogosQuebracabecaPecas piece : pieces) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            piece.pieceWidth, piece.pieceHeight);
                    params.setMargins(8, 8, 8, 8);

                    piece.setLayoutParams(params);
                    piece.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    piece.setAdjustViewBounds(false);

                    piece.setOnTouchListener(null);
                    piece.setOnClickListener(null);

                    piece.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            movePieceToGameArea(piece);
                        }
                    });

                    piecesContainer.addView(piece);
                }

                iniciarTimer();
            }
        });
    }

    // Reinicia jogo do zero
    private void reiniciarJogo() {
        jogosQuebracabecaConfetes.setVisibility(View.GONE);
        txtParabens.setVisibility(View.GONE);
        victoryScreen.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);

        ArrayList<View> viewsToRemove = new ArrayList<>();
        for (int i = 0; i < layoutMain.getChildCount(); i++) {
            View child = layoutMain.getChildAt(i);
            if (child instanceof JogosQuebracabecaPecas) {
                viewsToRemove.add(child);
            }
        }

        for (View view : viewsToRemove) {
            layoutMain.removeView(view);
        }

        gridOverlay.setGridSize(cols, rows);
        gridOverlay.clearHiddenLines();
        gridOverlay.invalidate();

        imageView.setAlpha(0.4f);

        iniciarJogo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTimerRunning) {
            pararTimer();
        }
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

    // Configura grid overlay sobre a imagem
    private void setupGridOverlay() {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) return;

        int viewWidth = imageView.getWidth();
        int viewHeight = imageView.getHeight();

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();

        float scale = Math.min(
                (float) viewWidth / drawableWidth,
                (float) viewHeight / drawableHeight
        );

        int scaledWidth = (int) (drawableWidth * scale);
        int scaledHeight = (int) (drawableHeight * scale);

        int offsetX = (viewWidth - scaledWidth) / 2;
        int offsetY = (viewHeight - scaledHeight) / 2;

        gridOverlay.setGridSize(cols, rows);
        gridOverlay.setImageBounds(offsetX, offsetY, scaledWidth, scaledHeight);

        boardLeft = offsetX + imageView.getLeft();
        boardTop = offsetY + imageView.getTop();
        boardRight = boardLeft + scaledWidth;
        boardBottom = boardTop + scaledHeight;
    }

    // Move peca do container inicial para area de jogo
    private void movePieceToGameArea(JogosQuebracabecaPecas piece) {
        piecesContainer.removeView(piece);

        RelativeLayout.LayoutParams gameParams = new RelativeLayout.LayoutParams(
                piece.pieceWidth, piece.pieceHeight);

        gameParams.leftMargin = (layoutMain.getWidth() - piece.pieceWidth) / 2;
        gameParams.topMargin = (layoutMain.getHeight() - piece.pieceHeight) / 2;

        piece.setLayoutParams(gameParams);
        piece.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        piece.setAdjustViewBounds(false);

        layoutMain.addView(piece);
        piece.bringToFront();

        piece.setOnTouchListener(new PieceTouchListener());
        piece.setOnClickListener(null);
    }

    // Divide imagem em pecas do quebra-cabeca
    private ArrayList<JogosQuebracabecaPecas> splitImage(ImageView imageView) {
        ArrayList<JogosQuebracabecaPecas> pieces = new ArrayList<>(rows * cols);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        int imageViewWidth = imageView.getWidth();
        int imageViewHeight = imageView.getHeight();

        float scale = Math.min(
                (float)imageViewWidth / bitmap.getWidth(),
                (float)imageViewHeight / bitmap.getHeight()
        );

        int scaledWidth = (int)(bitmap.getWidth() * scale);
        int scaledHeight = (int)(bitmap.getHeight() * scale);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);

        int offsetX = (imageViewWidth - scaledWidth) / 2;
        int offsetY = (imageViewHeight - scaledHeight) / 2;

        int pieceWidth = scaledWidth / cols;
        int pieceHeight = scaledHeight / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Bitmap chunk = Bitmap.createBitmap(
                        scaledBitmap,
                        col * pieceWidth,
                        row * pieceHeight,
                        pieceWidth,
                        pieceHeight);

                Bitmap chunkWithBorder = chunk.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(chunkWithBorder);

                Paint borderPaint = new Paint();
                borderPaint.setColor(Color.BLACK);
                borderPaint.setStyle(Paint.Style.STROKE);
                borderPaint.setStrokeWidth(6);
                borderPaint.setAntiAlias(true);
                canvas.drawRect(3, 3, pieceWidth - 3, pieceHeight - 3, borderPaint);

                JogosQuebracabecaPecas piece = new JogosQuebracabecaPecas(getApplicationContext());
                piece.setImageBitmap(chunkWithBorder);
                piece.originalBitmap = chunk;

                piece.xCoord = col * pieceWidth + offsetX + imageView.getLeft();
                piece.yCoord = row * pieceHeight + offsetY + imageView.getTop();
                piece.pieceWidth = pieceWidth;
                piece.pieceHeight = pieceHeight;
                piece.row = row;
                piece.col = col;
                piece.group = new ArrayList<>();
                piece.group.add(piece);
                piece.canMove = true;

                pieces.add(piece);
            }
        }

        return pieces;
    }

    // Verifica se todas as pecas foram encaixadas
    public void checkGameOver() {
        for (JogosQuebracabecaPecas piece : pieces) {
            if (piece.canMove) {
                return;
            }
        }

        pararTimer();
        mostrarTelaVitoria();
    }

    // Exibe tela de vitoria com animacoes
    private void mostrarTelaVitoria() {
        jogosQuebracabecaConfetes.startConfetti();

        int seconds = (int) (elapsedTime / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        String mensagem = String.format(Locale.getDefault(),
                "🎉 Parabéns!\nVocê completou o quebra-cabeça!\n⏱️ Tempo: %02d:%02d 🎉",
                minutes, seconds);

        txtParabens.setText(mensagem);
        txtParabens.setVisibility(View.VISIBLE);
        txtParabens.setAlpha(0f);
        txtParabens.setScaleX(0.5f);
        txtParabens.setScaleY(0.5f);
        txtParabens.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.setVisibility(View.GONE);

                victoryScreen.setVisibility(View.VISIBLE);
                victoryScreen.setAlpha(0f);
                victoryScreen.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtParabens.animate()
                                .alpha(0f)
                                .setDuration(500)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtParabens.setVisibility(View.GONE);
                                    }
                                })
                                .start();
                    }
                }, 2000);
            }
        }, 1500);
    }

    // Listener para arrastar e soltar pecas
    private class PieceTouchListener implements View.OnTouchListener {
        private float xDelta;
        private float yDelta;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            JogosQuebracabecaPecas piece = (JogosQuebracabecaPecas) view;

            if (!piece.canMove) return true;

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.getLayoutParams();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xDelta = event.getRawX() - params.leftMargin;
                    yDelta = event.getRawY() - params.topMargin;

                    for (JogosQuebracabecaPecas groupPiece : piece.group) {
                        groupPiece.bringToFront();
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) (event.getRawX() - xDelta) - params.leftMargin;
                    int deltaY = (int) (event.getRawY() - yDelta) - params.topMargin;

                    for (JogosQuebracabecaPecas groupPiece : piece.group) {
                        RelativeLayout.LayoutParams groupParams =
                                (RelativeLayout.LayoutParams) groupPiece.getLayoutParams();

                        int newLeftMargin = groupParams.leftMargin + deltaX;
                        int newTopMargin = groupParams.topMargin + deltaY;

                        if (newLeftMargin < boardLeft) {
                            newLeftMargin = boardLeft;
                        }
                        if (newLeftMargin + groupPiece.pieceWidth > boardRight) {
                            newLeftMargin = boardRight - groupPiece.pieceWidth;
                        }
                        if (newTopMargin < boardTop) {
                            newTopMargin = boardTop;
                        }
                        if (newTopMargin + groupPiece.pieceHeight > boardBottom) {
                            newTopMargin = boardBottom - groupPiece.pieceHeight;
                        }

                        groupParams.leftMargin = newLeftMargin;
                        groupParams.topMargin = newTopMargin;
                        groupParams.width = groupPiece.pieceWidth;
                        groupParams.height = groupPiece.pieceHeight;
                        groupPiece.setLayoutParams(groupParams);
                    }

                    params = (RelativeLayout.LayoutParams) piece.getLayoutParams();
                    xDelta = event.getRawX() - params.leftMargin;
                    yDelta = event.getRawY() - params.topMargin;
                    break;

                case MotionEvent.ACTION_UP:
                    int tolerance = 80;
                    int xDiff = Math.abs(piece.xCoord - params.leftMargin);
                    int yDiff = Math.abs(piece.yCoord - params.topMargin);

                    if (xDiff <= tolerance && yDiff <= tolerance) {
                        for (JogosQuebracabecaPecas groupPiece : piece.group) {
                            RelativeLayout.LayoutParams groupParams =
                                    (RelativeLayout.LayoutParams) groupPiece.getLayoutParams();
                            groupParams.leftMargin = groupPiece.xCoord;
                            groupParams.topMargin = groupPiece.yCoord;
                            groupParams.width = groupPiece.pieceWidth;
                            groupParams.height = groupPiece.pieceHeight;
                            groupPiece.setLayoutParams(groupParams);
                            groupPiece.canMove = false;

                            if (groupPiece.originalBitmap != null) {
                                groupPiece.setImageBitmap(groupPiece.originalBitmap);
                            }

                            placedPieces++;
                        }

                        atualizarContadorPecas();
                        checkAndConnectAdjacentPieces(piece);

                        for (JogosQuebracabecaPecas groupPiece : piece.group) {
                            sendViewToBack(groupPiece);
                        }

                        gridOverlay.bringToFront();
                        JogosQuebracabecaGameplayActivity.this.checkGameOver();
                    }
                    break;
            }
            return true;
        }
    }

    // Verifica e conecta pecas adjacentes que ja estao encaixadas
    private void checkAndConnectAdjacentPieces(JogosQuebracabecaPecas piece) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int adjacentRow = piece.row + dir[0];
            int adjacentCol = piece.col + dir[1];

            for (JogosQuebracabecaPecas otherPiece : pieces) {
                if (otherPiece.row == adjacentRow &&
                        otherPiece.col == adjacentCol &&
                        !otherPiece.canMove) {

                    gridOverlay.hideLineBetweenPieces(
                            piece.row, piece.col,
                            otherPiece.row, otherPiece.col
                    );

                    mergeGroups(piece, otherPiece);
                }
            }
        }
    }

    // Junta dois grupos de pecas em um só
    private void mergeGroups(JogosQuebracabecaPecas piece1, JogosQuebracabecaPecas piece2) {
        ArrayList<JogosQuebracabecaPecas> newGroup = new ArrayList<>();
        newGroup.addAll(piece1.group);

        for (JogosQuebracabecaPecas p : piece2.group) {
            if (!newGroup.contains(p)) {
                newGroup.add(p);
            }
        }

        for (JogosQuebracabecaPecas p : newGroup) {
            p.group = newGroup;
        }
    }

    private void sendViewToBack(final View child) {
        if (child.getParent() instanceof RelativeLayout) {
            RelativeLayout parent = (RelativeLayout) child.getParent();
            parent.removeView(child);
            parent.addView(child, 0);
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