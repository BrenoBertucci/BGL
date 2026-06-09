package com.example.bgl.network;

import androidx.annotation.NonNull;

import com.example.bgl.controller.SessionController;
import com.example.bgl.model.AuthModels.AuthResponse;
import com.example.bgl.model.AuthModels.RefreshRequest;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Renova o token automaticamente quando o Supabase responde 401
 * (token expirado). Pega um novo access_token usando o refresh_token,
 * salva na sessão e repete a requisição original. O usuário nem percebe.
 */
public class TokenAuthenticator implements Authenticator {

    private final SessionController session;

    public TokenAuthenticator(SessionController session) {
        this.session = session;
    }

    @Override
    public Request authenticate(Route route, @NonNull Response response) throws IOException {
        // Se já tentamos renovar nesta requisição, desistimos (evita loop infinito).
        if (response.request().header("X-Tentou-Renovar") != null) {
            return null;
        }

        String refreshToken = session.getRefreshToken();
        if (refreshToken == null) {
            return null;
        }

        // Chama o refresh de forma SÍNCRONA (estamos numa thread de rede).
        retrofit2.Response<AuthResponse> r = ApiClient.getSupabase()
                .renovar("refresh_token", new RefreshRequest(refreshToken))
                .execute();

        if (!r.isSuccessful() || r.body() == null || r.body().access_token == null) {
            // Refresh falhou: limpa a sessão e desiste.
            session.limpar();
            return null;
        }

        // Salva o novo token e repete a requisição com ele.
        AuthResponse data = r.body();
        String email = (data.user != null) ? data.user.email : session.getEmail();
        String userId = (data.user != null) ? data.user.id : session.getUserId();
        session.salvarSessao(data.access_token, data.refresh_token, email, userId, data.expires_in);

        return response.request().newBuilder()
                .header("Authorization", "Bearer " + data.access_token)
                .header("X-Tentou-Renovar", "1")
                .build();
    }
}
