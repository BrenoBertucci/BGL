package com.example.bgl.model;

import java.util.List;

/**
 * Um resultado da busca da Trakt. Cada resultado tem um "type"
 * ("movie" ou "show") e o objeto correspondente preenchido.
 * Documentação: GET /search/{type}?query=...
 */
public class TraktBusca {

    public String type;       // "movie" ou "show"
    public TraktItem movie;
    public TraktItem show;

    /** Devolve o objeto certo (filme OU série). */
    public TraktItem getItem() {
        return (movie != null) ? movie : show;
    }

    /** Dados de um filme ou série na Trakt. */
    public static class TraktItem {
        public String title;
        public Integer year;
        public String overview;   // vem com extended=full
        public Double rating;     // vem com extended=full (0 a 10)
        public Ids ids;
        public Images images;     // vem com extended=images
    }

    /** IDs do título em vários serviços. */
    public static class Ids {
        public int trakt;
        public Integer tmdb;
    }

    /** Imagens; "poster" é uma lista (a Trakt devolve 1 por enquanto). */
    public static class Images {
        public List<String> poster;
    }
}
