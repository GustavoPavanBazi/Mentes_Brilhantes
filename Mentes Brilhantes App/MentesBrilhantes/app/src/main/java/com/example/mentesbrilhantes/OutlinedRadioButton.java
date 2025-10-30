package com.example.mentesbrilhantes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatRadioButton;

public class OutlinedRadioButton extends AppCompatRadioButton {

    private int strokeColor = Color.BLACK;
    private int strokeWidth = 8; // Aumentado de 5 para 8

    public OutlinedRadioButton(Context context) {
        super(context);
    }

    public OutlinedRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlinedRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int originalColor = getCurrentTextColor();

        // Desenha o contorno preto
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(strokeWidth);
        setTextColor(strokeColor);
        super.onDraw(canvas);

        // Desenha o preenchimento branco
        getPaint().setStyle(Paint.Style.FILL);
        setTextColor(originalColor);
        super.onDraw(canvas);
    }
}
