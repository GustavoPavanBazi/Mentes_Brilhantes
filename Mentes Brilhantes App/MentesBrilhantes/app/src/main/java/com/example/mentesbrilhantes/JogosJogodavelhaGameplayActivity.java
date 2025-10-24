package com.example.mentesbrilhantes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class JogosJogodavelhaGameplayActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean playerXTurn = true;
    private boolean xStartsThisRound = true;
    private int roundCount = 0;
    private int playerXScore = 0;
    private int playerOScore = 0;
    private int drawScore = 0;
    private int lastMoveRow = -1;
    private int lastMoveCol = -1;

    private TextView txtCurrentPlayer;
    private TextView txtScore;
    private ImageButton btnReset, btnExit;
    private GridLayout board;
    private RelativeLayout mainLayout;
    private JogosJogodavelhaLinha winningLine;

    private String gameMode;
    private String botDifficulty;
    private boolean isPlayerX = true;
    private Random random = new Random();
    private boolean gameEnded = false;

    private int defaultBackgroundColor = Color.parseColor("#2C3E50");
    private int colorX = Color.parseColor("#3498DB");
    private int colorO = Color.parseColor("#E74C3C");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_jogos_jogodavelha_gameplay);

        Intent intent = getIntent();
        gameMode = intent.getStringExtra("MODE");
        botDifficulty = intent.getStringExtra("DIFFICULTY");

        mainLayout = findViewById(R.id.main_layout);
        txtCurrentPlayer = findViewById(R.id.txt_current_player);
        txtScore = findViewById(R.id.txt_score);
        btnReset = findViewById(R.id.btn_reset);
        btnExit = findViewById(R.id.btn_exit);
        board = findViewById(R.id.board);
        winningLine = findViewById(R.id.winning_line);

        createBoard();
        configurarBotoes();
        updateCurrentPlayerText();
        updateBackgroundColor();
    }

    // Cria o tabuleiro 3x3 com botoes
    private void createBoard() {
        int cellSize = (int) (getResources().getDisplayMetrics().widthPixels * 0.25);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button(this);
                button.setTextSize(48);
                button.setTextColor(Color.WHITE);
                button.setBackgroundColor(Color.parseColor("#34495E"));
                button.setShadowLayer(15, 0, 0, Color.BLACK);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.setMargins(4, 4, 4, 4);
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                button.setLayoutParams(params);

                buttons[i][j] = button;

                final int row = i;
                final int col = j;

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCellClick(row, col);
                    }
                });

                board.addView(button);
            }
        }
    }

    // Processa clique em uma celula do tabuleiro
    private void onCellClick(int row, int col) {
        if (gameEnded) return;
        if (!buttons[row][col].getText().toString().equals("")) return;

        if (gameMode.equals("PLAYER")) {
            makeMove(row, col);
        } else if (gameMode.equals("BOT")) {
            if (playerXTurn) {
                makeMove(row, col);

                if (roundCount < 9 && !checkForWin() && !gameEnded) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            botMove();
                        }
                    }, 500);
                }
            }
        }
    }

    // Registra uma jogada no tabuleiro
    private void makeMove(int row, int col) {
        lastMoveRow = row;
        lastMoveCol = col;

        buttons[row][col].setText(playerXTurn ? "X" : "O");
        buttons[row][col].setTextColor(playerXTurn ? colorX : colorO);
        buttons[row][col].setShadowLayer(15, 0, 0, Color.BLACK);

        roundCount++;

        if (checkForWin()) {
            if (playerXTurn) {
                playerXWins();
            } else {
                playerOWins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            playerXTurn = !playerXTurn;
            updateCurrentPlayerText();
            updateBackgroundColor();
        }
    }

    // Muda cor de fundo conforme jogador da vez
    private void updateBackgroundColor() {
        if (gameEnded) return;
        mainLayout.setBackgroundColor(playerXTurn ? colorX : colorO);
    }

    // Logica de jogada do bot baseada na dificuldade
    private void botMove() {
        if (gameEnded) return;

        int[] move = null;

        switch (botDifficulty) {
            case "EASY":
                move = getEasyMove();
                break;
            case "MEDIUM":
                move = getMediumMove();
                break;
            case "HARD":
                move = getHardMove();
                break;
        }

        if (move != null) {
            makeMove(move[0], move[1]);
        }
    }

    // Bot facil - escolhe movimento aleatorio
    private int[] getEasyMove() {
        ArrayList<int[]> availableMoves = getAvailableMoves();
        if (!availableMoves.isEmpty()) {
            return availableMoves.get(random.nextInt(availableMoves.size()));
        }
        return null;
    }

    // Bot medio - alterna entre jogadas otimas e aleatorias
    private int[] getMediumMove() {
        if (random.nextBoolean()) {
            return getHardMove();
        } else {
            return getEasyMove();
        }
    }

    // Bot dificil - maioria jogadas otimas, algumas aleatorias
    private int[] getHardMove() {
        if (random.nextFloat() < 0.75f) {
            return getBestMove();
        } else {
            return getEasyMove();
        }
    }

    // Calcula melhor jogada possivel usando estrategia e minimax
    private int[] getBestMove() {
        ArrayList<int[]> availableMoves = getAvailableMoves();

        if (availableMoves.size() == 9) {
            int[][] preferredMoves = {{0, 0}, {0, 2}, {2, 0}, {2, 2}, {1, 1}};
            return preferredMoves[random.nextInt(preferredMoves.length)];
        }

        if (availableMoves.size() <= 6) {
            return minimax();
        }

        int[] winningMove = findWinningMove("O");
        if (winningMove != null) return winningMove;

        int[] blockingMove = findWinningMove("X");
        if (blockingMove != null) return blockingMove;

        return minimax();
    }

    // Encontra jogada que resulta em vitoria imediata
    private int[] findWinningMove(String player) {
        for (int[] move : getAvailableMoves()) {
            buttons[move[0]][move[1]].setText(player);
            boolean wins = checkForWinQuick();
            buttons[move[0]][move[1]].setText("");

            if (wins) {
                return move;
            }
        }
        return null;
    }

    // Verifica vitoria rapida sem efeitos visuais
    private boolean checkForWinQuick() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    // Encontra melhor jogada usando algoritmo minimax
    private int[] minimax() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;

        for (int[] move : getAvailableMoves()) {
            buttons[move[0]][move[1]].setText("O");
            int score = minimaxAlgorithm(false);
            buttons[move[0]][move[1]].setText("");

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    // Algoritmo minimax recursivo para calcular melhor jogada
    private int minimaxAlgorithm(boolean isMaximizing) {
        String winner = getWinner();
        if (winner != null) {
            if (winner.equals("O")) return 10;
            if (winner.equals("X")) return -10;
        }

        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int[] move : getAvailableMoves()) {
                buttons[move[0]][move[1]].setText("O");
                int score = minimaxAlgorithm(false);
                buttons[move[0]][move[1]].setText("");
                bestScore = Math.max(score, bestScore);
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int[] move : getAvailableMoves()) {
                buttons[move[0]][move[1]].setText("X");
                int score = minimaxAlgorithm(true);
                buttons[move[0]][move[1]].setText("");
                bestScore = Math.min(score, bestScore);
            }
            return bestScore;
        }
    }

    private ArrayList<int[]> getAvailableMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    // Retorna vencedor atual ou null se nao houver
    private String getWinner() {
        for (int i = 0; i < 3; i++) {
            if (checkLine(buttons[i][0].getText().toString(),
                    buttons[i][1].getText().toString(),
                    buttons[i][2].getText().toString())) {
                return buttons[i][0].getText().toString();
            }

            if (checkLine(buttons[0][i].getText().toString(),
                    buttons[1][i].getText().toString(),
                    buttons[2][i].getText().toString())) {
                return buttons[0][i].getText().toString();
            }
        }

        if (checkLine(buttons[0][0].getText().toString(),
                buttons[1][1].getText().toString(),
                buttons[2][2].getText().toString())) {
            return buttons[0][0].getText().toString();
        }

        if (checkLine(buttons[0][2].getText().toString(),
                buttons[1][1].getText().toString(),
                buttons[2][0].getText().toString())) {
            return buttons[0][2].getText().toString();
        }

        return null;
    }

    private boolean checkLine(String a, String b, String c) {
        return !a.equals("") && a.equals(b) && b.equals(c);
    }

    // Verifica vitoria e desenha linha visual
    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                drawWinningLineForRow(i);
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                drawWinningLineForColumn(i);
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            drawWinningLineForDiagonalMain();
            return true;
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            drawWinningLineForDiagonalSecondary();
            return true;
        }

        return false;
    }

    // Desenha linha de vitoria na linha horizontal
    private void drawWinningLineForRow(int row) {
        int color = playerXTurn ? colorX : colorO;

        if (lastMoveCol == 1) {
            float[] start = getButtonCenterRelative(row, 0);
            float[] end = getButtonCenterRelative(row, 2);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else if (lastMoveCol == 0) {
            float[] start = getButtonCenterRelative(row, 2);
            float[] end = getButtonCenterRelative(row, 0);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else {
            float[] start = getButtonCenterRelative(row, 0);
            float[] end = getButtonCenterRelative(row, 2);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        }
    }

    // Desenha linha de vitoria na coluna vertical
    private void drawWinningLineForColumn(int col) {
        int color = playerXTurn ? colorX : colorO;

        if (lastMoveRow == 1) {
            float[] start = getButtonCenterRelative(0, col);
            float[] end = getButtonCenterRelative(2, col);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else if (lastMoveRow == 0) {
            float[] start = getButtonCenterRelative(2, col);
            float[] end = getButtonCenterRelative(0, col);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else {
            float[] start = getButtonCenterRelative(0, col);
            float[] end = getButtonCenterRelative(2, col);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        }
    }

    // Desenha linha de vitoria na diagonal principal
    private void drawWinningLineForDiagonalMain() {
        int color = playerXTurn ? colorX : colorO;

        if (lastMoveRow == 1 && lastMoveCol == 1) {
            float[] start = getButtonCenterRelative(0, 0);
            float[] end = getButtonCenterRelative(2, 2);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else if (lastMoveRow == 0 && lastMoveCol == 0) {
            float[] start = getButtonCenterRelative(2, 2);
            float[] end = getButtonCenterRelative(0, 0);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else {
            float[] start = getButtonCenterRelative(0, 0);
            float[] end = getButtonCenterRelative(2, 2);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        }
    }

    // Desenha linha de vitoria na diagonal secundaria
    private void drawWinningLineForDiagonalSecondary() {
        int color = playerXTurn ? colorX : colorO;

        if (lastMoveRow == 1 && lastMoveCol == 1) {
            float[] start = getButtonCenterRelative(0, 2);
            float[] end = getButtonCenterRelative(2, 0);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else if (lastMoveRow == 0 && lastMoveCol == 2) {
            float[] start = getButtonCenterRelative(2, 0);
            float[] end = getButtonCenterRelative(0, 2);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        } else {
            float[] start = getButtonCenterRelative(0, 2);
            float[] end = getButtonCenterRelative(2, 0);
            winningLine.drawWinningLine(start[0], start[1], end[0], end[1], color);
        }
    }

    // Retorna coordenadas do centro de um botao
    private float[] getButtonCenterRelative(int row, int col) {
        Button button = buttons[row][col];

        int[] boardLocation = new int[2];
        board.getLocationInWindow(boardLocation);

        int[] buttonLocation = new int[2];
        button.getLocationInWindow(buttonLocation);

        float centerX = buttonLocation[0] + button.getWidth() / 2.0f;
        float centerY = buttonLocation[1] + button.getHeight() / 2.0f;

        return new float[]{centerX, centerY};
    }

    private void playerXWins() {
        gameEnded = true;
        playerXScore++;
        String message = gameMode.equals("BOT") ? "Você Venceu! 🎉" : "Jogador X Venceu!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mainLayout.setBackgroundColor(colorX);
        updateScore();
    }

    private void playerOWins() {
        gameEnded = true;
        playerOScore++;
        String message = gameMode.equals("BOT") ? "Bot Venceu! 🤖" : "Jogador O Venceu!";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mainLayout.setBackgroundColor(colorO);
        updateScore();
    }

    private void draw() {
        gameEnded = true;
        drawScore++;
        Toast.makeText(this, "Empate!", Toast.LENGTH_SHORT).show();
        mainLayout.setBackgroundColor(Color.parseColor("#9B59B6"));
        drawVForDraw();
        updateScore();
    }

    // Desenha V roxo no caso de empate
    private void drawVForDraw() {
        int vColor = Color.parseColor("#9B59B6");
        float[] topLeft = getButtonCenterRelative(0, 0);
        float[] middle = getButtonCenterRelative(2, 1);
        float[] topRight = getButtonCenterRelative(0, 2);
        winningLine.drawVShape(topLeft[0], topLeft[1], middle[0], middle[1], topRight[0], topRight[1], vColor);
    }

    private void updateScore() {
        txtScore.setText("X: " + playerXScore + " | O: " + playerOScore + " | Empates: " + drawScore);
    }

    private void updateCurrentPlayerText() {
        if (gameMode.equals("PLAYER")) {
            txtCurrentPlayer.setText("Vez do Jogador " + (playerXTurn ? "X" : "O"));
        } else {
            txtCurrentPlayer.setText(playerXTurn ? "Sua Vez (X)" : "Vez do Bot (O)");
        }
    }

    // Limpa tabuleiro para nova rodada
    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }

        roundCount = 0;
        gameEnded = false;
        lastMoveRow = -1;
        lastMoveCol = -1;

        xStartsThisRound = !xStartsThisRound;
        playerXTurn = xStartsThisRound;

        winningLine.clear();
        updateBackgroundColor();
        updateCurrentPlayerText();

        if (gameMode.equals("BOT") && !playerXTurn) {
            board.post(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!gameEnded && roundCount == 0) {
                                botMove();
                            }
                        }
                    }, 300);
                }
            });
        }
    }

    // Reseta jogo completamente incluindo placar
    private void resetGame() {
        playerXScore = 0;
        playerOScore = 0;
        drawScore = 0;
        updateScore();
        xStartsThisRound = true;
        resetBoard();
    }

    private void configurarBotoes() {
        configurarBotaoComAnimacao(btnReset, new Runnable() {
            @Override
            public void run() {
                resetBoard();
            }
        });

        configurarBotaoComAnimacao(btnExit, new Runnable() {
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

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreen();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configurarFullscreen();
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