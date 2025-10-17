package com.example.mentesbrilhantes;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;

public class JogosJogodavelhaLinha extends View {

    private Paint linePaint;
    private Paint borderPaint;
    private ArrayList<Line> lines = new ArrayList<>();
    private boolean isVisible = false;
    private Path vPath = null;
    private PathMeasure vPathMeasure = null;
    private float vPathLength = 0;
    private float vProgress = 0;
    private int vColor;

    // Classe para representar uma linha
    private class Line {
        float startX, startY, endX, endY;
        float currentProgress = 0f;
        int color;

        Line(float startX, float startY, float endX, float endY, int color) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.color = color;
        }
    }

    public JogosJogodavelhaLinha(Context context) {
        super(context);
        init();
    }

    public JogosJogodavelhaLinha(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Inicializa os estilos de pintura
    private void init() {
        linePaint = new Paint();
        linePaint.setStrokeWidth(20);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(32);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setAntiAlias(true);
    }

    // Desenha linha de vitoria reta
    public void drawWinningLine(float startX, float startY, float endX, float endY, int color) {
        lines.clear();
        vPath = null;
        vPathMeasure = null;
        lines.add(new Line(startX, startY, endX, endY, color));
        this.isVisible = true;

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(600);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                for (Line line : lines) {
                    line.currentProgress = progress;
                }
                invalidate();
            }
        });
        animator.start();

        setVisibility(VISIBLE);
    }

    // Desenha linha em forma de V para vitoria diagonal
    public void drawVShape(float topLeftX, float topLeftY, float middleX, float middleY,
                           float topRightX, float topRightY, int color) {
        lines.clear();
        this.vColor = color;
        this.isVisible = true;

        vPath = new Path();
        vPath.moveTo(topLeftX, topLeftY);
        vPath.lineTo(middleX, middleY);
        vPath.lineTo(topRightX, topRightY);

        vPathMeasure = new PathMeasure(vPath, false);
        vPathLength = vPathMeasure.getLength();

        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                vProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();

        setVisibility(VISIBLE);
    }

    // Limpa todas as linhas
    public void clear() {
        isVisible = false;
        lines.clear();
        vPath = null;
        vPathMeasure = null;
        vProgress = 0;
        setVisibility(GONE);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isVisible) {
            if (vPath != null && vPathMeasure != null && vProgress > 0) {
                Path drawPath = new Path();
                vPathMeasure.getSegment(0, vPathLength * vProgress, drawPath, true);

                canvas.drawPath(drawPath, borderPaint);

                linePaint.setColor(vColor);
                canvas.drawPath(drawPath, linePaint);
            } else {
                for (Line line : lines) {
                    if (line.currentProgress > 0) {
                        float currentEndX = line.startX + (line.endX - line.startX) * line.currentProgress;
                        float currentEndY = line.startY + (line.endY - line.startY) * line.currentProgress;

                        canvas.drawLine(line.startX, line.startY, currentEndX, currentEndY, borderPaint);

                        linePaint.setColor(line.color);
                        canvas.drawLine(line.startX, line.startY, currentEndX, currentEndY, linePaint);
                    }
                }
            }
        }
    }
}