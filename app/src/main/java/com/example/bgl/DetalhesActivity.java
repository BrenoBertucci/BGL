package com.example.bgl;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.example.bgl.controller.FavoritosController;
import com.example.bgl.controller.ListaCallback;
import com.example.bgl.model.ItemSalvo;
import com.example.bgl.model.Titulo;
import com.example.bgl.controller.AssistindoController;
import com.example.bgl.controller.WatchlistController;
import com.example.bgl.controller.TraktController;

import java.util.List;


public class DetalhesActivity extends AppCompatActivity {

    /** Chave usada para receber o título enviado pela tela de Busca. */
    public static final String EXTRA_TITULO = "extra_titulo";

    // Views da tela (já ligadas ao layout)
    private ImageView imgPoster;
    private TextView txtTitulo;
    private TextView txtAnoNota;
    private TextView txtSinopse;
    private TextView txtElenco;
    private Button btnFavoritar;
    private Button btnAssistindo;
    private Button btnWatchlist;

    // O filme/série que veio da tela anterior.
    private Titulo titulo;

    private FavoritosController favoritosController;
    private AssistindoController assistindoController;
    private WatchlistController watchlistController;
    private TraktController traktController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        favoritosController = new FavoritosController(this);
        assistindoController = new AssistindoController(this);
        watchlistController = new WatchlistController(this);
        traktController = new TraktController();

        // 1. Liga as variáveis às views do XML.
        imgPoster = findViewById(R.id.img_poster);
        txtTitulo = findViewById(R.id.txt_titulo);
        txtAnoNota = findViewById(R.id.txt_ano_nota);
        txtSinopse = findViewById(R.id.txt_sinopse);
        txtElenco = findViewById(R.id.txt_elenco);
        btnFavoritar = findViewById(R.id.btn_favoritar);
        btnAssistindo = findViewById(R.id.btn_assistindo);
        btnWatchlist = findViewById(R.id.btn_watchlist);

        // 2. Recebe o título enviado pela tela de Busca.
        titulo = (Titulo) getIntent().getSerializableExtra(EXTRA_TITULO);

        // 3. Monta a tela.
        preencherDados();
        configurarBotoes();
        carregarElenco();        // UC04 — elenco
        verificarListas();       // marca os botões já salvos
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

    /** Liga os três botões de ação às suas listas. */
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

        btnAssistindo.setEnabled(false);
        assistindoController.adicionar(montarItem(), new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                btnAssistindo.setEnabled(true);
                Toast.makeText(DetalhesActivity.this, "Adicionado aos assistindo!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(String mensagem) {
                btnAssistindo.setEnabled(true);
                Toast.makeText(DetalhesActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void marcarWatchlist(){
        if (titulo == null) return;

        btnWatchlist.setEnabled(false);
        watchlistController.adicionar(montarItem(), new ListaCallback() {
            @Override
            public void onSucesso(List<ItemSalvo> itens) {
                btnWatchlist.setEnabled(true);
                Toast.makeText(DetalhesActivity.this, "Adicionado à watchlist!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErro(String mensagem) {
                btnWatchlist.setEnabled(true);
                Toast.makeText(DetalhesActivity.this, mensagem, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------------------------------------------------------------
    // Elenco (UC04)
    // ---------------------------------------------------------------
    private void carregarElenco() {
        if (titulo == null) return;
        txtElenco.setText("Carregando elenco...");

        traktController.buscarElenco(titulo.id, titulo.getTipo(), new TraktController.ElencoCallback() {
            @Override
            public void onSucesso(List<String> nomes) {
                if (nomes == null || nomes.isEmpty()) {
                    txtElenco.setText("Elenco não disponível.");
                } else {
                    // Junta os nomes separados por vírgula (TextUtils funciona em qualquer API).
                    txtElenco.setText(TextUtils.join(", ", nomes));
                }
            }

            @Override
            public void onErro(String mensagem) {
                txtElenco.setText("Elenco não disponível.");
            }
        });
    }

    // ---------------------------------------------------------------
    // Indica nos botões se o título já está em cada lista
    // ---------------------------------------------------------------
    private void verificarListas() {
        if (titulo == null) return;

        favoritosController.listar(new ListaCallback() {
            @Override public void onSucesso(List<ItemSalvo> itens) { marcarSeContem(itens, btnFavoritar, "✓ Favorito"); }
            @Override public void onErro(String mensagem) { }
        });
        assistindoController.listar(new ListaCallback() {
            @Override public void onSucesso(List<ItemSalvo> itens) { marcarSeContem(itens, btnAssistindo, "✓ Assistindo"); }
            @Override public void onErro(String mensagem) { }
        });
        watchlistController.listar(new ListaCallback() {
            @Override public void onSucesso(List<ItemSalvo> itens) { marcarSeContem(itens, btnWatchlist, "✓ Na watchlist"); }
            @Override public void onErro(String mensagem) { }
        });
    }

    /** Se o título atual estiver na lista, troca o texto do botão para indicar. */
    private void marcarSeContem(List<ItemSalvo> itens, Button botao, String textoMarcado) {
        if (titulo == null || itens == null) return;
        for (ItemSalvo item : itens) {
            if (item.tmdbId == titulo.id && titulo.getTipo().equals(item.tipo)) {
                botao.setText(textoMarcado);
                return;
            }
        }
    }
}
