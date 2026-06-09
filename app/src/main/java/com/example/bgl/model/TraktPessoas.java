package com.example.bgl.model;

import java.util.List;

/**
 * Resposta do endpoint de elenco da Trakt (.../people).
 * Só usamos a parte "cast" (atores). Cada entrada tem o personagem
 * e a pessoa (de onde tiramos o nome).
 */
public class TraktPessoas {

    public List<Cast> cast;

    public static class Cast {
        public String character;   // personagem (ex.: "Tony Stark")
        public Person person;
    }

    public static class Person {
        public String name;        // nome do ator (ex.: "Robert Downey Jr.")
    }
}
