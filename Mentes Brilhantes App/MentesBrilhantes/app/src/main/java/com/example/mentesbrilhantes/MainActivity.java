package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
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

        // Configurar listeners dos ImageButtons para navegação
        findViewById(R.id.btn_falas).setOnClickListener(v -> {
            startActivity(new Intent(this, FalasActivity.class));
        });

        findViewById(R.id.btn_jogos).setOnClickListener(v -> {
            startActivity(new Intent(this, JogosActivity.class));
        });

        findViewById(R.id.btn_videos).setOnClickListener(v -> {
            startActivity(new Intent(this, VideosActivity.class));
        });

        findViewById(R.id.btn_perfil).setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilActivity.class));
        });
    }
}