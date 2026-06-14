package com.example.bgl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bgl.controller.AuthController;

/**
 * Tela inicial. Anima a marca e decide para onde o usuário vai (auto-login — UC02, A2):
 *  - Se há sessão salva  -> MenuActivity (hub)
 *  - Senão               -> LoginActivity
 */
public class SplashActivity extends AppCompatActivity {

    /** Tempo de exibição da splash (deixa a animação respirar). */
    private static final long SPLASH_DELAY_MS = 1400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        animarEntrada();

        new Handler(Looper.getMainLooper())
                .postDelayed(this::irParaProximaTela, SPLASH_DELAY_MS);
    }

    /** Logo cresce com leve "bounce"; textos surgem em fade na sequência. */
    private void animarEntrada() {
        View logo = findViewById(R.id.img_logo);
        View brand = findViewById(R.id.txt_brand);
        View tagline = findViewById(R.id.txt_tagline);

        logo.setAlpha(0f);
        logo.setScaleX(0.6f);
        logo.setScaleY(0.6f);
        logo.animate()
                .alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(650)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();

        brand.setAlpha(0f);
        brand.setTranslationY(24f);
        brand.animate()
                .alpha(1f).translationY(0f)
                .setStartDelay(300)
                .setDuration(500)
                .start();

        tagline.setAlpha(0f);
        tagline.animate()
                .alpha(1f)
                .setStartDelay(550)
                .setDuration(500)
                .start();
    }

    private void irParaProximaTela() {
        if (isFinishing()) return;

        AuthController auth = new AuthController(this);
        Class<?> destino = auth.estaLogado() ? MenuActivity.class : LoginActivity.class;

        startActivity(new Intent(this, destino));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
