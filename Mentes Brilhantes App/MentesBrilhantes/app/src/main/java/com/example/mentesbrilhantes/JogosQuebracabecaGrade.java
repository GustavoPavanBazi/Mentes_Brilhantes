package com.example.mentesbrilhantes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.HashSet;
import java.util.Set;

// View customizada que desenha grade sobre o quebra-cabeca
public class JogosQuebracabecaGrade extends View {

    private Paint gridPaint;
    private int rows = 5;
    private int cols = 3;
    private int imageLeft, imageTop, imageWidth, imageHeight;
    private Set<String> hiddenLines = new HashSet<>();

    public JogosQuebracabecaGrade(Context context) {
        super(context);
        init();
    }

    public JogosQuebracabecaGrade(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(Color.argb(140, 80, 80, 80));
        gridPaint.setStrokeWidth(3);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
    }

    public void setGridSize(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        hiddenLines.clear();
        invalidate();
    }

    public void setImageBounds(int left, int top, int width, int height) {
        this.imageLeft = left;
        this.imageTop = top;
        this.imageWidth = width;
        this.imageHeight = height;
        invalidate();
    }

    // Esconde linha entre duas pecas adjacentes quando elas se conectam
    public void hideLineBetweenPieces(int row1, int col1, int row2, int col2) {
        if (row1 == row2 && Math.abs(col1 - col2) == 1) {
            int col = Math.max(col1, col2);
            String lineKey = "V_" + row1 + "_" + col;
            hiddenLines.add(lineKey);
        } else if (col1 == col2 && Math.abs(row1 - row2) == 1) {
            int row = Math.max(row1, row2);
            String lineKey = "H_" + row + "_" + col1;
            hiddenLines.add(lineKey);
        }

        invalidate();
    }

    public void clearHiddenLines() {
        hiddenLines.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (imageWidth == 0 || imageHeight == 0) return;

        float cellWidth = (float) imageWidth / cols;
        float cellHeight = (float) imageHeight / rows;

        // Desenha linhas verticais
        for (int col = 1; col < cols; col++) {
            float x = imageLeft + col * cellWidth;

            for (int row = 0; row < rows; row++) {
                String lineKey = "V_" + row + "_" + col;

                if (!hiddenLines.contains(lineKey)) {
                    float y1 = imageTop + row * cellHeight;
                    float y2 = imageTop + (row + 1) * cellHeight;
                    canvas.drawLine(x, y1, x, y2, gridPaint);
                }
            }
        }

        // Desenha linhas horizontais
        for (int row = 1; row < rows; row++) {
            float y = imageTop + row * cellHeight;

            for (int col = 0; col < cols; col++) {
                String lineKey = "H_" + row + "_" + col;

                if (!hiddenLines.contains(lineKey)) {
                    float x1 = imageLeft + col * cellWidth;
                    float x2 = imageLeft + (col + 1) * cellWidth;
                    canvas.drawLine(x1, y, x2, y, gridPaint);
                }
            }
        }

        // Desenha borda externa
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.argb(140, 80, 80, 80));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        borderPaint.setAntiAlias(true);
        canvas.drawRect(imageLeft, imageTop, imageLeft + imageWidth, imageTop + imageHeight, borderPaint);
    }
}