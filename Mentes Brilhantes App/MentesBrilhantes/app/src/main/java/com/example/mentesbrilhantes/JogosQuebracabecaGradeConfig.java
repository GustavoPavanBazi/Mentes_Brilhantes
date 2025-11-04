package com.example.mentesbrilhantes;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import java.util.ArrayList;

public class JogosQuebracabecaGradeConfig implements View.OnTouchListener {

    private float xDelta;
    private float yDelta;
    private JogosQuebracabecaGameplayActivity activity;

    public JogosQuebracabecaGradeConfig(JogosQuebracabecaGameplayActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        final int tolerance = 60;

        JogosQuebracabecaPecas piece = (JogosQuebracabecaPecas) view;
        if (!piece.canMove) return true;

        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                xDelta = x - lParams.leftMargin;
                yDelta = y - lParams.topMargin;

                // Traz o grupo todo pra frente quando pega a peça
                for (JogosQuebracabecaPecas groupPiece : piece.group) {
                    groupPiece.bringToFront();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // Calcula quanto a peça se moveu
                int dx = (int) (x - xDelta) - lParams.leftMargin;
                int dy = (int) (y - yDelta) - lParams.topMargin;

                // Move o grupo inteiro junto
                for (JogosQuebracabecaPecas groupPiece : piece.group) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) groupPiece.getLayoutParams();
                    params.leftMargin += dx;
                    params.topMargin += dy;
                    params.width = groupPiece.pieceWidth;
                    params.height = groupPiece.pieceHeight;
                    groupPiece.setLayoutParams(params);
                }

                xDelta = x - ((RelativeLayout.LayoutParams) piece.getLayoutParams()).leftMargin;
                yDelta = y - ((RelativeLayout.LayoutParams) piece.getLayoutParams()).topMargin;
                break;

            case MotionEvent.ACTION_UP:
                int xDiff = Math.abs(piece.xCoord - lParams.leftMargin);
                int yDiff = Math.abs(piece.yCoord - lParams.topMargin);

                // Se soltou perto da posição correta, encaixa
                if (xDiff <= tolerance && yDiff <= tolerance) {
                    for (JogosQuebracabecaPecas groupPiece : piece.group) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) groupPiece.getLayoutParams();
                        params.leftMargin = groupPiece.xCoord;
                        params.topMargin = groupPiece.yCoord;
                        params.width = groupPiece.pieceWidth;
                        params.height = groupPiece.pieceHeight;
                        groupPiece.setLayoutParams(params);
                        groupPiece.canMove = false;
                    }

                    checkAndConnectAdjacentPieces(piece);

                    // Manda o grupo pro fundo depois de encaixar
                    for (JogosQuebracabecaPecas groupPiece : piece.group) {
                        sendViewToBack(groupPiece);
                    }

                    activity.checkGameOver();
                }
                break;
        }
        return true;
    }

    // Verifica se tem peças adjacentes ja encaixadas pra juntar em grupo
    private void checkAndConnectAdjacentPieces(JogosQuebracabecaPecas piece) {
        int[][] directions = {{-1,0}, {1,0}, {0,-1}, {0,1}};

        for (int[] dir : directions) {
            int adjRow = piece.row + dir[0];
            int adjCol = piece.col + dir[1];

            for (JogosQuebracabecaPecas other : activity.pieces) {
                if (other.row == adjRow && other.col == adjCol && !other.canMove) {
                    mergeGroups(piece, other);
                }
            }
        }
    }

    // Junta dois grupos de peças em um só
    private void mergeGroups(JogosQuebracabecaPecas p1, JogosQuebracabecaPecas p2) {
        ArrayList<JogosQuebracabecaPecas> newGroup = new ArrayList<>(p1.group);
        for (JogosQuebracabecaPecas p : p2.group) {
            if (!newGroup.contains(p)) newGroup.add(p);
        }

        for (JogosQuebracabecaPecas p : newGroup) {
            p.group = newGroup;
        }
    }

    // Manda a view pro fundo da pilha de views
    private void sendViewToBack(View child) {
        ViewGroup parent = (ViewGroup) child.getParent();
        if (parent != null) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }
}