package com.example.bgl;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bgl.controller.AuthController;

/**
 * Tela inicial. Decide para onde o usuário vai (auto-login — UC02, A2):
 *  - Se há sessão salva  -> MainActivity
 *  - Senão               -> LoginActivity
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AuthController auth = new AuthController(this);

        Class<?> destino = auth.estaLogado() ? MenuActivity.class : LoginActivity.class;
        startActivity(new Intent(this, destino));
        finish();
    }
}
