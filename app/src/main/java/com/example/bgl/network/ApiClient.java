package com.example.bgl.network;

import com.example.bgl.BuildConfig;

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

    /** Cliente do Supabase AUTH (login, cadastro, logout). */
    public static SupabaseService getSupabase() {
        if (supabaseService == null) {
            supabaseService = retrofitSupabase().create(SupabaseService.class);
        }
        return supabaseService;
    }

    /** Cliente das TABELAS do Supabase (favoritos, assistindo, watchlist). */
    public static SupabaseDataService getSupabaseData() {
        if (supabaseDataService == null) {
            supabaseDataService = retrofitSupabase().create(SupabaseDataService.class);
        }
        return supabaseDataService;
    }

    /** Monta um Retrofit do Supabase que injeta "apikey" e "Content-Type" em toda chamada. */
    private static Retrofit retrofitSupabase() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request comHeaders = original.newBuilder()
                            .header("apikey", BuildConfig.SUPABASE_KEY)
                            .header("Content-Type", "application/json")
                            .build();
                    return chain.proceed(comHeaders);
                })
                .addInterceptor(logging())
                .build();

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.SUPABASE_URL + "/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
