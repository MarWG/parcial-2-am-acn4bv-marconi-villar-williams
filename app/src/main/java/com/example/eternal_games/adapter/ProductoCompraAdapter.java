package com.example.eternal_games.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eternal_games.R;
import com.example.eternal_games.model.Producto;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;


public class ProductoCompraAdapter extends RecyclerView.Adapter<ProductoCompraAdapter.ViewHolder> {


    private List<Producto> productos = new ArrayList<>();


    public void setProductos(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_compra, parent, false);
        return new ViewHolder(vista);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);


        holder.titulo.setText(producto.title);
        holder.cantidad.setText("Cantidad: " + producto.cantidad);
        holder.precio.setText("Precio: $" + producto.price);


        if (producto.imgUrl != null && !producto.imgUrl.isEmpty()) {
            Picasso.get()
                    .load(producto.imgUrl)
                    .placeholder(R.drawable.imagen_no_disponible)
                    .error(R.drawable.imagen_no_disponible)
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.imagen_no_disponible);
        }
    }


    @Override
    public int getItemCount() {
        return productos.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, cantidad, precio, ubicacion;
        ImageView imagen;


        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            cantidad = itemView.findViewById(R.id.txtCantidad);
            precio = itemView.findViewById(R.id.txtPrecio);
            imagen = itemView.findViewById(R.id.imgProducto);
            ubicacion = itemView.findViewById(R.id.txtUbicacionRetiro);
        }
    }
}

