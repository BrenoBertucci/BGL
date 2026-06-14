package com.example.bgl.utils;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class CryptoUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // Atenção: Para AES-128, a chave e o vetor deve ser exataemnteo 16 caracteres (16 Bytes).
    private static final String SECRET_KEY = "ChaveSecretaBGL!";
    private static final String INIT_VECTOR = "VetorInicialBGL!";

    public static String criptografar(String textoPlano) {
        if (textoPlano == null || textoPlano.isEmpty()) return "";
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));

            // Base64 transforma os bytes embaralhados em um texto que JSON aceita enviar.
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String descriptografar(String textoCifrado) {
        if (textoCifrado == null || textoCifrado.isEmpty()) return "";
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(textoCifrado, Base64.NO_WRAP));

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}


