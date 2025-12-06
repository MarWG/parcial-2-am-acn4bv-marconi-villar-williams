package com.example.eternal_games.viewmodel;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.model.Producto;
import com.example.eternal_games.repository.FirebaseRepository;
import com.example.eternal_games.repository.ProductoRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductoViewModel extends ViewModel {

    private MutableLiveData<List<Producto>> productos = new MutableLiveData<>();
    private MutableLiveData<List<CarritoItem>> carrito = new MutableLiveData<>();
    private FirebaseRepository repo = new FirebaseRepository();
    private MutableLiveData<String> mensajeToast = new MutableLiveData<>();

    public LiveData<List<Producto>> getProductos() {
        return productos;
    }

    public LiveData<List<CarritoItem>> getCarrito() {
        return carrito;
    }
    public LiveData<String> getMensajeToast() {
        return mensajeToast;
    }

    public void cargarDatosIniciales() {
        String userId = repo.obtenerUserId();
        ProductoRepository.cargarDesdeFirebase(null, productosList -> {
            productos.setValue(productosList);

            repo.obtenerCarritoUsuario(userId, carritoItems -> {
                List<CarritoItem> carritoConDetalles = new ArrayList<>();

                for (CarritoItem item : carritoItems) {
                    for (Producto p : productosList) {
                        if (p.id.equals(item.producto.id)) {
                            carritoConDetalles.add(new CarritoItem(p, item.cantidad));
                            break;
                        }
                    }
                }
                carrito.setValue(carritoConDetalles);
            }, e -> {
                // Manejo de error
            });
        });
    }

    public void agregarAlCarrito(Producto producto) {
        String uid =  repo.obtenerUserId();

        List<CarritoItem> carritoActual = carrito.getValue();
        if (carritoActual == null) carritoActual = new ArrayList<>();

        boolean yaExiste = false;
        for (CarritoItem item : carritoActual) {
            if (item.producto.id.equals(producto.id)) {
                item.cantidad++;
                yaExiste = true;
                repo.agregarAlCarrito(uid, producto.id, item.cantidad,
                        aVoid -> {}, e -> {});
                break;
            }
        }

        if (!yaExiste) {
            carritoActual.add(new CarritoItem(producto, 1));
            repo.agregarAlCarrito(uid, producto.id, 1,
                    aVoid -> {}, e -> {});
        }
        carrito.setValue(new ArrayList<>(carritoActual)); // refresca LiveData
        // Emitir mensaje para la UI
        mensajeToast.setValue(producto.title + " agregado al carrito");

    }

}