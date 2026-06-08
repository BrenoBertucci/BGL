package com.example.bgl;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bgl.adapter.ItemSalvoAdapter;
import com.example.bgl.controller.ListaCallback;
import com.example.bgl.controller.WatchlistController;
import com.example.bgl.model.ItemSalvo;

import java.util.List;

/**
 * Tela "Assistir Mais Tarde" / Watchlist (UC08). A TELA está pronta;
 * quem carrega os dados é o WatchlistController — o EXERCÍCIO DA AULA.
 * Quando você implementar o controller, esta tela já vai funcionar.
 */
public class WatchlistActivity extends AppCompatActivity {

    private TextView txtStatus;
    private ItemSalvoAdapter adapter;
    private WatchlistController watchlistController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        watchlistController = new WatchlistController(this);

        txtStatus = findViewById(R.id.txt_status);
        RecyclerView recycler = findViewById(R.id.recycler_lista);

        adapter = new ItemSalvoAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        carregar();
    }

    private void carregar() {
        txtStatus.setText("Carregando...");

        watchlistController.listar(new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                if (itens == null || itens.isEmpty()) {
                    txtStatus.setText("Você ainda não adicionou nenhum título aqui.");
                } else {
                    txtStatus.setText("");
                }
                adapter.atualizar(itens);
            }

            @Override
            public void onErro(String mensagem) {
                txtStatus.setText(mensagem);
            }
        });
    }
}
