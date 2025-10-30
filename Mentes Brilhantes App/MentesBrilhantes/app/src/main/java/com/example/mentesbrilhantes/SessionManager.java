package com.example.mentesbrilhantes;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_GENDER = "userGender";
    private static final String KEY_VOICE_PREFERENCE = "voicePreference";
    private static final String KEY_FIRST_TIME = "isFirstTime";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Cria sessão de login
    public void createLoginSession(String name, String email, String gender) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_GENDER, gender);
        editor.putBoolean(KEY_FIRST_TIME, false);
        editor.commit();
    }

    // Verifica se o usuário está logado
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Verifica se é a primeira vez que o app está sendo aberto
    public boolean isFirstTime() {
        return prefs.getBoolean(KEY_FIRST_TIME, true);
    }

    // Marca que não é mais primeira vez (usado no registro)
    public void setNotFirstTime() {
        editor.putBoolean(KEY_FIRST_TIME, false);
        editor.commit();
    }

    // Pega o nome do usuário
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Usuário");
    }

    // Pega o email do usuário
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    // Pega o sexo do usuário
    public String getUserGender() {
        return prefs.getString(KEY_USER_GENDER, "masculino");
    }

    // Salva a preferência de voz do usuário
    public void setVoicePreference(String voice) {
        editor.putString(KEY_VOICE_PREFERENCE, voice);
        editor.commit();
    }

    // Pega a preferência de voz (se não houver, usa o gender como padrão)
    public String getVoicePreference() {
        String voicePref = prefs.getString(KEY_VOICE_PREFERENCE, null);
        if (voicePref == null) {
            // Se não tem preferência salva, usa o gender do cadastro
            return getUserGender();
        }
        return voicePref;
    }

    // NOVO - Verifica se voz é feminina (retorna boolean)
    public boolean isVozFeminina() {
        String voice = getVoicePreference();
        return voice.equalsIgnoreCase("feminino");
    }

    // NOVO - Salva preferência de voz (recebe boolean)
    public void setVozFeminina(boolean isFeminino) {
        String voice = isFeminino ? "feminino" : "masculino";
        setVoicePreference(voice);
    }

    // Faz logout mas mantém que não é primeira vez
    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_GENDER);
        editor.remove(KEY_VOICE_PREFERENCE);
        // NÃO limpa KEY_FIRST_TIME para que não mostre registro novamente
        editor.commit();
    }
}
