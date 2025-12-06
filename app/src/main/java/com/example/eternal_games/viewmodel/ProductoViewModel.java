package com.example.eternal_games.viewmodel;

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

    public LiveData<List<Producto>> getProductos() {
        return productos;
    }

    public LiveData<List<CarritoItem>> getCarrito() {
        return carrito;
    }

    public void cargarDatosIniciales(String userId) {
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
}