package com.example.mentesbrilhantes;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JogosMemoriaConfetes {

    private Context context;
    private ViewGroup container;
    private List<Confete> confetes = new ArrayList<>();
    private Random random = new Random();

    public JogosMemoriaConfetes(Context context, ViewGroup container) {
        this.context = context;
        this.container = container;
    }

    public void iniciar() {
        ConfeteView confeteView = new ConfeteView(context);
        container.addView(confeteView);

        // Criar confetes
        for (int i = 0; i < 100; i++) {
            confetes.add(new Confete(
                    random.nextInt(container.getWidth()),
                    -random.nextInt(500),
                    obterCorAleatoria()
            ));
        }

        // Animar confetes
        ValueAnimator animator = ValueAnimator.ofFloat(0, container.getHeight() + 500);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                for (Confete confete : confetes) {
                    confete.y = confete.startY + value;
                    confete.rotation += 5;
                }
                confeteView.invalidate();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                container.removeView(confeteView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animator.start();
    }

    private int obterCorAleatoria() {
        int[] cores = {
                Color.parseColor("#FF6B6B"),
                Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"),
                Color.parseColor("#FFA07A"),
                Color.parseColor("#98D8C8"),
                Color.parseColor("#F7DC6F"),
                Color.parseColor("#BB8FCE"),
                Color.parseColor("#85C1E2")
        };
        return cores[random.nextInt(cores.length)];
    }

    private class ConfeteView extends View {
        private Paint paint = new Paint();

        public ConfeteView(Context context) {
            super(context);
            setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (Confete confete : confetes) {
                paint.setColor(confete.cor);
                canvas.save();
                canvas.translate(confete.x, confete.y);
                canvas.rotate(confete.rotation);
                canvas.drawRect(-10, -10, 10, 10, paint);
                canvas.restore();
            }
        }
    }

    private class Confete {
        float x, y, startY, rotation;
        int cor;

        Confete(float x, float startY, int cor) {
            this.x = x;
            this.y = startY;
            this.startY = startY;
            this.cor = cor;
            this.rotation = random.nextInt(360);
        }
    }
}
