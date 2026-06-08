package com.example.bgl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Representa um título salvo em uma das listas do usuário
 * (favoritos / assistindo / watchlist) no Supabase.
 * Os nomes com @SerializedName batem com as colunas das tabelas.
 */
public class ItemSalvo implements Serializable {

    @SerializedName("user_id")
    public String userId;

    @SerializedName("tmdb_id")
    public int tmdbId;

    @SerializedName("tipo")
    public String tipo;        // "movie" ou "tv"

    @SerializedName("titulo")
    public String titulo;

    @SerializedName("poster_url")
    public String posterUrl;

    // Construtor vazio exigido pelo Gson.
    public ItemSalvo() { }

    public ItemSalvo(int tmdbId, String tipo, String titulo, String posterUrl) {
        this.tmdbId = tmdbId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.posterUrl = posterUrl;
    }

    /** Mostra "Filme" ou "Série" de forma amigável. */
    public String getTipoAmigavel() {
        return "movie".equals(tipo) ? "Filme" : "Série";
    }
}
