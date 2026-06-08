package com.example.bgl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bgl.adapter.TituloAdapter;
import com.example.bgl.controller.AuthController;
import com.example.bgl.controller.TraktController;
import com.example.bgl.model.Titulo;

import java.util.List;

/**
 * Tela de Busca (UC03). O usuário pesquisa um título na Trakt e os
 * resultados aparecem numa RecyclerView. Ao tocar em um item, abre
 * a tela de Detalhes (UC04).
 */
public class MainActivity extends AppCompatActivity {

    private EditText inputSearch;
    private TextView txtEmail;
    private TextView txtStatus;
    private Button btnSearch;
    private RecyclerView recycler;

    private TituloAdapter adapter;
    private AuthController authController;
    private TraktController traktController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        authController = new AuthController(this);
        traktController = new TraktController();

        inputSearch = findViewById(R.id.input_search);
        txtEmail = findViewById(R.id.txt_email);
        txtStatus = findViewById(R.id.txt_status);
        btnSearch = findViewById(R.id.btn_search);
        recycler = findViewById(R.id.recycler_resultados);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Mostra o e-mail do usuário logado.
        String email = authController.getSession().getEmail();
        txtEmail.setText(email != null ? email : "");

        // Configura a lista: layout vertical + adapter.
        adapter = new TituloAdapter(this::abrirDetalhes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> buscar());
        btnLogout.setOnClickListener(v -> sair());
    }

    private void buscar() {
        String termo = inputSearch.getText().toString().trim();
        if (TextUtils.isEmpty(termo)) {
            inputSearch.setError(getString(R.string.error_required));
            return;
        }

        btnSearch.setEnabled(false);
        txtStatus.setText("Buscando...");

        traktController.buscar(termo, TraktController.Filtro.AMBOS, new TraktController.BuscaCallback() {
            @Override
            public void onSucesso(List<Titulo> resultados) {
                btnSearch.setEnabled(true);
                if (resultados.isEmpty()) {
                    txtStatus.setText("Nenhum título encontrado para '" + termo + "'.");
                } else {
                    txtStatus.setText("");
                }
                adapter.atualizar(resultados);
            }

            @Override
            public void onErro(String mensagem) {
                btnSearch.setEnabled(true);
                txtStatus.setText(mensagem);
            }
        });
    }

    /** Abre a tela de Detalhes enviando o título selecionado. */
    private void abrirDetalhes(Titulo titulo) {
        Intent intent = new Intent(this, DetalhesActivity.class);
        intent.putExtra(DetalhesActivity.EXTRA_TITULO, titulo);
        startActivity(intent);
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
        finish();
    }
}
