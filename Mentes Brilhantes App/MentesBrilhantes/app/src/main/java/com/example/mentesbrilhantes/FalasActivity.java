package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class FalasActivity extends AppCompatActivity {

    private ImageButton btnSocial, btnLazer, btnComidas, btnBanheiro;
    private ImageButton btnEmocoes, btnAnimais, btnLugares, btnComunicacao;
    private ImageButton btnHome, btnAvancar, btnVoltar;
    private int paginaAtual = 1;
    private final int TOTAL_PAGINAS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falas);

        // Inicializar botões
        inicializarBotoes();

        // Configurar listeners
        configurarListeners();

        // Mostrar primeira página
        mostrarPagina(paginaAtual);
    }

    private void inicializarBotoes() {
        // Botões funcionais - Página 1
        btnSocial = findViewById(R.id.btn_social);
        btnLazer = findViewById(R.id.btn_lazer);
        btnComidas = findViewById(R.id.btn_comidas);
        btnBanheiro = findViewById(R.id.btn_banheiro);

        // Botões de navegação
        btnHome = findViewById(R.id.btn_home);
        btnAvancar = findViewById(R.id.btn_avancar);
        btnVoltar = findViewById(R.id.btn_voltar);
    }

    private void configurarListeners() {
        // Botão voltar para tela principal
        btnHome.setOnClickListener(v -> {
            finish(); // Volta para MainActivity
        });

        // Botão avançar página
        btnAvancar.setOnClickListener(v -> {
            if (paginaAtual < TOTAL_PAGINAS) {
                paginaAtual++;
                mostrarPagina(paginaAtual);
            }
        });

        // Botão voltar página
        btnVoltar.setOnClickListener(v -> {
            if (paginaAtual > 1) {
                paginaAtual--;
                mostrarPagina(paginaAtual);
            }
        });

        // Listeners dos botões funcionais - Página 1
        btnSocial.setOnClickListener(v -> {
            if (paginaAtual == 1) {
                // Ações sociais: "Olá", "Tchau", "Por favor", etc.
            } else {
                // Emoções: "Feliz", "Triste", "Com raiva", etc.
            }
        });

        btnLazer.setOnClickListener(v -> {
            if (paginaAtual == 1) {
                // Atividades de lazer: "Brincar", "Assistir TV", "Música", etc.
            } else {
                // Animais: "Cachorro", "Gato", "Pássaro", etc.
            }
        });

        btnComidas.setOnClickListener(v -> {
            if (paginaAtual == 1) {
                // Comidas: "Água", "Lanche", "Fruta", etc.
            } else {
                // Lugares: "Casa", "Escola", "Parque", etc.
            }
        });

        btnBanheiro.setOnClickListener(v -> {
            if (paginaAtual == 1) {
                // Necessidades: "Banheiro", "Sede", "Fome", etc.
            } else {
                // Comunicação: "Sim", "Não", "Mais", "Pare", etc.
            }
        });
    }

    private void mostrarPagina(int pagina) {
        switch (pagina) {
            case 1:
                // Página 1: Social, Lazer, Comidas, Banheiro
                btnSocial.setImageResource(R.drawable.social);
                btnLazer.setImageResource(R.drawable.lazer);
                btnComidas.setImageResource(R.drawable.comidas);
                btnBanheiro.setImageResource(R.drawable.banheiro);

                // Controles de navegação
                btnAvancar.setVisibility(View.VISIBLE);
                btnVoltar.setVisibility(View.GONE);
                break;

            case 2:
                // Página 2: Emoções, Animais, Lugares, Comunicação
                btnSocial.setImageResource(R.drawable.emocoes);
                btnLazer.setImageResource(R.drawable.animais);
                btnComidas.setImageResource(R.drawable.lugares);
                btnBanheiro.setImageResource(R.drawable.comunicacao);

                // Controles de navegação
                btnAvancar.setVisibility(View.GONE);
                btnVoltar.setVisibility(View.VISIBLE);
                break;
        }
    }
}
