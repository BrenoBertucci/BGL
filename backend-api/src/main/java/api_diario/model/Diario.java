package api_diario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Diario {

    // O ID será o próprio ID do filme.
    @Id
    private Integer traktId;

    // o texto que chegará já criptografado do Android.
    private String textCifrado;

    //Construtor padrão obrigátorio para o Spring/JPA.
    public Diario(){
    }

    // Construtor com parâmetros.
    public Diario(Integer traktId, String textCifrado) {
        this.traktId = traktId;
        this.textCifrado = textCifrado;

    }

    // Getters e Setters.
    public Integer getTraktId() {
        return traktId;
    }

    public void setTraktId(Integer traktId) {
        this.traktId = traktId;
    }

    public String getTextCifrado() {
        return textCifrado;
    }

    public void setTextCifrado(String textCifrado) {
        this.textCifrado = textCifrado;
    }
}
