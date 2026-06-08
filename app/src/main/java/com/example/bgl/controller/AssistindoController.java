package com.example.bgl.controller;

import android.content.Context;

import com.example.bgl.network.ApiClient;
import com.example.bgl.network.SupabaseDataService;

/**
 * Controller da lista "ASSISTINDO" (UC06).
 *
 * ====================================================================
 *  EXERCÍCIO DA AULA — este controller está vazio de propósito.
 *  Use o FavoritosController como modelo: o código é IGUAL, só muda
 *  o endpoint chamado.
 * ====================================================================
 */
public class AssistindoController {

    private final SupabaseDataService api;
    private final SessionController session;

    public AssistindoController(Context context) {
        this.api = ApiClient.getSupabaseData();
        this.session = new SessionController(context.getApplicationContext());
    }

    /**
     * EXERCÍCIO — Buscar os títulos da lista "Assistindo" do usuário.
     *
     * Passo a passo (copie do FavoritosController):
     *   1) String bearer = "Bearer " + session.getAccessToken();
     *   2) api.listarAssistindo(bearer, "*").enqueue(...);
     *   3) no onResponse de sucesso: callback.onSucesso(resp.body());
     *   4) nos erros: callback.onErro("mensagem");
     */
    public void listar(ListaCallback callback) {
        // TODO (aula): implementar a busca da lista "Assistindo".
    }
}
