package com.example.bgl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bgl.R;
import com.example.bgl.model.Titulo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter da RecyclerView de resultados.
 * Mostra pôster, título, ano e nota de cada filme/série.
 */
public class TituloAdapter extends RecyclerView.Adapter<TituloAdapter.TituloViewHolder> {

    /** Avisa a tela quando o usuário toca em um item. */
    public interface OnItemClickListener {
        void onItemClick(Titulo titulo);
    }

    private final List<Titulo> lista = new ArrayList<>();
    private final OnItemClickListener listener;

    public TituloAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    /** Troca a lista exibida e atualiza a tela. */
    public void atualizar(List<Titulo> novos) {
        lista.clear();
        lista.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TituloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_titulo, parent, false);
        return new TituloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TituloViewHolder holder, int position) {
        Titulo titulo = lista.get(position);

        holder.txtTitulo.setText(titulo.getTituloExibicao());
        holder.txtAno.setText(titulo.getAno());
        holder.txtNota.setText("⭐ " + String.format("%.1f", titulo.voteAverage));

        Glide.with(holder.itemView.getContext())
                .load(titulo.getPosterUrl())
                .into(holder.imgPoster);

        // Ao tocar no item, avisa a tela (que abre os detalhes).
        holder.itemView.setOnClickListener(v -> listener.onItemClick(titulo));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    /** Guarda as referências das views de um item (padrão ViewHolder). */
    static class TituloViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtTitulo;
        TextView txtAno;
        TextView txtNota;

        TituloViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_poster);
            txtTitulo = itemView.findViewById(R.id.txt_titulo);
            txtAno = itemView.findViewById(R.id.txt_ano);
            txtNota = itemView.findViewById(R.id.txt_nota);
        }
    }
}
