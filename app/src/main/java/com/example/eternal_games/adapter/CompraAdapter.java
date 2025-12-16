package com.example.eternal_games.adapter;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eternal_games.R;
import com.example.eternal_games.model.Compra;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.CompraViewHolder> {
    private List<Compra> compras = new ArrayList<>();
    private Consumer<Compra> onItemClick; // callback simple
    public interface OnItemClickListener {
        void onItemClicked(Compra compra);
    }
    public void setOnItemClick(Consumer<Compra> onItemClick) {
        this.onItemClick = onItemClick;
    }

    // Método para actualizar la lista
    public void setCompras(List<Compra> nuevasCompras) {
        this.compras = nuevasCompras;
        notifyDataSetChanged();
    }

    public List<Compra> getCompras() {
        return compras;
    }

    @NonNull
    @Override
    public CompraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_compra, parent, false);
        return new CompraViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompraViewHolder holder, int position) {
        Compra compra = compras.get(position);
        holder.txtCompraId.setText("Compra #" + compra.id);
        holder.txtCompraId.setTypeface(null, compra.leido ? Typeface.NORMAL : Typeface.BOLD);
        holder.txtFecha.setText(compra.fecha != null ? compra.fecha.toString() : "Sin fecha");
        holder.txtTotal.setText("Total: $" + compra.totalGeneral);
        int fondo = compra.leido ? R.color.white : R.color.colorFondoClaro;
        holder.itemView.setBackgroundResource(fondo);
        int colorIndicador = compra.leido ? R.color.white : R.color.purple_500;
        holder.indicadorEstado.setBackgroundResource(colorIndicador);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.accept(compra);
        });
    }

    @Override
    public int getItemCount() {
        return compras.size();
    }

    static class CompraViewHolder extends RecyclerView.ViewHolder {
        TextView txtCompraId, txtFecha, txtTotal;
        View indicadorEstado;
        public CompraViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCompraId = itemView.findViewById(R.id.txtCompraId);
            txtFecha = itemView.findViewById(R.id.txtFechaCompra);
            txtTotal = itemView.findViewById(R.id.txtTotalCompra);
            indicadorEstado = itemView.findViewById(R.id.indicadorEstado);
        }
    }
}