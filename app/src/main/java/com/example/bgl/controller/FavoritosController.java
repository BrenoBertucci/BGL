package com.example.bgl.controller;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.bgl.model.ItemSalvo;
import com.example.bgl.network.ApiClient;
import com.example.bgl.network.SupabaseDataService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controller da lista de Favoritos (UC05 / UC08).
 */
public class FavoritosController {

    private final SupabaseDataService api;
    private final SessionController session;

    public FavoritosController(Context context) {
        ApiClient.init(context);
        this.api = ApiClient.getSupabaseData();
        this.session = new SessionController(context.getApplicationContext());
    }

    /**
     * Busca no Supabase todos os favoritos do usuário logado (UC08).
     */
    public void listar(ListaCallback callback) {
        String bearer = "Bearer " + session.getAccessToken();

        api.listarFavoritos(bearer, "*").enqueue(new Callback<List<ItemSalvo>>() {
            @Override
            public void onResponse(@NonNull Call<List<ItemSalvo>> call,
                    @NonNull Response<List<ItemSalvo>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    callback.onSucesso(resp.body());
                } else {
                    callback.onErro("Não foi possível carregar os favoritos.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ItemSalvo>> call, @NonNull Throwable t) {
                callback.onErro("Sem conexão. Tente novamente.");
            }
        });
    }

    /**
     * Adiciona um título aos favoritos (UC05).
     */
    public void adicionar(ItemSalvo item, ListaCallback callback) {
        String bearer = "Bearer " + session.getAccessToken();

        // Preenche o dono do registro (necessário para o RLS aceitar o insert).
        item.userId = session.getUserId();

        api.inserirFavorito(bearer, item).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> resp) {
                if (resp.isSuccessful()) {
                    callback.onSucesso(null);
                    return;
                }

                // Trata os erros de forma enxuta e organizada
                switch (resp.code()) {
                    case 409:
                        callback.onErro("Já está na lista.");
                        break;
                    case 401:
                        callback.onErro("Sessão expirada. Faça login novamente.");
                        break;
                    case 422:
                        callback.onErro("Item inválido. Verifique os dados.");
                        break;
                    case 429:
                        callback.onErro("Muitas requisições. Tente mais tarde.");
                        break;
                    default:
                        callback.onErro("Não foi possível adicionar o item.");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onErro("Sem conexão. Tente novamente.");
            }
        });
    }

    /**
     * Remove um título dos favoritos (UC09).
     */
    public void remover(ItemSalvo item, ListaCallback callback) {
        String bearer = "Bearer " + session.getAccessToken();

        // PostgREST usa o formato coluna=eq.valor para filtrar.
        api.removerFavorito(bearer, "eq." + item.tmdbId, "eq." + item.tipo)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> resp) {
                        if (resp.isSuccessful()) {
                            callback.onSucesso(null);
                        } else {
                            callback.onErro("Não foi possível remover.");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        callback.onErro("Sem conexão. Tente novamente.");
                    }
                });
    }
}
