package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eternal_games.adapter.CarritoAdapter;
import com.example.eternal_games.CarritoCallback;
import com.example.eternal_games.CompraFinalizada;
import com.example.eternal_games.repository.FirebaseRepository;
import com.example.eternal_games.R;
import com.example.eternal_games.model.CarritoItem;

import java.util.ArrayList;
import java.util.List;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrito;
    private ArrayList<CarritoItem> carrito;

    private TextView txtTotalGeneral;
    private TextView txtCantidadTotal;
    private Button btnFinalizarCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Recibir el carrito desde MainActivity
        carrito = (ArrayList<CarritoItem>) getIntent().getSerializableExtra("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        // Configurar RecyclerView
        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        CarritoAdapter adapter = new CarritoAdapter(this, carrito);

        adapter.setCallback(new CarritoCallback() {
            @Override
            public void onCarritoActualizado(List<CarritoItem> nuevoCarrito) {
                actualizarResumen(); // recalcula total y cantidad
            }

            @Override
            public void onProductoEliminado(CarritoItem eliminado) {
                FirebaseRepository repo = new FirebaseRepository();
                String userId = repo.obtenerUserId();

                repo.eliminarDelCarrito(userId, eliminado.producto.id,
                        aVoid -> Toast.makeText(CarritoActivity.this,
                                eliminado.producto.title + " eliminado del carrito",
                                Toast.LENGTH_SHORT).show(),
                        e -> Toast.makeText(CarritoActivity.this,
                                "Error al eliminar de Firebase",
                                Toast.LENGTH_SHORT).show()
                );
            }
        });
        ////////////////////////////////////////////////////////////////////

        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));
        recyclerCarrito.setAdapter(adapter);

        // Referencias visuales
        txtTotalGeneral = findViewById(R.id.txtTotalGeneral);
        txtCantidadTotal = findViewById(R.id.txtCantidadTotal);
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

        // Mostrar resumen
        actualizarResumen();

        // Botón para volver a MainActivity
        ImageButton btnInicio = findViewById(R.id.btnInicio);
        //btnInicio.setOnClickListener(v -> finish());

        //HACEMOS EL FINIS CON INTENTE PARA DEVOLVER CANTIDAD DE ITEM AL BADGET
        btnInicio.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("carritoActualizado", carrito);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        // Simulacion de boton finalizar compra
//        btnFinalizarCompra.setOnClickListener(v -> {
//            Toast.makeText(this, "Compra finalizada. ¡Gracias!", Toast.LENGTH_LONG).show();
        //});
        btnFinalizarCompra.setOnClickListener(v -> {
            if (carrito.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            int total = 0;
            int cantidad = 0;

            FirebaseRepository repo = new FirebaseRepository();
            String userId = repo.obtenerUserId();

            // Eliminar cada item del carrito en Firebase
            for (CarritoItem item : carrito) {
                total += item.getTotal();
                cantidad += item.cantidad;

                repo.eliminarDelCarrito(userId, item.producto.id,
                        aVoid -> Log.d("Compra", "Item eliminado de Firebase"),
                        e -> Log.e("Compra", "Error al eliminar item", e));
            }

            // Vaciar lista local y actualizar UI
            carrito.clear();
            adapter.notifyDataSetChanged();
            actualizarResumen();

            // Mostrar diálogo de confirmación
            CompraFinalizada dialog = new CompraFinalizada(total, cantidad);
            dialog.show(getSupportFragmentManager(), "CompraFinalizada");

            // Devolver carrito vacío y cantidad al MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("carritoActualizado", carrito);
            resultIntent.putExtra("cantidadTotal", cantidad);
            setResult(RESULT_OK, resultIntent);
        });
    }

    private void actualizarResumen() {
        double total = 0;
        int cantidad = 0;
        for (CarritoItem item : carrito) {
            total += item.getTotal(); // precio * cantidad
            cantidad += item.cantidad;
        }
        txtTotalGeneral.setText("Total: $" + (int)total);
        txtCantidadTotal.setText("Cantidad: " + cantidad);
    }
}