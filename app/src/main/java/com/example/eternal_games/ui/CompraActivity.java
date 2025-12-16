package com.example.eternal_games.ui;
import android.content.Intent;
import android.os.Bundle;
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
        RecyclerView recyclerView = findViewById(R.id.recyclerCompras);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CompraAdapter();
        recyclerView.setAdapter(adapter);
        compraViewModel = new ViewModelProvider(this).get(CompraViewModel.class);
        //marca como leido
        adapter.setOnItemClick(compra -> {
            compraViewModel.marcarComoLeido(compra.id, true);
            //Reflejar inmediatamente en la UI
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
    }
}