package com.example.eternal_games.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.eternal_games.R;
import com.example.eternal_games.adapter.ProductoCompraAdapter;
import com.example.eternal_games.viewmodel.CompraViewModel;
import android.widget.TextView;


public class CompraDetalleActivity extends AppCompatActivity {
    private CompraViewModel compraViewModel;
    private ProductoCompraAdapter detalleAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_compra);


        //boton de cierre
        findViewById(R.id.btnCerrar).setOnClickListener(v -> {
            finish(); // cierra el Activity y vuelve al anterior
        });
        String compraId = getIntent().getStringExtra("compraId");
        compraViewModel = new ViewModelProvider(this).get(CompraViewModel.class);


        // Configurar RecyclerView y Adapter
        RecyclerView recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        detalleAdapter = new ProductoCompraAdapter();
        recyclerProductos.setAdapter(detalleAdapter);


        // Observar la compra seleccionada
        compraViewModel.getCompraSeleccionada().observe(this, compra -> {
            if (compra != null) {
                ((TextView) findViewById(R.id.txtCompraId))
                        .setText("Compra #" + compra.id);


                ((TextView) findViewById(R.id.txtFecha))
                        .setText(compra.fecha.toString());


                ((TextView) findViewById(R.id.txtTotalGeneral))
                        .setText(getString(R.string.total_format, compra.totalGeneral));


                // Ubicación: mostrar dirección legible y abrir Maps al click
                TextView ubicacion = findViewById(R.id.txtUbicacionRetiro);
                String ubicacionLink = compra.ubicacionRetiro; // viene de Firestore
                Uri uri = Uri.parse(ubicacionLink);
                String direccion = uri.getQueryParameter("query");


                ubicacion.setText("Retirar en: " + direccion);
                ubicacion.setOnClickListener(v -> {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + direccion);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                });




                detalleAdapter.setProductos(compra.productos);
            }
        });


        // Cargar la compra desde el ViewModel
        compraViewModel.cargarCompra(compraId);
    }
}

