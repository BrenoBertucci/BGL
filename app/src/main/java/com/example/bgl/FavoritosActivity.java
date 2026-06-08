package com.example.bgl;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bgl.adapter.ItemSalvoAdapter;
import com.example.bgl.controller.FavoritosController;
import com.example.bgl.controller.ListaCallback;
import com.example.bgl.model.ItemSalvo;

import java.util.List;

/**
 * Tela de Favoritos (UC08). Mostra os títulos favoritados pelo usuário.
 * EXEMPLO COMPLETO — as telas Assistindo e Watchlist seguem o mesmo molde.
 */
public class FavoritosActivity extends AppCompatActivity {

    private TextView txtStatus;
    private ItemSalvoAdapter adapter;
    private FavoritosController favoritosController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        favoritosController = new FavoritosController(this);

        txtStatus = findViewById(R.id.txt_status);
        RecyclerView recycler = findViewById(R.id.recycler_lista);

        adapter = new ItemSalvoAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        carregar();
    }

    private void carregar() {
        txtStatus.setText("Carregando...");

        favoritosController.listar(new ListaCallback() {
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
