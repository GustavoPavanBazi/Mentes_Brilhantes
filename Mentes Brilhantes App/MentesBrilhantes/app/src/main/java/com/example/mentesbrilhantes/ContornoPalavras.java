package com.example.mentesbrilhantes;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class ContornoPalavras extends AppCompatTextView {

    private boolean isDrawing = false;
    private int outlineColor = 0xFF000000; // Preto por padrão
    private float outlineWidth = 8f; // Largura do contorno

    public ContornoPalavras(Context context) {
        super(context);
        init(null);
    }

    public ContornoPalavras(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ContornoPalavras(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ContornoPalavras);
            outlineColor = a.getColor(R.styleable.ContornoPalavras_outlineColor, outlineColor);
            outlineWidth = a.getDimension(R.styleable.ContornoPalavras_outlineWidth, outlineWidth);
            a.recycle();
        }
    }

    public void setOutlineColor(int color) {
        outlineColor = color;
        invalidate();
    }

    public void setOutlineWidth(float width) {
        outlineWidth = width;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isDrawing) {
            super.onDraw(canvas);
            return;
        }

        Paint paint = getPaint();

        // Salvar cor original
        int originalColor = getCurrentTextColor();

        // Desenhar contorno preto
        isDrawing = true;
        setTextColor(outlineColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(outlineWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeMiter(10f);
        super.onDraw(canvas);

        // Desenhar preenchimento (cor original)
        setTextColor(originalColor);
        paint.setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
        isDrawing = false;
    }
}
