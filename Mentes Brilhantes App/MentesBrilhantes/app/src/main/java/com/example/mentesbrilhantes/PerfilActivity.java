package com.example.mentesbrilhantes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        sessionManager = new SessionManager(this);

        // Inicializa os componentes
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnLogout = findViewById(R.id.btn_logout);

        // Exibe os dados do usuário
        tvUserName.setText(sessionManager.getUserName());
        tvUserEmail.setText(sessionManager.getUserEmail());

        // Configura o botão de logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sair da conta")
                .setMessage("Tem certeza que deseja sair?")
                .setPositiveButton("Sim", (dialog, which) -> logout())
                .setNegativeButton("Não", null)
                .show();
    }

    private void logout() {
        // Limpa a sessão
        sessionManager.logout();

        Toast.makeText(this, "Você saiu da conta", Toast.LENGTH_SHORT).show();

        // Volta para a tela de login
        Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
