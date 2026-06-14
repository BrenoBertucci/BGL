package com.example.bgl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bgl.controller.AuthController;

/**
 * Tela de Menu (hub do app). Depois do login, o usuário cai aqui e
 * escolhe para onde ir: Buscar, Favoritos, Assistindo ou Watchlist.
 * O logout do app fica somente nesta tela.
 */
public class MenuActivity extends AppCompatActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        Ui.aplicarInsets(findViewById(R.id.menu_root));

        authController = new AuthController(this);

        TextView txtEmail = findViewById(R.id.txt_email);
        Button btnBuscar = findViewById(R.id.btn_buscar);
        Button btnFavoritos = findViewById(R.id.btn_favoritos);
        Button btnAssistindo = findViewById(R.id.btn_assistindo);
        Button btnWatchlist = findViewById(R.id.btn_watchlist);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Mostra o e-mail do usuário logado.
        String email = authController.getSession().getEmail();
        txtEmail.setText(email != null ? email : "");

        // Cada botão abre a sua tela.
        btnBuscar.setOnClickListener(v -> abrir(MainActivity.class));
        btnFavoritos.setOnClickListener(v -> abrir(FavoritosActivity.class));
        btnAssistindo.setOnClickListener(v -> abrir(AssistindoActivity.class));
        btnWatchlist.setOnClickListener(v -> abrir(WatchlistActivity.class));
        btnLogout.setOnClickListener(v -> sair());

        animarEntrada();
    }

    /** Conteúdo do menu sobe com fade ao abrir a tela. */
    private void animarEntrada() {
        View content = findViewById(R.id.menu_content);
        content.setAlpha(0f);
        content.setTranslationY(48f);
        content.animate()
                .alpha(1f).translationY(0f)
                .setDuration(450)
                .start();
    }

    private void abrir(Class<?> tela) {
        startActivity(new Intent(this, tela));
    }

    private void sair() {
        authController.logout(new AuthController.AuthCallback() {
            @Override
            public void onSucesso() { irParaLogin(); }
            @Override
            public void onErro(String mensagem) { irParaLogin(); }
        });
    }

    private void irParaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
