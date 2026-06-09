package com.example.bgl.network;

import com.example.bgl.model.ItemSalvo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Acesso às TABELAS do Supabase (API REST do PostgREST).
 * O RLS garante que cada usuário só vê/grava as próprias linhas,
 * desde que o token (Authorization) seja enviado.
 *
 * Endpoints no formato: /rest/v1/{nome_da_tabela}
 */
public interface SupabaseDataService {

    // ---------------- FAVORITOS ----------------
    @GET("rest/v1/favoritos")
    Call<List<ItemSalvo>> listarFavoritos(
            @Header("Authorization") String bearer,
            @Query("select") String colunas);

    @Headers({"Prefer: return=minimal"})
    @POST("rest/v1/favoritos")
    Call<Void> inserirFavorito(
            @Header("Authorization") String bearer,
            @Body ItemSalvo item);

    // ---------------- ASSISTINDO ----------------
    @GET("rest/v1/assistindo")
    Call<List<ItemSalvo>> listarAssistindo(
            @Header("Authorization") String bearer,
            @Query("select") String colunas);

    @Headers({"Prefer: return=minimal"})
    @POST("rest/v1/assistindo")
    Call<Void> inserirAssistindo(
            @Header("Authorization") String bearer,
            @Body ItemSalvo item);

    // ---------------- WATCHLIST ----------------
    @GET("rest/v1/watchlist")
    Call<List<ItemSalvo>> listarWatchlist(
            @Header("Authorization") String bearer,
            @Query("select") String colunas);

    @Headers({"Prefer: return=minimal"})
    @POST("rest/v1/watchlist")
    Call<Void> inserirWatchlist(
            @Header("Authorization") String bearer,
            @Body ItemSalvo item);
}
