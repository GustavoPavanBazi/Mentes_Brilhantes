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
    private static final String KEY_RESPONSAVEL = "responsavel";
    private static final String KEY_CELULAR_RESPONSAVEL = "celularResponsavel";
    private static final String KEY_BIRTHDATE = "birthdate";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ATUALIZADO: aceita 6 parâmetros agora
    public void createLoginSession(String name, String email, String gender, String responsavel, String celularResponsavel, String birthdate) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_GENDER, gender);
        editor.putString(KEY_RESPONSAVEL, responsavel);
        editor.putString(KEY_CELULAR_RESPONSAVEL, celularResponsavel);
        editor.putString(KEY_BIRTHDATE, birthdate);
        editor.putBoolean(KEY_FIRST_TIME, false);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isFirstTime() {
        return prefs.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setNotFirstTime() {
        editor.putBoolean(KEY_FIRST_TIME, false);
        editor.commit();
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Usuário");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserGender() {
        return prefs.getString(KEY_USER_GENDER, "masculino");
    }

    public String getResponsavel() {
        return prefs.getString(KEY_RESPONSAVEL, "Não informado");
    }

    public String getCelularResponsavel() {
        return prefs.getString(KEY_CELULAR_RESPONSAVEL, "Não informado");
    }

    public String getBirthdate() {
        return prefs.getString(KEY_BIRTHDATE, "Não informado");
    }

    public void setVoicePreference(String voice) {
        editor.putString(KEY_VOICE_PREFERENCE, voice);
        editor.commit();
    }

    public String getVoicePreference() {
        String voicePref = prefs.getString(KEY_VOICE_PREFERENCE, null);
        if (voicePref == null) {
            return getUserGender();
        }
        return voicePref;
    }

    public boolean isVozFeminina() {
        String voice = getVoicePreference();
        return voice.equalsIgnoreCase("feminino");
    }

    public void setVozFeminina(boolean isFeminino) {
        String voice = isFeminino ? "feminino" : "masculino";
        setVoicePreference(voice);
    }

    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_GENDER);
        editor.remove(KEY_VOICE_PREFERENCE);
        editor.remove(KEY_RESPONSAVEL);
        editor.remove(KEY_CELULAR_RESPONSAVEL);
        editor.remove(KEY_BIRTHDATE);
        editor.commit();
    }
}
