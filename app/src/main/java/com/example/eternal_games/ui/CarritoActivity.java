package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eternal_games.R;
import com.example.eternal_games.adapter.CarritoAdapter;
import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.viewmodel.CarritoViewModel;
import com.example.eternal_games.viewmodel.CompraViewModel;

import java.util.ArrayList;
import java.util.List;

public class CarritoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrito;
    private TextView txtTotalGeneral;
    private TextView txtCantidadTotal;
    private Button btnFinalizarCompra;
    private CarritoAdapter adapter;
    private CarritoViewModel carritoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        // Referencias visuales
        recyclerCarrito = findViewById(R.id.recyclerCarrito);
        txtTotalGeneral = findViewById(R.id.txtTotalGeneral);
        txtCantidadTotal = findViewById(R.id.txtCantidadTotal);
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);
        ImageButton btnInicio = findViewById(R.id.btnInicio);
        ImageButton btnContacto = findViewById(R.id.btnContacto);

        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(CarritoActivity.this, CompraActivity.class);
            startActivity(intent);
        });

        // ViewModel
        carritoViewModel = new ViewModelProvider(this).get(CarritoViewModel.class);
        CompraViewModel compraViewModel = new ViewModelProvider(this).get(CompraViewModel.class);


        // Adapter: al eliminar, el VM borra (repo actualiza memoria + Firebase)
        adapter = new CarritoAdapter(this, new ArrayList<>(), item -> {
            carritoViewModel.eliminarProducto(item);
            Toast.makeText(this, item.producto.title + " eliminado del carrito", Toast.LENGTH_SHORT).show();
        });

        recyclerCarrito.setLayoutManager(new LinearLayoutManager(this));
        recyclerCarrito.setAdapter(adapter);

        // Observamos el carrito
        carritoViewModel.getCarrito().observe(this, items -> adapter.setItems(items));

        // Observamos totales (calculados desde el carrito)
        carritoViewModel.getCantidadTotal().observe(this, cantidad ->
                txtCantidadTotal.setText("Cantidad: " + cantidad)
        );

        carritoViewModel.getTotalGeneral().observe(this, total ->
                txtTotalGeneral.setText("Total: $" + total)
        );

        // Finalizar compra
        btnFinalizarCompra.setOnClickListener(v -> {
            List<CarritoItem> items = carritoViewModel.getCarrito().getValue();
            if (items == null || items.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tomamos valores ANTES de vaciar
            Integer cantidad = carritoViewModel.getCantidadTotal().getValue();
            Double total = carritoViewModel.getTotalGeneral().getValue();

            if (cantidad == null) cantidad = 0;
            if (total == null) total = 0.0;

            // Vaciar (repo borra en Firebase y memoria)
            compraViewModel.registrarCompra(items);
            carritoViewModel.finalizarCompra();
            compraViewModel.cargarCompras();
            // Dialog resumen
            View view = getLayoutInflater().inflate(R.layout.compra_finalizada, null);
            TextView txtResumen = view.findViewById(R.id.txtResumen);
            txtResumen.setText("Total: $" + total + "\nCantidad: " + cantidad);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .create();

            Button btnCerrar = view.findViewById(R.id.btnCerrar);
            btnCerrar.setOnClickListener(cerrarView -> {
                dialog.dismiss();
                volverAMain();
            });

            dialog.show();
        });

        // Volver siempre a Main
        btnInicio.setOnClickListener(v -> volverAMain());

        //CompraViewModel compraViewModel = new ViewModelProvider(this).get(CompraViewModel.class);

        compraViewModel.getHayNotificacionesNoLeidas().observe(this, hayNoLeidas -> {
            ImageView badge = findViewById(R.id.ic_notificacion);
            badge.setVisibility(hayNoLeidas ? View.VISIBLE : View.GONE);
        });

    }

    private void volverAMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}