package com.example.eternal_games;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerProductos;
    private Button btnDemo;
    private TextView badgeCantidad;
    private FloatingActionButton fabCarrito;
    private List<Producto> productos = new ArrayList<>();
    private List<CarritoItem> carrito = new ArrayList<>();
    private ProductoAdapter adapter;
    private FirebaseRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Si no hay usuario logueado, redirigimos al login
        repo = new FirebaseRepository(); //intanciamso fiberbase repo para manejo de bd
        if (!repo.estaLogueado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        btnDemo = findViewById(R.id.btnDemo);
        badgeCantidad = findViewById(R.id.badgeCantidad);
        fabCarrito = findViewById(R.id.fabCarrito);
        /// cerrar sesion
        ImageButton btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        MenuCerrarSesion(btnCerrarSesion);

        // Cargar productos desde el repositorio
        //productos.addAll(ProductoRepository.cargarProductos(this));

        // Configurar RecyclerView con adapter que actualiza el badge y el carrito
        //adapter = new ProductoAdapter(this, productos, badgeCantidad, carrito);
        //recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
        //recyclerProductos.setAdapter(adapter);

        //Se cambio el metodo ahora cargar productos desde el Firebase con su carrito
        String userId = repo.obtenerUserId();
        cargarDatosIniciales(userId);
        // Cargar productos desde Firebase
        //ProductoRepository.cargarDesdeFirebase(this, productos -> {
        //    this.productos.addAll(productos);
        // Pasar repo y userId al adapter
        //    adapter = new ProductoAdapter(
        //            this, this.productos, badgeCantidad, carrito, repo, userId
        //    );
        //    recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
        //    recyclerProductos.setAdapter(adapter);
        //});


        // Botón para agregar productos demo desde JSON --> quedo inactivo luego vemso que hacemos
        //btnDemo.setOnClickListener(v -> {
        //    productos.addAll(ProductoRepository.obtenerProductosDemo());
        //    adapter.notifyDataSetChanged();
        //    Toast.makeText(this, "Productos demo cargados.", Toast.LENGTH_SHORT).show();
        //});

        // Botón flotante para abrir el carrito
        fabCarrito.setOnClickListener(v -> {
            if (carrito.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Intent intent = new Intent(this, CarritoActivity.class);
            //intent.putExtra("carrito", new ArrayList<>(carrito)); // Serializable
            //startActivity(intent);
            //CAMBIAMOS STARACTIVITY POR STARACTIVITYRESULT PARA VER RESULTADO ELEGUIMSO 1001
            Intent intent = new Intent(this, CarritoActivity.class);
            intent.putExtra("carrito", new ArrayList<>(carrito)); // Serializable
            startActivityForResult(intent, 1001);
        });

        // Inicializar badge en 0 (oculto)
        actualizarBadge(0);
    }

    public void actualizarBadge(int cantidad) {
        if (badgeCantidad != null) {
            if (cantidad > 0) {
                badgeCantidad.setText(String.valueOf(cantidad));
                badgeCantidad.setVisibility(TextView.VISIBLE);
            } else {
                badgeCantidad.setVisibility(TextView.GONE);
            }
        }
    }

    //NUEVO METODO AL MOMENTO DE VOLVER AL ACTIVITY VERIFICA PARA ACTUALIZAR BADGE
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            ArrayList<CarritoItem> carritoActualizado =
                    (ArrayList<CarritoItem>) data.getSerializableExtra("carritoActualizado");

            if (carritoActualizado != null) {
                carrito.clear();
                carrito.addAll(carritoActualizado);
                actualizarBadge(calcularCantidadTotal(carrito));
            }
        }
    }
    private int calcularCantidadTotal(List<CarritoItem> carrito) {
        int total = 0;
        for (CarritoItem item : carrito) {
            total += item.cantidad;
        }
        return total;
    }

    private void MenuCerrarSesion(ImageButton btnCerrarSesion) {
        btnCerrarSesion.setOnClickListener(v -> mostrarMenuCerrarSesion(v));
    }

    /// delegamos la logica aca luego vemso que hacemos
    private void mostrarMenuCerrarSesion(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenu().add("Cerrar sesión");

        popup.setOnMenuItemClickListener(item -> {
            if ("Cerrar sesión".equals(item.getTitle())) {
                cerrarSesion();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void cerrarSesion() {
        repo.cerrarSesion();
        navegarAlLogin();
    }

    private void navegarAlLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void cargarDatosIniciales(String userId) {
        ProductoRepository.cargarDesdeFirebase(this, productos -> {
            this.productos.clear();
            this.productos.addAll(productos);

            repo.obtenerCarritoUsuario(userId,
                    carritoItems -> {
                        carrito.clear();
                        List<CarritoItem> carritoConDetalles = new ArrayList<>();

                        for (CarritoItem item : carritoItems) {
                            for (Producto p : productos) {
                                if (p.id.equals(item.producto.id)) {
                                    carritoConDetalles.add(new CarritoItem(p, item.cantidad));
                                    break;
                                }
                            }
                        }

                        carrito.addAll(carritoConDetalles);
                        actualizarBadge(calcularCantidadTotal(carrito));

                        adapter = new ProductoAdapter(this, productos, badgeCantidad, carrito, repo, userId);
                        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
                        recyclerProductos.setAdapter(adapter);
                    },
                    e -> {
                        Log.e("Carrito", "Error al cargar carrito", e);
                        Toast.makeText(this, "Error al cargar el carrito", Toast.LENGTH_SHORT).show();
                    }
            );
        });
    }
}