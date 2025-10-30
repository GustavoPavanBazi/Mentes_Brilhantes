package com.example.mentesbrilhantes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private ImageButton saveButton;

    private static final String LOGIN_URL = "https://www.quaiati.com.br/mentes_brilhantes/php/login_neuro.php";
    private static boolean isButtonProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarFullscreen();
        setContentView(R.layout.activity_register);
        inicializarCampos();
        configurarBotaoProtegido();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreen();
        isButtonProcessing = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configurarFullscreen();
        }
    }

    private void inicializarCampos() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void configurarBotaoProtegido() {
        saveButton.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isButtonProcessing) {
                            isButtonProcessing = true;
                            isThisButtonActive = true;
                            v.animate()
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(80)
                                    .start();
                            return true;
                        } else {
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        if (isThisButtonActive) {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            realizarCadastro();
                                            isButtonProcessing = false;
                                            isThisButtonActive = false;
                                        }
                                    })
                                    .start();
                        } else {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(80)
                                .start();
                        if (isThisButtonActive) {
                            isButtonProcessing = false;
                            isThisButtonActive = false;
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void realizarCadastro() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Por favor, informe o email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Por favor, informe a senha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Email inválido. Verifique o formato", Toast.LENGTH_SHORT).show();
            return;
        }

        login(RegisterActivity.this, email, password, new LoginManager.LoginCallback() {
            @Override
            public void onSuccess(JSONObject user) {
                // Sucesso tratado dentro do método login
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static void openMainActivity(Context context, JSONObject user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("USER_NAME", user.optString("name", "Usuário"));
        intent.putExtra("USER_EMAIL", user.optString("email", ""));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).finish();
        }
    }

    public static void login(Context context, String email, String password, final LoginManager.LoginCallback callback) {
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                LOGIN_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.optString("status");
                        String message = obj.optString("message", "");

                        if ("success".equalsIgnoreCase(status)) {
                            JSONArray data = obj.optJSONArray("data");
                            JSONObject user = (data != null && data.length() > 0)
                                    ? data.getJSONObject(0)
                                    : new JSONObject();

                            String userName = user.optString("name", "Usuário");
                            String userEmail = user.optString("email", email);
                            String userGender = user.optString("gender", "masculino");

                            // Cria sessão de login persistente
                            SessionManager sessionManager = new SessionManager(context);
                            sessionManager.createLoginSession(userName, userEmail, userGender);

                            // Salvar credenciais para biometria
                            SharedPreferences sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", email);
                            editor.putString("password", password);
                            editor.apply();

                            Toast.makeText(context, message.isEmpty() ? "Login realizado com sucesso!" : message, Toast.LENGTH_SHORT).show();
                            if (callback != null) callback.onSuccess(user);
                            openMainActivity(context, user);
                        } else {
                            String errorMsg = message.isEmpty() ? "Erro ao fazer login" : message;
                            if (callback != null) {
                                callback.onError(errorMsg);
                            }
                        }
                    } catch (JSONException e) {
                        if (callback != null) {
                            callback.onError("Erro ao processar resposta do servidor");
                        }
                    }
                },
                error -> {
                    if (callback != null) {
                        String errorMsg = "Erro de conexão";
                        if (error.networkResponse != null) {
                            int code = error.networkResponse.statusCode;
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            try {
                                JSONObject errorJson = new JSONObject(body);
                                errorMsg = errorJson.optString("message", "Erro no servidor");
                            } catch (JSONException e) {
                                switch (code) {
                                    case 400:
                                        errorMsg = "Dados inválidos. Verifique os campos";
                                        break;
                                    case 401:
                                        errorMsg = "Email ou senha incorretos";
                                        break;
                                    case 404:
                                        errorMsg = "Servidor não encontrado";
                                        break;
                                    case 500:
                                    case 502:
                                    case 503:
                                        errorMsg = "Erro no servidor. Tente novamente mais tarde";
                                        break;
                                    default:
                                        errorMsg = "Erro HTTP " + code;
                                }
                            }
                        } else {
                            errorMsg = "Sem conexão com a internet. Verifique sua rede";
                        }
                        callback.onError(errorMsg);
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1f));
        VolleySingleton.getInstance(context).addToRequestQueue(postRequest);
    }

    private void configurarFullscreen() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}
