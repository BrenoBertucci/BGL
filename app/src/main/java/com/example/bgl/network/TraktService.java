package com.example.bgl.network;

import com.example.bgl.model.TraktBusca;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Endpoint de busca da Trakt.
 * GET /search/{type}?query=...&extended=full,images
 *
 * "type" pode ser "movie", "show" ou "movie,show" (para ambos).
 * Os headers (trakt-api-key, trakt-api-version) são adicionados pelo ApiClient.
 */
public interface TraktService {

    @GET("search/{type}")
    Call<List<TraktBusca>> buscar(
            @Path(value = "type", encoded = true) String type,
            @Query("query") String termo,
            @Query("extended") String extended,
            @Query("page") int pagina,
            @Query("limit") int limite);
}
