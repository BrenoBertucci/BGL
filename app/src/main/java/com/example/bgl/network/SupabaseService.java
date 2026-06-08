package com.example.bgl.network;

import com.example.bgl.model.AuthModels.AuthResponse;
import com.example.bgl.model.AuthModels.LoginRequest;
import com.example.bgl.model.AuthModels.RefreshRequest;
import com.example.bgl.model.AuthModels.SignUpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Endpoints do Supabase Auth (GoTrue).
 * O header "apikey" é injetado automaticamente pelo ApiClient.
 */
public interface SupabaseService {

    @POST("auth/v1/signup")
    Call<AuthResponse> cadastrar(@Body SignUpRequest body);

    @POST("auth/v1/token")
    Call<AuthResponse> login(@Query("grant_type") String grantType, @Body LoginRequest body);

    @POST("auth/v1/token")
    Call<AuthResponse> renovar(@Query("grant_type") String grantType, @Body RefreshRequest body);

    @POST("auth/v1/logout")
    Call<Void> logout(@Header("Authorization") String bearerToken);
}
