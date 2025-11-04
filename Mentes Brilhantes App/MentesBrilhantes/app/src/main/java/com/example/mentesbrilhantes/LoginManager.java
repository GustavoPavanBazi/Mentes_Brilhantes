package com.example.mentesbrilhantes;

import android.content.Context;
import android.content.Intent;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginManager {
    private static final String LOGIN_URL = "https://www.quaiati.com.br/mentes_brilhantes/php/login_neuro.php";

    public interface LoginCallback {
        void onSuccess(JSONObject user);
        void onError(String errorMessage);
    }

    public static void login(Context context, String email, String password, final LoginCallback callback) {
        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                LOGIN_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if ("success".equalsIgnoreCase(obj.optString("status"))) {
                            JSONArray data = obj.optJSONArray("data");
                            JSONObject user = (data != null && data.length() > 0) ? data.getJSONObject(0) : new JSONObject();
                            if (callback != null) callback.onSuccess(user);
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("USER_NAME", user.optString("name", "Usuário"));
                            intent.putExtra("USER_EMAIL", user.optString("email", email));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            String msg = obj.optString("message", "Usuário ou senha inválidos.");
                            if (callback != null) callback.onError(msg);
                        }
                    } catch (JSONException e) {
                        if (callback != null) callback.onError("Erro ao processar resposta: " + e.getMessage());
                    }
                },
                error -> {
                    if (callback != null) {
                        if (error.networkResponse != null) {
                            int code = error.networkResponse.statusCode;
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            callback.onError("HTTP " + code + ": " + body);
                        } else {
                            callback.onError("Falha de rede: " + error.toString());
                        }
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
}
