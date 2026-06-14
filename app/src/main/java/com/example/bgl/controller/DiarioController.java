package com.example.bgl.controller;

import com.example.bgl.model.DiarioDTO;
import com.example.bgl.network.ApiClient;
import com.example.bgl.utils.CryptoUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiarioController {

    public interface DiarioCallback {
        void onSucesso(String notaDescriptografada);
        void onErro(String mensagem);
    }

    public void salvarDiario(int traktId, String notaPlana, DiarioCallback callback) {
        // Criptografa o texto antes de enviar para a rede.
        String textoCifrado = CryptoUtil.criptografar(notaPlana);
        if (textoCifrado == null) {
            callback.onErro("Erro ao criptografar a nota localmente.");
            return;
        }

        DiarioDTO dto = new DiarioDTO(traktId, textoCifrado);

        // Envia para o Spring Boot.
        ApiClient.getDiarioApi().salvarNota(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSucesso("Salvo com Sucesso");
                } else {
                    callback.onErro("Erro do servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onErro("Falha de rede: " + t.getMessage());
            }
        });
    }

    public void buscarDiario(int traktId, DiarioCallback callback) {
        ApiClient.getDiarioApi().buscarNota(traktId).enqueue(new Callback<DiarioDTO>() {
            @Override
            public void onResponse(Call<DiarioDTO> call, Response<DiarioDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Recebe o texto cifrado do Spring Boot e descriptografa.
                    String decifrado = CryptoUtil.descriptografar(response.body().textCifrado);
                    callback.onSucesso(decifrado);
                } else if (response.code() == 404) {
                    // 404 significa apenas que o usuário apenas não escreveu nada sobre este filme.
                    callback.onSucesso("");
                } else {
                    callback.onErro("Erro ao buscar nota.");
                }
            }

            @Override
            public void onFailure(Call<DiarioDTO> call, Throwable t) {
                callback.onErro("Falha de rede: " + t.getMessage());
            }
        });
    }
}

