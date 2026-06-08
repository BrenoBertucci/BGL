package com.example.bgl.controller;

import android.content.Context;

import com.example.bgl.model.AuthModels.AuthResponse;
import com.example.bgl.model.AuthModels.LoginRequest;
import com.example.bgl.model.AuthModels.SignUpRequest;
import com.example.bgl.network.ApiClient;
import com.example.bgl.network.SupabaseService;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controla TODA a autenticação do app (cadastro, login, logout).
 * As telas só conversam com esta classe — elas não conhecem o Supabase.
 */
public class AuthController {

    /** Callback simples para devolver o resultado à tela. */
    public interface AuthCallback {
        void onSucesso();
        void onErro(String mensagem);
    }

    private final SupabaseService api;
    private final SessionController session;

    public AuthController(Context context) {
        this.api = ApiClient.getSupabase();
        this.session = new SessionController(context.getApplicationContext());
    }

    public SessionController getSession() {
        return session;
    }

    // -------------------------------------------------------------
    // CADASTRO (UC01)
    // -------------------------------------------------------------
    public void cadastrar(String nome, String email, String senha, AuthCallback callback) {
        SignUpRequest body = new SignUpRequest(email, senha, nome);

        api.cadastrar(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    AuthResponse data = resp.body();
                    // Se o projeto exige confirmação de e-mail, não vem access_token aqui.
                    if (data.access_token != null) {
                        guardarSessao(data);
                    }
                    callback.onSucesso();
                } else {
                    callback.onErro(traduzirErro(resp, "Não foi possível cadastrar."));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onErro("Sem conexão. Tente novamente.");
            }
        });
    }

    // -------------------------------------------------------------
    // LOGIN (UC02)
    // -------------------------------------------------------------
    public void login(String email, String senha, AuthCallback callback) {
        LoginRequest body = new LoginRequest(email, senha);

        api.login("password", body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().access_token != null) {
                    guardarSessao(resp.body());
                    callback.onSucesso();
                } else {
                    callback.onErro(traduzirErro(resp, "E-mail ou senha incorretos."));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onErro("Sem conexão. Tente novamente.");
            }
        });
    }

    // -------------------------------------------------------------
    // LOGOUT (UC11)
    // -------------------------------------------------------------
    public void logout(AuthCallback callback) {
        String token = session.getAccessToken();
        // Limpa localmente primeiro — o usuário sai mesmo se a rede falhar.
        session.limpar();

        if (token == null) {
            callback.onSucesso();
            return;
        }
        api.logout("Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) { callback.onSucesso(); }
            @Override
            public void onFailure(Call<Void> call, Throwable t) { callback.onSucesso(); }
        });
    }

    /** Há uma sessão salva? (auto-login no Splash). */
    public boolean estaLogado() {
        return session.estaLogado();
    }

    // -------------------------------------------------------------
    // Auxiliares privados
    // -------------------------------------------------------------
    private void guardarSessao(AuthResponse data) {
        String email = (data.user != null) ? data.user.email : null;
        String userId = (data.user != null) ? data.user.id : null;
        session.salvarSessao(data.access_token, data.refresh_token, email, userId, data.expires_in);
    }

    /** Tenta extrair a mensagem de erro do corpo retornado pelo Supabase. */
    private String traduzirErro(Response<?> resp, String padrao) {
        try {
            if (resp.errorBody() != null) {
                String corpo = resp.errorBody().string();
                JSONObject json = new JSONObject(corpo);
                if (json.has("error_description")) return json.getString("error_description");
                if (json.has("msg")) return json.getString("msg");
                if (json.has("message")) return json.getString("message");
            }
        } catch (Exception ignored) { }
        return padrao;
    }
}
