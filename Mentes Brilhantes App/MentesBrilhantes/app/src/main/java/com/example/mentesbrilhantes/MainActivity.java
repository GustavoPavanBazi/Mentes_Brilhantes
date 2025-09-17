package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Configurar botões com animação de pressionar
        configurarBotoesComAnimacaoPressionar();
    }

    private void configurarAnimacaoPressionar(View botao, Runnable acao) {
        botao.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Animar para menor quando pressiona
                    v.animate()
                            .scaleX(0.9f)
                            .scaleY(0.9f)
                            .setDuration(100)
                            .start();
                    return true;

                case MotionEvent.ACTION_UP:
                    // Voltar ao normal quando solta E executar ação
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
                    // Voltar ao normal se cancelar (sair do botão)
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

    private void configurarBotoesComAnimacaoPressionar() {
        // Botão Falas
        configurarAnimacaoPressionar(findViewById(R.id.btn_falas), () -> {
            startActivity(new Intent(this, FalasActivity.class));
        });

        // Botão Jogos
        configurarAnimacaoPressionar(findViewById(R.id.btn_jogos), () -> {
            startActivity(new Intent(this, JogosActivity.class));
        });

        // Botão Vídeos
        configurarAnimacaoPressionar(findViewById(R.id.btn_videos), () -> {
            startActivity(new Intent(this, VideosActivity.class));
        });

        // Botão Perfil
        configurarAnimacaoPressionar(findViewById(R.id.btn_perfil), () -> {
            startActivity(new Intent(this, PerfilActivity.class));
        });
    }
}
