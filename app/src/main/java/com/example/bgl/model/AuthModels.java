package com.example.bgl.model;

/**
 * Modelos (DTOs) usados na comunicação com o Supabase Auth.
 * Campos públicos por simplicidade — o Gson os preenche automaticamente.
 */
public class AuthModels {

    /** Corpo enviado no cadastro (POST /auth/v1/signup). */
    public static class SignUpRequest {
        public String email;
        public String password;
        public Data data;

        public SignUpRequest(String email, String password, String nome) {
            this.email = email;
            this.password = password;
            this.data = new Data(nome);
        }

        /** Vai como user_metadata; o trigger do banco lê "nome" daqui. */
        public static class Data {
            public String nome;
            public Data(String nome) { this.nome = nome; }
        }
    }

    /** Corpo enviado no login (POST /auth/v1/token?grant_type=password). */
    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    /** Corpo enviado na renovação (POST /auth/v1/token?grant_type=refresh_token). */
    public static class RefreshRequest {
        public String refresh_token;
        public RefreshRequest(String refreshToken) { this.refresh_token = refreshToken; }
    }

    /** Resposta de login/cadastro/refresh do Supabase. */
    public static class AuthResponse {
        public String access_token;
        public String refresh_token;
        public long expires_in;   // segundos até expirar
        public User user;
    }

    /** Usuário autenticado. */
    public static class User {
        public String id;
        public String email;
    }
}
