package com.example.bgl;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bgl.adapter.TituloAdapter;
import com.example.bgl.controller.TraktController;
import com.example.bgl.model.Titulo;

import java.util.List;

/**
 * Tela de Busca (UC03). O usuário pesquisa um título na Trakt e os
 * resultados aparecem numa RecyclerView. Ao tocar em um item, abre
 * a tela de Detalhes (UC04).
 *
 * Chega-se aqui pelo Menu (hub); o botão de voltar retorna para lá.
 * O logout fica somente no Menu — assim a navegação tem um caminho único.
 */
public class MainActivity extends AppCompatActivity {

    private EditText inputSearch;
    private TextView txtStatus;
    private Button btnSearch;
    private RecyclerView recycler;

    private TituloAdapter adapter;
    private TraktController traktController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Ui.aplicarInsets(findViewById(R.id.main));

        traktController = new TraktController();

        inputSearch = findViewById(R.id.input_search);
        txtStatus = findViewById(R.id.txt_status);
        btnSearch = findViewById(R.id.btn_search);
        recycler = findViewById(R.id.recycler_resultados);
        ImageButton btnBack = findViewById(R.id.btn_back);

        // Configura a lista: layout vertical + adapter.
        adapter = new TituloAdapter(this::abrirDetalhes);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> buscar());
        btnBack.setOnClickListener(v -> finish());

        // Buscar direto pelo teclado (ação "pesquisar").
        inputSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscar();
                return true;
            }
            return false;
        });
    }

    private void buscar() {
        String termo = inputSearch.getText().toString().trim();
        if (TextUtils.isEmpty(termo)) {
            inputSearch.setError(getString(R.string.error_required));
            return;
        }

        btnSearch.setEnabled(false);
        txtStatus.setText(R.string.search_loading);

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
                recycler.scheduleLayoutAnimation();   // entrada em cascata
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
}
