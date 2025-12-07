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

import com.example.eternal_games.repository.FirebaseRepository;
import com.example.eternal_games.adapter.ProductoAdapter;
import com.example.eternal_games.R;
import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.model.Producto;
import com.example.eternal_games.viewmodel.ProductoViewModel;
import com.example.eternal_games.viewmodel.SesionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

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
    private SesionViewModel sesionViewModel;
    private ProductoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración global de Firestore esto por si sigue trayendo cacheluego lo borramos
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false) // desactiva cache local
                .build();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);


        //repo = new FirebaseRepository();
        setContentView(R.layout.activity_main);
        recyclerProductos = findViewById(R.id.recyclerProductos);
        btnDemo = findViewById(R.id.btnDemo);
        badgeCantidad = findViewById(R.id.badgeCantidad);
        fabCarrito = findViewById(R.id.fabCarrito);
        btnDemo = findViewById(R.id.btnDemo);

        /// cerrar sesion (hay que mover esto para respetar arquitectura nueva)
        ImageButton btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        MenuCerrarSesion(btnCerrarSesion);

        sesionViewModel = new ViewModelProvider(this).get(SesionViewModel.class);

        // Observamos si el usuario está logueado
        sesionViewModel.getUsuarioLogueado().observe(this, logueado -> {
            if (Boolean.FALSE.equals(logueado)) {
                navegarAlLogin();
            } else if (Boolean.TRUE.equals(logueado)) {
                //inicializamos ProductoViewModel
                viewModel = new ViewModelProvider(this).get(ProductoViewModel.class);

                adapter = new ProductoAdapter(this, new ArrayList<>(), badgeCantidad, new ArrayList<>(),
                        producto -> viewModel.agregarAlCarrito(producto)
                );
                recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
                recyclerProductos.setAdapter(adapter);

                viewModel.getProductos().observe(this, productos -> adapter.setProductos(productos));
                viewModel.getCarrito().observe(this, carritoItems -> {
                    adapter.setCarrito(carritoItems);
                    actualizarBadge(calcularCantidadTotal(carritoItems));
                });
                viewModel.getMensajeToast().observe(this, mensaje ->
                        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                );

                viewModel.cargarDatosIniciales(); //con sesión válida
            }
        });

        // Observamos si se cerró la sesión
        sesionViewModel.getSesionCerrada().observe(this, cerrada -> {
            if (Boolean.TRUE.equals(cerrada)) {
                navegarAlLogin();
            }
        });

        //Disparamos la verificación de sesión
        sesionViewModel.verificarSesion();

        // Refactor para arquitectura (MVVM + Repository + Adapter) //
        // Botón flotante para abrir el carrito
        // preguntamso directo al livedata
        fabCarrito.setOnClickListener(v -> {
            List<CarritoItem> carritoActual = viewModel.getCarrito().getValue();
            if (carritoActual == null || carritoActual.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, CarritoActivity.class);
            intent.putExtra("carrito", new ArrayList<>(carritoActual));
            startActivityForResult(intent, 1001);
        });
        ///////////FIN DE REFACTOR///////////////////////////////
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

        // mail desde el ViewModel
        String mail = sesionViewModel.getUsuarioMail().getValue();
        if (mail != null) {
            MenuItem infoMail = popup.getMenu().add(mail);
            infoMail.setEnabled(false);
        }

        popup.getMenu().add("Cerrar sesión");

        popup.setOnMenuItemClickListener(item -> {
            if ("Cerrar sesión".equals(item.getTitle())) {
                sesionViewModel.cerrarSesion(); // delegamos al ViewModel
                return true;
            }
            return false;
        });

        popup.show();
    }

    /*private void cerrarSesion() {
        repo.cerrarSesion();
        navegarAlLogin();
    }*/

    private void navegarAlLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

}