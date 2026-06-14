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
import com.example.bgl.controller.AssistindoController;
import com.example.bgl.controller.WatchlistController;

import java.util.List;


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
    private AssistindoController assistindoController;
    private WatchlistController watchlistController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        favoritosController = new FavoritosController(this);
        assistindoController = new AssistindoController(this);
        watchlistController = new WatchlistController(this);

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
        btnAssistindo.setOnClickListener(v -> marcarAssistindo());
        btnWatchlist.setOnClickListener(v -> marcarWatchlist());
    }

    /** Monta o item e manda salvar nos favoritos (UC05). */
    private void favoritar() {
        if (titulo == null) return;

        ItemSalvo item = new ItemSalvo();
        item.tmdbId = titulo.id;
        item.tipo = titulo.getTipo();                 
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

    private ItemSalvo montarItem(){
        ItemSalvo item = new ItemSalvo();
        item.tmdbId = titulo.id;
        item.tipo = titulo.getTipo();                 // "movie" ou "tv"
        item.titulo = titulo.getTituloExibicao();
        item.posterUrl = titulo.getPosterUrl();
        return item;
    }

    private void marcarAssistindo(){
        if (titulo == null) return;

        assistindoController.adicionar(montarItem(), new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                Toast.makeText(DetalhesActivity.this, "Adicionado aos assistindo!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalhesActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void marcarWatchlist(){
        if (titulo == null) return;

        watchlistController.adicionar(montarItem(), new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                Toast.makeText(DetalhesActivity.this, "Adicionado à watchlist!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(String mensagem) {
                Toast.makeText(DetalhesActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }
}
