package com.example.eternal_games.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eternal_games.repository.FirebaseRepository;
import com.example.eternal_games.R;
import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.model.Producto;
import com.squareup.picasso.Picasso;
import java.util.List;
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    public interface OnProductoClickListener {
        void onAgregarClick(Producto producto);
    }

    private Context context;
    private List<Producto> productos;
    private TextView badgeCantidad;
    private List<CarritoItem> carrito;
    private OnProductoClickListener listener;

    public ProductoAdapter(Context context,
                           List<Producto> productos,
                           TextView badgeCantidad,
                           List<CarritoItem> carrito,
                           OnProductoClickListener listener) {
        this.context = context;
        this.productos = productos;
        this.badgeCantidad = badgeCantidad;
        this.carrito = carrito;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto p = productos.get(position);
        holder.titulo.setText(p.title);
        holder.descripcion.setText(p.description);
        holder.txtPrecio.setText("Precio:" + p.price);

        if (p.imgUrl != null && !p.imgUrl.isEmpty()) {
            Picasso.get()
                    .load(p.imgUrl)
                    .placeholder(R.drawable.imagen_no_disponible)
                    .error(R.drawable.imagen_no_disponible)
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.imagen_no_disponible);
        }

        holder.btnAgregar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAgregarClick(p); // notifica al ViewModel
            }
            Toast.makeText(context, p.title + " agregado al carrito", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, txtPrecio;
        ImageView imagen;
        Button btnAgregar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtTitulo);
            descripcion = itemView.findViewById(R.id.txtDescripcion);
            imagen = itemView.findViewById(R.id.imgProducto);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            btnAgregar = itemView.findViewById(R.id.btnAgregar);
        }
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        notifyDataSetChanged();
    }

    public void setCarrito(List<CarritoItem> carritoItems) {
        this.carrito = carritoItems;
        notifyDataSetChanged();
    }
}