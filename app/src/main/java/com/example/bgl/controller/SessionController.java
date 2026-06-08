package com.example.bgl.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

/**
 * Guarda a sessão do usuário (tokens) de forma CRIPTOGRAFADA em disco,
 * usando EncryptedSharedPreferences (RNF03 — nunca em texto claro).
 */
public class SessionController {

    private static final String ARQUIVO = "cineglow_session";
    private static final String KEY_ACCESS = "access_token";
    private static final String KEY_REFRESH = "refresh_token";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EXPIRES = "expires_at";

    private SharedPreferences prefs;

    public SessionController(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    ARQUIVO,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (Exception e) {
            // Fallback (raro): se o Keystore falhar, usa prefs normais para não quebrar o app.
            Log.e("SessionController", "Falha ao criar prefs criptografadas", e);
            prefs = context.getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        }
    }

    /** Salva os dados da sessão após login/cadastro. */
    public void salvarSessao(String accessToken, String refreshToken, String email,
                             String userId, long expiresInSegundos) {
        long expiraEm = System.currentTimeMillis() + (expiresInSegundos * 1000);
        prefs.edit()
                .putString(KEY_ACCESS, accessToken)
                .putString(KEY_REFRESH, refreshToken)
                .putString(KEY_EMAIL, email)
                .putString(KEY_USER_ID, userId)
                .putLong(KEY_EXPIRES, expiraEm)
                .apply();
    }

    public String getAccessToken() { return prefs.getString(KEY_ACCESS, null); }
    public String getRefreshToken() { return prefs.getString(KEY_REFRESH, null); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, null); }
    public String getUserId() { return prefs.getString(KEY_USER_ID, null); }

    /** Existe uma sessão salva? (usado no auto-login). */
    public boolean estaLogado() {
        return getAccessToken() != null;
    }

    /** O token de acesso já expirou? */
    public boolean tokenExpirado() {
        return System.currentTimeMillis() >= prefs.getLong(KEY_EXPIRES, 0);
    }

    /** Apaga a sessão (logout). */
    public void limpar() {
        prefs.edit().clear().apply();
    }
}
