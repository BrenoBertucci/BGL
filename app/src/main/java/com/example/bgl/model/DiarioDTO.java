package com.example.bgl.model;

import com.google.gson.annotations.SerializedName;
public class DiarioDTO {

    @SerializedName("traktId")
    public int traktId;

    @SerializedName("textCifrado")
    public String textCifrado;

    public DiarioDTO(int traktId, String textCifrado) {
        this.traktId = traktId;
        this.textCifrado = textCifrado;
    }
}
