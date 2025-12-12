package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eternal_games.R;
import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.model.Producto;
import com.example.eternal_games.viewmodel.ProductoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetalleProductoActivity extends AppCompatActivity {

    private ProductoViewModel viewModel;
    private Producto producto;
    private FloatingActionButton fabCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        // Recibimos el producto desde el intent
        producto = (Producto) getIntent().getSerializableExtra("producto");

        // Referencias a la UI
        ImageView imgProducto = findViewById(R.id.imgDetalleProducto);
        TextView tvTitulo = findViewById(R.id.tvDetalleTitulo);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        TextView tvPrecio = findViewById(R.id.tvDetallePrecio);
        Button btnAgregar = findViewById(R.id.btnDetalleAgregar);
        fabCarrito = findViewById(R.id.fabCarrito);

        // Seteamos datos del producto
        if (producto != null) {
            tvTitulo.setText(producto.title);
            tvDescripcion.setText(producto.description);
            tvPrecio.setText("Precio: $" + producto.price);
            if (producto.imgUrl != null && !producto.imgUrl.isEmpty()) {
                Picasso.get()
                        .load(producto.imgUrl)
                        .placeholder(R.drawable.imagen_no_disponible)
                        .error(R.drawable.imagen_no_disponible)
                        .into(imgProducto);
            } else {
                imgProducto.setImageResource(R.drawable.imagen_no_disponible);
            }

        }

        // Botón para agregar al carrito
        btnAgregar.setOnClickListener(v -> {
            viewModel.agregarAlCarrito(producto);
            Toast.makeText(this, producto.title + " agregado al carrito", Toast.LENGTH_SHORT).show();
        });

        // FAB para abrir el carrito
        fabCarrito.setOnClickListener(v -> {
            List<CarritoItem> carritoActual = viewModel.getCarrito().getValue();
            if (carritoActual == null || carritoActual.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CarritoActivity.class);
            intent.putExtra("carrito", new ArrayList<>(carritoActual));
            startActivity(intent);
        });
    }
}
