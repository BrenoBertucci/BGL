package com.example.bgl;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
 */
public class FavoritosActivity extends AppCompatActivity {

    private TextView txtStatus;
    private RecyclerView recycler;
    private ItemSalvoAdapter adapter;
    private FavoritosController favoritosController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoritos);
        Ui.aplicarInsets(findViewById(R.id.lista_root));

        favoritosController = new FavoritosController(this);

        txtStatus = findViewById(R.id.txt_status);
        recycler = findViewById(R.id.recycler_lista);
        ImageButton btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        // Segurar um item pede a remoção.
        adapter = new ItemSalvoAdapter(this::confirmarRemocao);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();   // recarrega sempre que a tela volta a aparecer
    }

    /** Mostra um diálogo de confirmação antes de remover (UC09). */
    private void confirmarRemocao(ItemSalvo item) {
        new AlertDialog.Builder(this)
                .setTitle("Remover")
                .setMessage("Remover \"" + item.titulo + "\" dos favoritos?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Remover", (dialog, which) -> remover(item))
                .show();
    }

    private void remover(ItemSalvo item) {
        favoritosController.remover(item, new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                Toast.makeText(FavoritosActivity.this, "Removido.", Toast.LENGTH_SHORT).show();
                carregar();   // recarrega a lista
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(FavoritosActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void carregar() {
        txtStatus.setText(R.string.lista_carregando);

        favoritosController.listar(new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                if (itens == null || itens.isEmpty()) {
                    txtStatus.setText(R.string.lista_vazia);
                } else {
                    txtStatus.setText(R.string.lista_dica_remover);
                }
                adapter.atualizar(itens);
                recycler.scheduleLayoutAnimation();   // entrada em cascata
            }

            @Override
            public void onErro(String mensagem) {
                txtStatus.setText(mensagem);
            }
        });
    }
}
