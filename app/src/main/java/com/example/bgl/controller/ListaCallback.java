package com.example.bgl.controller;

import com.example.bgl.model.ItemSalvo;

import java.util.List;

/**
 * Retorno usado pelos controllers das listas (favoritos/assistindo/watchlist).
 * A tela recebe a lista pronta ou uma mensagem de erro.
 */
public interface ListaCallback {
    void onSucesso(List<ItemSalvo> itens);
    void onErro(String mensagem);
}
