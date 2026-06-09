package com.example.bgl.controller;

import androidx.annotation.NonNull;

import com.example.bgl.BuildConfig;
import com.example.bgl.model.TraktBusca;
import com.example.bgl.model.Titulo;
import com.example.bgl.network.ApiClient;
import com.example.bgl.network.TraktService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Controla as buscas de filmes e séries na Trakt (UC03).
 * Converte a resposta da Trakt para o modelo "Titulo" que o app já usa,
 * então as telas e o adapter continuam iguais.
 */
public class TraktController {

    /** Filtro de busca escolhido pelo usuário. */
    public enum Filtro { FILME, SERIE, AMBOS }

    public interface BuscaCallback {
        void onSucesso(List<Titulo> resultados);
        void onErro(String mensagem);
    }

    private final TraktService api;

    public TraktController() {
        this.api = ApiClient.getTrakt();
    }

    public void buscar(String termo, Filtro filtro, BuscaCallback callback) {

        String tipo = tipoDoFiltro(filtro);

        api.buscar(tipo, termo, "full,images", 1, 30).enqueue(new Callback<List<TraktBusca>>() {
            @Override
            public void onResponse(@NonNull Call<List<TraktBusca>> call,
                                   @NonNull Response<List<TraktBusca>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    callback.onSucesso(converter(resp.body()));
                } else {
                    callback.onErro("Não foi possível carregar os resultados.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TraktBusca>> call, @NonNull Throwable t) {
                callback.onErro("Sem conexão. Tente novamente.");
            }
        });
    }

    /** Traduz o filtro para o "type" da Trakt. */
    private String tipoDoFiltro(Filtro filtro) {
        switch (filtro) {
            case FILME: return "movie";
            case SERIE: return "show";
            default:    return "movie,show";
        }
    }

    /** Converte a lista da Trakt para a lista de Titulo usada no app. */
    private List<Titulo> converter(List<TraktBusca> resultados) {
        List<Titulo> lista = new ArrayList<>();

        for (TraktBusca resultado : resultados) {
            TraktBusca.TraktItem item = resultado.getItem();
            if (item == null) continue;

            Titulo titulo = new Titulo();
            titulo.mediaType = resultado.type;   // "movie" ou "show"

            // Título: filmes usam "title"; séries também (a Trakt usa "title" nos dois).
            if ("movie".equals(resultado.type)) {
                titulo.title = item.title;
                if (item.year != null) titulo.releaseDate = String.valueOf(item.year);
            } else {
                titulo.name = item.title;
                if (item.year != null) titulo.firstAirDate = String.valueOf(item.year);
            }

            titulo.overview = item.overview;
            if (item.rating != null) titulo.voteAverage = item.rating;
            if (item.ids != null) titulo.id = item.ids.trakt;
            if (item.images != null && item.images.poster != null && !item.images.poster.isEmpty()) {
                titulo.posterPath = item.images.poster.get(0);
            }

            lista.add(titulo);
        }
        return lista;
    }
}
