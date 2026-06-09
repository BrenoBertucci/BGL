package com.example.bgl.network;

import com.example.bgl.model.TraktBusca;
import com.example.bgl.model.TraktPessoas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Endpoints da Trakt usados no app.
 * Os headers (trakt-api-key, trakt-api-version) são adicionados pelo ApiClient.
 */
public interface TraktService {

    /** Busca: GET /search/{type}?query=...&extended=full,images */
    @GET("search/{type}")
    Call<List<TraktBusca>> buscar(
            @Path(value = "type", encoded = true) String type,
            @Query("query") String termo,
            @Query("extended") String extended,
            @Query("page") int pagina,
            @Query("limit") int limite);

    /** Elenco: GET /movies/{id}/people  ou  GET /shows/{id}/people */
    @GET("{tipo}/{id}/people")
    Call<TraktPessoas> elenco(
            @Path(value = "tipo", encoded = true) String tipo,  // "movies" ou "shows"
            @Path("id") int id);
}
