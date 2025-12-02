package com.example.mentesbrilhantes;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;

public class JogosMemoriaCartao extends ImageView {

    private int imagemId;
    private int versoId;
    private int posicao;
    private boolean virada = false;
    private boolean encontrada = false;

    public JogosMemoriaCartao(Context context, int imagemId, int versoId, int posicao) {
        super(context);
        this.imagemId = imagemId;
        this.versoId = versoId;
        this.posicao = posicao;
        inicializar();
    }

    private void inicializar() {
        // Configurar layout params - altura reduzida
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 0.7f); // Altura reduzida
        params.setMargins(4, 4, 4, 4);
        setLayoutParams(params);

        // Configurar ImageView - encaixa a imagem (que já tem bordas arredondadas)
        setScaleType(ScaleType.FIT_XY);
        setAdjustViewBounds(true);
        setImageResource(versoId);
    }

    public void mostrarPreview() {
        setImageResource(imagemId);
    }

    public void esconderPreview() {
        setImageResource(versoId);
    }

    public void virar() {
        if (!virada && !encontrada) {
            virada = true;
            animate()
                    .scaleX(0)
                    .setDuration(150)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            setImageResource(imagemId);
                            animate()
                                    .scaleX(1)
                                    .setDuration(150)
                                    .start();
                        }
                    })
                    .start();
        }
    }

    public void desvirar() {
        if (virada && !encontrada) {
            animate()
                    .scaleX(0)
                    .setDuration(150)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            setImageResource(versoId);
                            animate()
                                    .scaleX(1)
                                    .setDuration(150)
                                    .start();
                            virada = false;
                        }
                    })
                    .start();
        }
    }

    public void marcarComoEncontrada() {
        encontrada = true;
        // Apenas marca como encontrada, sem escurecer ou diminuir
        // A carta permanece virada normalmente
    }

    public boolean isVirada() {
        return virada;
    }

    public boolean isEncontrada() {
        return encontrada;
    }

    public int getImagemId() {
        return imagemId;
    }

    public int getPosicao() {
        return posicao;
    }
}
