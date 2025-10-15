package com.example.mentesbrilhantes;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;
import java.util.Random;

// View que exibe animacao de confetes na tela de vitoria
public class JogosQuebracabecaConfetes extends View {

    private ArrayList<Confetti> confettiList = new ArrayList<>();
    private Paint paint;
    private TextPaint textPaint;
    private Random random;
    private ValueAnimator animator;

    public JogosQuebracabecaConfetes(Context context) {
        super(context);
        init();
    }

    public JogosQuebracabecaConfetes(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);

        random = new Random();
    }

    // Inicia animacao de confetes vindo dos lados da tela
    public void startConfetti() {
        setVisibility(VISIBLE);
        confettiList.clear();

        for (int i = 0; i < 70; i++) {
            confettiList.add(new Confetti(false, true));
        }

        for (int i = 0; i < 30; i++) {
            confettiList.add(new Confetti(true, true));
        }

        for (int i = 0; i < 70; i++) {
            confettiList.add(new Confetti(false, false));
        }

        for (int i = 0; i < 30; i++) {
            confettiList.add(new Confetti(true, false));
        }

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                for (Confetti confetti : confettiList) {
                    confetti.update(progress);
                }
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Confetti confetti : confettiList) {
            if (confetti.isEmoji) {
                textPaint.setColor(Color.WHITE);
                textPaint.setAlpha((int)(255 * confetti.alpha));
                canvas.save();
                canvas.translate(confetti.x, confetti.y);
                canvas.rotate(confetti.rotation);
                canvas.drawText(confetti.emoji, 0, 20, textPaint);
                canvas.restore();
            } else {
                paint.setColor(confetti.color);
                paint.setAlpha((int)(255 * confetti.alpha));
                canvas.save();
                canvas.translate(confetti.x, confetti.y);
                canvas.rotate(confetti.rotation);
                canvas.drawRect(-confetti.size / 2, -confetti.size / 2,
                        confetti.size / 2, confetti.size / 2, paint);
                canvas.restore();
            }
        }
    }

    // Classe interna que representa um confete individual
    private class Confetti {
        float x, y;
        float startX, startY;
        float targetX, targetY;
        float rotation;
        float rotationSpeed;
        int color;
        int size;
        boolean fromLeft;
        boolean isEmoji;
        String emoji;
        float alpha;
        float delay;

        Confetti(boolean isEmoji, boolean fromLeft) {
            this.isEmoji = isEmoji;
            this.fromLeft = fromLeft;

            int screenWidth = getWidth() == 0 ? 1080 : getWidth();
            int screenHeight = getHeight() == 0 ? 1920 : getHeight();

            startY = random.nextInt(screenHeight);

            if (fromLeft) {
                startX = -150;
                targetX = screenWidth * 0.5f + random.nextInt((int)(screenWidth * 0.6f));
            } else {
                startX = screenWidth + 150;
                targetX = -100 + random.nextInt((int)(screenWidth * 0.6f));
            }

            targetY = startY + (random.nextFloat() * 400 - 200);

            x = startX;
            y = startY;

            rotation = random.nextFloat() * 360;
            rotationSpeed = random.nextFloat() * 25 - 12.5f;
            alpha = 1.0f;
            delay = random.nextFloat() * 0.3f;

            if (isEmoji) {
                String[] emojis = {
                        "🎉", "🎊", "🎈", "🎆", "✨",
                        "⭐", "🌟", "💫", "🎁", "🏆",
                        "🎀", "🎯", "🔥", "💝", "🌈"
                };
                emoji = emojis[random.nextInt(emojis.length)];
                size = random.nextInt(35) + 55;
            } else {
                size = random.nextInt(25) + 12;

                int[] colors = {
                        Color.rgb(255, 87, 51),
                        Color.rgb(255, 195, 0),
                        Color.rgb(76, 175, 80),
                        Color.rgb(33, 150, 243),
                        Color.rgb(156, 39, 176),
                        Color.rgb(255, 152, 0),
                        Color.rgb(233, 30, 99),
                        Color.rgb(0, 188, 212),
                        Color.rgb(255, 235, 59),
                        Color.rgb(244, 67, 54),
                        Color.rgb(103, 58, 183),
                        Color.rgb(0, 150, 136)
                };
                color = colors[random.nextInt(colors.length)];
            }
        }

        // Atualiza posicao e rotacao do confete baseado no progresso da animacao
        void update(float progress) {
            float adjustedProgress = Math.max(0, (progress - delay) / (1 - delay));

            if (adjustedProgress <= 0) return;

            x = startX + (targetX - startX) * adjustedProgress;
            y = startY + (targetY - startY) * adjustedProgress;

            float wave = (float) Math.sin(adjustedProgress * 10) * 30;
            y += wave;

            rotation += rotationSpeed;

            if (adjustedProgress < 0.1f) {
                alpha = adjustedProgress / 0.1f;
            } else if (adjustedProgress > 0.8f) {
                alpha = 1.0f - ((adjustedProgress - 0.8f) / 0.2f);
            } else {
                alpha = 1.0f;
            }
        }
    }
}