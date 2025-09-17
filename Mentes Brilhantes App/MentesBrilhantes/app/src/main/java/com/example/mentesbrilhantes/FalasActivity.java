package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class FalasActivity extends AppCompatActivity {

    private ImageButton btnSocial, btnLazer, btnComidas, btnBanheiro;
    private ImageButton btnSair, btnAvancar, btnVoltar;
    private int paginaAtual = 1;
    private final int TOTAL_PAGINAS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falas);

        inicializarBotoes();
        configurarListeners();
        mostrarPagina(paginaAtual);
    }

    private void inicializarBotoes() {
        btnSocial = findViewById(R.id.btn_social);
        btnLazer = findViewById(R.id.btn_lazer);
        btnComidas = findViewById(R.id.btn_comidas);
        btnBanheiro = findViewById(R.id.btn_banheiro);

        btnSair = findViewById(R.id.btn_sair);
        btnAvancar = findViewById(R.id.btn_avancar);
        btnVoltar = findViewById(R.id.btn_voltar);
    }

    private void configurarAnimacaoPressionar(View botao, Runnable acao) {
        botao.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Diminuir quando pressiona
                    v.animate()
                            .scaleX(0.9f)
                            .scaleY(0.9f)
                            .setDuration(100)
                            .start();
                    return true;

                case MotionEvent.ACTION_UP:
                    // Voltar ao normal e executar ação
                    v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .withEndAction(() -> {
                                if (acao != null) {
                                    acao.run();
                                }
                            })
                            .start();
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    // Voltar ao normal se cancelar
                    v.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                    return true;
            }
            return false;
        });
    }

    private void configurarListeners() {
        // Botão sair
        configurarAnimacaoPressionar(btnSair, () -> finish());

        // Botão avançar
        configurarAnimacaoPressionar(btnAvancar, () -> {
            if (paginaAtual < TOTAL_PAGINAS) {
                paginaAtual++;
                mostrarPagina(paginaAtual);
            }
        });

        // Botão voltar
        configurarAnimacaoPressionar(btnVoltar, () -> {
            if (paginaAtual > 1) {
                paginaAtual--;
                mostrarPagina(paginaAtual);
            }
        });

        // Botões funcionais
        configurarAnimacaoPressionar(btnSocial, () -> {
            // Ação do botão social/emoções
        });

        configurarAnimacaoPressionar(btnLazer, () -> {
            // Ação do botão lazer/animais
        });

        configurarAnimacaoPressionar(btnComidas, () -> {
            // Ação do botão comidas/lugares
        });

        configurarAnimacaoPressionar(btnBanheiro, () -> {
            // Ação do botão banheiro/comunicação
        });
    }

    private void mostrarPagina(int pagina) {
        switch (pagina) {
            case 1:
                btnSocial.setImageResource(R.drawable.social);
                btnLazer.setImageResource(R.drawable.lazer);
                btnComidas.setImageResource(R.drawable.comidas);
                btnBanheiro.setImageResource(R.drawable.banheiro);

                btnAvancar.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.GONE);
                break;

            case 2:
                btnSocial.setImageResource(R.drawable.emocoes);
                btnLazer.setImageResource(R.drawable.animais);
                btnComidas.setImageResource(R.drawable.lugares);
                btnBanheiro.setImageResource(R.drawable.comunicacao);

                btnAvancar.setVisibility(View.GONE);
                btnVoltar.setVisibility(View.VISIBLE);
                break;
        }
    }
}
