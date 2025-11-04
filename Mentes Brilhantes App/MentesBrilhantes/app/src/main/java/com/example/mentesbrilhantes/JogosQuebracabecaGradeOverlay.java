package com.example.mentesbrilhantes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

// ImageView customizada que desenha uma grade sobre a imagem
public class JogosQuebracabecaGradeOverlay extends AppCompatImageView {

    private Paint gridPaint;
    private int rows = 5;
    private int cols = 3;

    public JogosQuebracabecaGradeOverlay(Context context) {
        super(context);
        init();
    }

    public JogosQuebracabecaGradeOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JogosQuebracabecaGradeOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(Color.argb(140, 80, 80, 80));
        gridPaint.setStrokeWidth(3);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Drawable drawable = getDrawable();
        if (drawable == null) return;

        int viewWidth = getWidth();
        int viewHeight = getHeight();

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

        float cellWidth = (float) scaledWidth / cols;
        float cellHeight = (float) scaledHeight / rows;

        // Desenha linhas verticais
        for (int i = 1; i < cols; i++) {
            float x = offsetX + i * cellWidth;
            canvas.drawLine(x, offsetY, x, offsetY + scaledHeight, gridPaint);
        }

        // Desenha linhas horizontais
        for (int i = 1; i < rows; i++) {
            float y = offsetY + i * cellHeight;
            canvas.drawLine(offsetX, y, offsetX + scaledWidth, y, gridPaint);
        }

        // Desenha borda externa da grade
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.argb(140, 80, 80, 80));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        borderPaint.setAntiAlias(true);
        canvas.drawRect(offsetX, offsetY, offsetX + scaledWidth, offsetY + scaledHeight, borderPaint);
    }
}