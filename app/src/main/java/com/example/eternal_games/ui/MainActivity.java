package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eternal_games.R;
import com.example.eternal_games.adapter.ProductoAdapter;
import com.example.eternal_games.viewmodel.ProductoViewModel;
import com.example.eternal_games.viewmodel.SesionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerProductos;
    private Button btnDemo;
    private TextView badgeCantidad;
    private FloatingActionButton fabCarrito;

    private ProductoAdapter adapter;
    private SesionViewModel sesionViewModel;
    private ProductoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        btnDemo = findViewById(R.id.btnDemo);
        badgeCantidad = findViewById(R.id.badgeCantidad);
        fabCarrito = findViewById(R.id.fabCarrito);

        ImageButton btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        MenuCerrarSesion(btnCerrarSesion);

        ImageButton btnContacto = findViewById(R.id.btnContacto);

        btnContacto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompraActivity.class);
            startActivity(intent);
        });


        sesionViewModel = new ViewModelProvider(this).get(SesionViewModel.class);

        sesionViewModel.getUsuarioLogueado().observe(this, logueado -> {
            if (Boolean.FALSE.equals(logueado)) {
                navegarAlLogin();
                return;
            }

            // Inicializamos ViewModel
            viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

            adapter = new ProductoAdapter(
                    this,
                    new ArrayList<>(),
                    badgeCantidad,
                    new ArrayList<>(),
                    producto -> viewModel.agregarAlCarrito(producto)
            );

            recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerProductos.setAdapter(adapter);

            // Productos
            viewModel.getProductos().observe(this, productos ->
                    adapter.setProductos(productos)
            );

            // Carrito compartido → badge
            viewModel.getCarrito().observe(this, carritoItems -> {
                adapter.setCarrito(carritoItems);
                actualizarBadge(viewModel.calcularCantidadTotal(carritoItems));
            });

            // Toast
            viewModel.getMensajeToast().observe(this, mensaje ->
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            );

            //Cargas iniciales (productos + carrito)
            viewModel.inicializarDatos();

        });

        sesionViewModel.getSesionCerrada().observe(this, cerrada -> {
            if (Boolean.TRUE.equals(cerrada)) {
                navegarAlLogin();
            }
        });

        sesionViewModel.verificarSesion();

        btnDemo.setOnClickListener(v -> viewModel.insertarProductosDemo());

        // Abrir carrito
        fabCarrito.setOnClickListener(v -> {
            startActivity(new Intent(this, CarritoActivity.class));
        });
    }

    private void actualizarBadge(int cantidad) {
        if (cantidad > 0) {
            badgeCantidad.setText(String.valueOf(cantidad));
            badgeCantidad.setVisibility(TextView.VISIBLE);
        } else {
            badgeCantidad.setVisibility(TextView.GONE);
        }
    }

    private void MenuCerrarSesion(ImageButton btnCerrarSesion) {
        btnCerrarSesion.setOnClickListener(this::mostrarMenuCerrarSesion);
    }

    private void mostrarMenuCerrarSesion(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);

        String mail = sesionViewModel.getUsuarioMail().getValue();
        if (mail != null) {
            MenuItem infoMail = popup.getMenu().add(mail);
            infoMail.setEnabled(false);
        }

        popup.getMenu().add("Cerrar sesión");

        popup.setOnMenuItemClickListener(item -> {
            if ("Cerrar sesión".equals(item.getTitle())) {
                sesionViewModel.cerrarSesion();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void navegarAlLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
