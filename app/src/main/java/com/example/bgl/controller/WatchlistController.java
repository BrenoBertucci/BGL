package com.example.bgl.controller;

import android.content.Context;

import com.example.bgl.network.ApiClient;
import com.example.bgl.network.SupabaseDataService;

/**
 * Controller da "WATCHLIST" — Assistir Mais Tarde (UC07).
 *
 * ====================================================================
 *  EXERCÍCIO DA AULA — este controller está vazio de propósito.
 *  Use o FavoritosController como modelo: o código é IGUAL, só muda
 *  o endpoint chamado (api.listarWatchlist).
 * ====================================================================
 */
public class WatchlistController {

    private final SupabaseDataService api;
    private final SessionController session;

    public WatchlistController(Context context) {
        this.api = ApiClient.getSupabaseData();
        this.session = new SessionController(context.getApplicationContext());
    }

    /**
     * EXERCÍCIO — Buscar os títulos da Watchlist do usuário.
     *
     * Passo a passo (copie do FavoritosController):
     *   1) String bearer = "Bearer " + session.getAccessToken();
     *   2) api.listarWatchlist(bearer, "*").enqueue(...);
     *   3) no onResponse de sucesso: callback.onSucesso(resp.body());
     *   4) nos erros: callback.onErro("mensagem");
     */
    public void listar(ListaCallback callback) {
        // TODO (aula): implementar a busca da Watchlist.
    }
}
