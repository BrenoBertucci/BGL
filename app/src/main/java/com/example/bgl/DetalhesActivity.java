package com.example.bgl;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bgl.controller.FavoritosController;
import com.example.bgl.controller.ListaCallback;
import com.example.bgl.model.ItemSalvo;
import com.example.bgl.model.Titulo;

import java.util.List;

/**
 * Tela de Detalhes do título (UC04).
 *
 * ====================================================================
 *  ATENÇÃO: esta tela está PROPOSITALMENTE sem a lógica.
 *  O layout (activity_detalhes.xml) já está pronto e as views já
 *  estão referenciadas aqui embaixo. O que falta é o EXERCÍCIO DA AULA:
 *
 *   1) Preencher os campos com os dados do "titulo" (método preencherDados).
 *   2) Fazer os 3 botões salvarem nas listas (método configurarBotoes).
 *
 *  Os TODOs marcam exatamente onde escrever cada parte.
 * ====================================================================
 */
public class DetalhesActivity extends AppCompatActivity {

    /** Chave usada para receber o título enviado pela tela de Busca. */
    public static final String EXTRA_TITULO = "extra_titulo";

    // Views da tela (já ligadas ao layout)
    private ImageView imgPoster;
    private TextView txtTitulo;
    private TextView txtAnoNota;
    private TextView txtSinopse;
    private Button btnFavoritar;
    private Button btnAssistindo;
    private Button btnWatchlist;

    // O filme/série que veio da tela anterior.
    private Titulo titulo;

    private FavoritosController favoritosController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        favoritosController = new FavoritosController(this);

        // 1. Liga as variáveis às views do XML.
        imgPoster = findViewById(R.id.img_poster);
        txtTitulo = findViewById(R.id.txt_titulo);
        txtAnoNota = findViewById(R.id.txt_ano_nota);
        txtSinopse = findViewById(R.id.txt_sinopse);
        btnFavoritar = findViewById(R.id.btn_favoritar);
        btnAssistindo = findViewById(R.id.btn_assistindo);
        btnWatchlist = findViewById(R.id.btn_watchlist);

        // 2. Recebe o título enviado pela tela de Busca.
        titulo = (Titulo) getIntent().getSerializableExtra(EXTRA_TITULO);

        // 3. Monta a tela.
        preencherDados();
        configurarBotoes();
    }

    /** Mostra os dados do título na tela (pôster, nome, ano/nota e sinopse). */
    private void preencherDados() {
        if (titulo == null) return;

        txtTitulo.setText(titulo.getTituloExibicao());
        txtAnoNota.setText(titulo.getAno() + "   ⭐ " + String.format("%.1f", titulo.voteAverage));

        if (titulo.overview != null && !titulo.overview.isEmpty()) {
            txtSinopse.setText(titulo.overview);
        } else {
            txtSinopse.setText("Sem sinopse disponível.");
        }

        Glide.with(this)
                .load(titulo.getPosterUrl())
                .into(imgPoster);
    }

    /**
     * Botões de ação.
     *  - "Favoritar" já está IMPLEMENTADO (exemplo).
     *  - "Assistindo" e "Watchlist" ficam como EXERCÍCIO DA AULA:
     *    é só copiar o padrão do favoritar, trocando o controller.
     */
    private void configurarBotoes() {
        btnFavoritar.setOnClickListener(v -> favoritar());

        // TODO (aula): btnAssistindo e btnWatchlist — mesma ideia do favoritar.
    }

    /** Monta o item e manda salvar nos favoritos (UC05). */
    private void favoritar() {
        if (titulo == null) return;

        ItemSalvo item = new ItemSalvo();
        item.tmdbId = titulo.id;
        item.tipo = titulo.getTipo();                 // "movie" ou "tv"
        item.titulo = titulo.getTituloExibicao();
        item.posterUrl = titulo.getPosterUrl();

        btnFavoritar.setEnabled(false);
        favoritosController.adicionar(item, new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                btnFavoritar.setEnabled(true);
                Toast.makeText(DetalhesActivity.this, "Adicionado aos favoritos!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(String mensagem) {
                btnFavoritar.setEnabled(true);
                Toast.makeText(DetalhesActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}
