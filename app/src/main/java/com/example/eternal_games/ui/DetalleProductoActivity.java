package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eternal_games.R;
import com.example.eternal_games.model.Producto;
import com.example.eternal_games.viewmodel.CompraViewModel;
import com.example.eternal_games.viewmodel.ProductoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class DetalleProductoActivity extends AppCompatActivity {

    private ProductoViewModel viewModel;
    private Producto producto;
    private FloatingActionButton fabCarrito;
    private TextView badgeCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

        // si el carrito todavía no estaba cargado, lo carga (una vez)
        viewModel.cargarCarritoSiHaceFalta();

        // Recibimos el producto desde el intent
        producto = (Producto) getIntent().getSerializableExtra("producto");

        // Referencias a la UI
        ImageView imgProducto = findViewById(R.id.imgDetalleProducto);
        TextView tvTitulo = findViewById(R.id.tvDetalleTitulo);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        TextView tvPrecio = findViewById(R.id.tvDetallePrecio);
        Button btnAgregar = findViewById(R.id.btnDetalleAgregar);
        fabCarrito = findViewById(R.id.fabCarrito);
        badgeCantidad = findViewById(R.id.badgeCantidad);
        ImageButton btnInicio = findViewById(R.id.btnInicio);
        ImageButton btnContacto = findViewById(R.id.btnContacto);

        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleProductoActivity.this, CompraActivity.class);
            startActivity(intent);
        });


        // Observar carrito => el badge se actualiza solo
        viewModel.getCarrito().observe(this, carritoItems -> {
            int cantidad = viewModel.calcularCantidadTotal(carritoItems);
            actualizarBadge(cantidad);
        });

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
            if (producto == null) {
                Toast.makeText(this, "Producto inválido.", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.agregarAlCarrito(producto);
            Toast.makeText(this, producto.title + " agregado al carrito", Toast.LENGTH_SHORT).show();
            // el badge se actualiza solo por el observer
        });

        // FAB para abrir el carrito
        fabCarrito.setOnClickListener(v -> {
            // ya no hace falta pasar "carrito" por Intent
            Intent intent = new Intent(this, CarritoActivity.class);
            startActivity(intent);
        });

        // Volver siempre a Main
        btnInicio.setOnClickListener(v -> volverAMain());

        CompraViewModel compraViewModel = new ViewModelProvider(this).get(CompraViewModel.class);

        compraViewModel.getHayNotificacionesNoLeidas().observe(this, hayNoLeidas -> {
            ImageView badge = findViewById(R.id.ic_notificacion);
            badge.setVisibility(hayNoLeidas ? View.VISIBLE : View.GONE);
        });
    }

    private void actualizarBadge(int cantidad) {
        if (badgeCantidad == null) return;

        if (cantidad > 0) {
            badgeCantidad.setText(String.valueOf(cantidad));
            badgeCantidad.setVisibility(TextView.VISIBLE);
        } else {
            badgeCantidad.setVisibility(TextView.GONE);
        }
    }

    private void volverAMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}
