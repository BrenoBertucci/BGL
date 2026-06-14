package com.example.bgl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.bgl.R;
import com.example.bgl.model.ItemSalvo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter das listas salvas (favoritos / assistindo / watchlist).
 * Mostra pôster, título e o tipo (Filme/Série).
 */
public class ItemSalvoAdapter extends RecyclerView.Adapter<ItemSalvoAdapter.ViewHolder> {

    /** Avisa a tela quando o usuário segura um item (para remover). */
    public interface OnRemover {
        void onRemover(ItemSalvo item);
    }

    private final List<ItemSalvo> lista = new ArrayList<>();
    private final OnRemover listener;

    public ItemSalvoAdapter(OnRemover listener) {
        this.listener = listener;
    }

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

        // Pôster com cantos arredondados (acompanha o cartão de vidro).
        int raio = (int) (10 * holder.itemView.getResources().getDisplayMetrics().density);
        Glide.with(holder.itemView.getContext())
                .load(item.posterUrl)
                .placeholder(R.drawable.glass_input)
                .error(R.drawable.glass_input)
                .transform(new CenterCrop(), new RoundedCorners(raio))
                .into(holder.imgPoster);

        // Segurar o item pede a remoção (a tela mostra a confirmação).
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onRemover(item);
            return true;
        });
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
