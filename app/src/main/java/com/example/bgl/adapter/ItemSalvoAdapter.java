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
import com.example.bgl.model.ItemSalvo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter das listas salvas (favoritos / assistindo / watchlist).
 * Mostra pôster, título e o tipo (Filme/Série).
 */
public class ItemSalvoAdapter extends RecyclerView.Adapter<ItemSalvoAdapter.ViewHolder> {

    private final List<ItemSalvo> lista = new ArrayList<>();

    /** Troca a lista exibida e atualiza a tela. */
    public void atualizar(List<ItemSalvo> novos) {
        lista.clear();
        if (novos != null) lista.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salvo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemSalvo item = lista.get(position);

        holder.txtTitulo.setText(item.titulo);
        holder.txtTipo.setText(item.getTipoAmigavel());

        Glide.with(holder.itemView.getContext())
                .load(item.posterUrl)
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtTitulo;
        TextView txtTipo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.img_poster);
            txtTitulo = itemView.findViewById(R.id.txt_titulo);
            txtTipo = itemView.findViewById(R.id.txt_tipo);
        }
    }
}
