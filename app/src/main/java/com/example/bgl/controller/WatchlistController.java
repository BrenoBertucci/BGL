package com.example.bgl.controller;

import android.content.Context;
import java.util.List;
import com.example.bgl.network.ApiClient;
import com.example.bgl.network.SupabaseDataService;
import retrofit2.Call;
import retrofit2.Response;
import androidx.annotation.NonNull;
import com.example.bgl.model.ItemSalvo;
public class WatchlistController {

    private final SupabaseDataService api;
    private final SessionController session;

    public WatchlistController(Context context) {
        ApiClient.init(context);
        this.api = ApiClient.getSupabaseData();
        this.session = new SessionController(context.getApplicationContext());
    }

    
    public void listar(ListaCallback callback) {
        String bearer = "Bearer " + session.getAccessToken();
        api.listarWatchlist(bearer, "*").enqueue(new retrofit2.Callback<List<ItemSalvo>>() {
            @Override
            public void onResponse(@NonNull Call<List<ItemSalvo>> call, @NonNull Response<List<ItemSalvo>> resp) {
                if(!resp.isSuccessful() || resp.body() == null) {
                    callback.onErro("Não foi possível carregar os resultados.");
                    return;
                }
                callback.onSucesso(resp.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<ItemSalvo>> call, @NonNull Throwable t) {
                callback.onErro("Sem conexão. Tente novamente.");
            }
        });
    }

    public void adicionar(ItemSalvo item, ListaCallback callback){
        String bearer = "Bearer " + session.getAccessToken();

        item.userId = session.getUserId();

        api.inserirWatchlist(bearer, item).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> resp) {
                if (resp.isSuccessful()) {
                    callback.onSucesso(null);
                } else if (resp.code() == 409) {
                    callback.onErro("Já está na lista.");
                } else {
                    callback.onErro("Não foi possível adicionar o item.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onErro("Sem conexão. Tente novamente.");
            }
        });

    }

    /** Remove um título da Watchlist (UC09). */
    public void remover(ItemSalvo item, ListaCallback callback) {
        String bearer = "Bearer " + session.getAccessToken();

        api.removerWatchlist(bearer, "eq." + item.tmdbId, "eq." + item.tipo)
                .enqueue(new retrofit2.Callback<Void>() {
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
