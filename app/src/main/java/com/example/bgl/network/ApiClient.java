package com.example.bgl.network;

import android.content.Context;

import com.example.bgl.BuildConfig;
import com.example.bgl.controller.SessionController;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Fábrica única (singleton) dos clientes Retrofit.
 * Centraliza a configuração de rede para Supabase e Trakt.
 */
public class ApiClient {

    private static final String TRAKT_BASE_URL = "https://api.trakt.tv/";

    private static SupabaseService supabaseService;
    private static SupabaseDataService supabaseDataService;
    private static TraktService traktService;

    // Sessão usada pelo TokenAuthenticator para renovar o token.
    private static SessionController session;

    /** Deve ser chamado antes de usar o cliente de dados (passe qualquer Context). */
    public static void init(Context context) {
        if (session == null) {
            session = new SessionController(context.getApplicationContext());
        }
    }

    /** Cliente do Supabase AUTH (login, cadastro, logout). SEM renovação (evita loop). */
    public static SupabaseService getSupabase() {
        if (supabaseService == null) {
            OkHttpClient client = supabaseClientBuilder().build();
            supabaseService = new Retrofit.Builder()
                    .baseUrl(BuildConfig.SUPABASE_URL + "/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SupabaseService.class);
        }
        return supabaseService;
    }

    /** Cliente das TABELAS do Supabase. COM renovação automática do token (401). */
    public static SupabaseDataService getSupabaseData() {
        if (supabaseDataService == null) {
            OkHttpClient.Builder builder = supabaseClientBuilder();
            if (session != null) {
                builder.authenticator(new TokenAuthenticator(session));
            }
            OkHttpClient client = builder.build();
            supabaseDataService = new Retrofit.Builder()
                    .baseUrl(BuildConfig.SUPABASE_URL + "/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(SupabaseDataService.class);
        }
        return supabaseDataService;
    }

    /** Builder com os headers "apikey" e "Content-Type" em toda chamada do Supabase. */
    private static OkHttpClient.Builder supabaseClientBuilder() {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request comHeaders = original.newBuilder()
                            .header("apikey", BuildConfig.SUPABASE_KEY)
                            .header("Content-Type", "application/json")
                            .build();
                    return chain.proceed(comHeaders);
                })
                .addInterceptor(logging());
    }

    /** Cliente da Trakt: injeta os headers obrigatórios em toda chamada. */
    public static TraktService getTrakt() {
        if (traktService == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request comHeaders = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("trakt-api-version", "2")
                                .header("trakt-api-key", BuildConfig.TRAKT_CLIENT_ID)
                                .build();
                        return chain.proceed(comHeaders);
                    })
                    .addInterceptor(logging())
                    .build();

            traktService = new Retrofit.Builder()
                    .baseUrl(TRAKT_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(TraktService.class);
        }
        return traktService;
    }

    private static HttpLoggingInterceptor logging() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }
}
