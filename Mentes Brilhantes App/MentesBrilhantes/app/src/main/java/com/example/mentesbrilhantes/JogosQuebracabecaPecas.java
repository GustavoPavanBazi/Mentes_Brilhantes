package com.example.mentesbrilhantes;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.appcompat.widget.AppCompatImageView;
import java.util.ArrayList;

// Representa uma peça do quebra-cabeça com suas propriedades e estado
public class JogosQuebracabecaPecas extends AppCompatImageView {

    public int xCoord;
    public int yCoord;
    public int pieceWidth;
    public int pieceHeight;
    public int row;
    public int col;
    public boolean canMove;
    public ArrayList<JogosQuebracabecaPecas> group;
    public Bitmap originalBitmap;

    public JogosQuebracabecaPecas(Context context) {
        super(context);
    }
}