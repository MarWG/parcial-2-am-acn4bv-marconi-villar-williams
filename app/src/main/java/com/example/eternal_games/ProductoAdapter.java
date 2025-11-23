package com.example.eternal_games;

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
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private Context context;
    private List<Producto> productos;
    private TextView badgeCantidad;
    private List<CarritoItem> carrito;
    private FirebaseRepository firebaseRepository;
    private String userId;


    public ProductoAdapter(Context context,
                           List<Producto> productos,
                           TextView badgeCantidad,
                           List<CarritoItem> carrito,
                           FirebaseRepository firebaseRepository,
                           String userId) {
        this.context = context;
        this.productos = productos;
        this.badgeCantidad = badgeCantidad;
        this.carrito = carrito;
        this.firebaseRepository = firebaseRepository;
        this.userId = userId;
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
        holder.imagen.setImageResource(p.img);
        holder.txtPrecio.setText("Precio:" + p.price);

        holder.btnAgregar.setOnClickListener(v -> {
            boolean yaExiste = false;
            for (CarritoItem item : carrito) {
                if (item.producto.id.equals(p.id)) { //usar equals si es String
                    item.cantidad++;
                    yaExiste = true;

                    //Actualizar cantidad en Firebase
                    firebaseRepository.agregarAlCarrito(userId, p.id, item.cantidad,
                            aVoid -> Log.d("Carrito", "Cantidad actualizada en Firebase"),
                            e -> Log.e("Carrito", "Error al actualizar", e)
                    );
                    break;
                }
            }

            if (!yaExiste) {
                carrito.add(new CarritoItem(p, 1));

                //Agregar nuevo producto en Firebase
                firebaseRepository.agregarAlCarrito(userId, p.id, 1,
                        aVoid -> Log.d("Carrito", "Producto agregado en Firebase"),
                        e -> Log.e("Carrito", "Error al agregar", e)
                );
            }
            if (badgeCantidad != null) {
                int totalUnidades = 0;
                for (CarritoItem item : carrito) {
                    totalUnidades += item.cantidad;
                }
                badgeCantidad.setText(String.valueOf(totalUnidades));
                badgeCantidad.setVisibility(View.VISIBLE);
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
}