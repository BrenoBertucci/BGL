package com.example.bgl.network;

import com.example.bgl.model.DiarioDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DiarioService {

    @POST("api/diario")
    Call<Void> salvarNota(@Body DiarioDTO diario);

    @GET("api/diario/filme/{id}")
    Call<DiarioDTO> buscarNota(@Path("id") int id);
}
