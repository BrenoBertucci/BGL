package com.example.bgl.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Representa um filme ou série (dados vindos da Trakt).
 * Usamos "title" para filmes e "name" para séries, então
 * expomos helpers para a UI não precisar saber dessa diferença.
 *
 * Implementa Serializable para podermos enviar o objeto inteiro
 * de uma tela para outra via Intent (forma simples, sem boilerplate).
 */
public class Titulo implements Serializable {

    public int id;

    @SerializedName("title")
    public String title;        // filmes

    @SerializedName("name")
    public String name;         // séries

    @SerializedName("poster_path")
    public String posterPath;

    @SerializedName("overview")
    public String overview;

    @SerializedName("vote_average")
    public double voteAverage;

    @SerializedName("release_date")
    public String releaseDate;  // filmes (yyyy-MM-dd)

    @SerializedName("first_air_date")
    public String firstAirDate; // séries

    @SerializedName("media_type")
    public String mediaType;    // "movie" | "tv" (vem no /search/multi)

    /** Título a exibir, independente de ser filme ou série. */
    public String getTituloExibicao() {
        return (title != null && !title.isEmpty()) ? title : name;
    }

    /** Ano de lançamento (só os 4 primeiros dígitos da data). */
    public String getAno() {
        String data = (releaseDate != null && !releaseDate.isEmpty()) ? releaseDate : firstAirDate;
        if (data != null && data.length() >= 4) return data.substring(0, 4);
        return "—";
    }

    /** Retorna "movie" ou "tv". */
    public String getTipo() {
        if (mediaType != null && !mediaType.isEmpty()) return mediaType;
        // Se veio de /search/movie ou /search/tv não há media_type:
        return (title != null && !title.isEmpty()) ? "movie" : "tv";
    }

    /** URL completa do pôster (ou null se não houver).
     *  A Trakt devolve o caminho sem "https://", então adicionamos quando falta. */
    public String getPosterUrl() {
        if (posterPath == null || posterPath.isEmpty()) return null;
        if (posterPath.startsWith("http")) return posterPath;
        return "https://" + posterPath;
    }
}
