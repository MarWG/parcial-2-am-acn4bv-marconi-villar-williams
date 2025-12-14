package com.example.eternal_games.adapter;

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

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.CompraViewHolder> {

    private List<Compra> compras = new ArrayList<>();

    // Método para actualizar la lista
    public void setCompras(List<Compra> nuevasCompras) {
        this.compras = nuevasCompras;
        notifyDataSetChanged();
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
        holder.txtFecha.setText(compra.fecha != null ? compra.fecha.toString() : "Sin fecha");
        holder.txtTotal.setText("Total: $" + compra.totalGeneral);
    }

    @Override
    public int getItemCount() {
        return compras.size();
    }

    static class CompraViewHolder extends RecyclerView.ViewHolder {
        TextView txtCompraId, txtFecha, txtTotal;

        public CompraViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCompraId = itemView.findViewById(R.id.txtCompraId);
            txtFecha = itemView.findViewById(R.id.txtFechaCompra);
            txtTotal = itemView.findViewById(R.id.txtTotalCompra);
        }
    }
}