package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eternal_games.R;
import com.example.eternal_games.adapter.CompraAdapter;
import com.example.eternal_games.model.Compra;
import com.example.eternal_games.viewmodel.CompraViewModel;

public class CompraActivity extends AppCompatActivity {

    private CompraViewModel compraViewModel;
    private CompraAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra);
        ImageButton btnInicio = findViewById(R.id.btnInicio);

        RecyclerView recyclerView = findViewById(R.id.recyclerCompras);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnContacto = findViewById(R.id.btnContacto);

        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(CompraActivity.this, CompraActivity.class);
            startActivity(intent);
        });

        btnInicio.setOnClickListener(v -> volverAMain());

        adapter = new CompraAdapter();
        recyclerView.setAdapter(adapter);
        compraViewModel = new ViewModelProvider(this).get(CompraViewModel.class);
        //marca como leido
        adapter.setOnItemClick(compra -> {
            compraViewModel.marcarComoLeido(compra.id, true);
            // 2. Reflejar inmediatamente en la UI
            compra.leido = true;
            int position = adapter.getCompras().indexOf(compra);
            if (position != -1) {
                adapter.notifyItemChanged(position);
            }
            // Abrir detalle como modal
            Intent intent = new Intent(this, CompraDetalleActivity.class);
            intent.putExtra("compraId", compra.id);
            startActivity(intent);
        });
        // Observar compras
        compraViewModel.getCompras().observe(this, compras -> {
            adapter.setCompras(compras);
        });

        // Cargar compras desde Firebase
        compraViewModel.cargarCompras();

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
