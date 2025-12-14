package com.example.eternal_games.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.model.Producto;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartRepository {

    private static CartRepository instance;

    private final FirebaseRepository repo = new FirebaseRepository();
    private final MutableLiveData<List<CarritoItem>> carrito =
            new MutableLiveData<>(new ArrayList<>());

    private boolean cargado = false;

    public static synchronized CartRepository getInstance() {
        if (instance == null) instance = new CartRepository();
        return instance;
    }

    public LiveData<List<CarritoItem>> getCarrito() {
        return carrito;
    }

    public void setCarrito(List<CarritoItem> items) {
        carrito.setValue(new ArrayList<>(items));
    }

    public void cargarDesdeFirebaseSiHaceFalta() {
        if (cargado) return;
        cargado = true;

        String userId = repo.obtenerUserId();
        repo.obtenerCarritoUsuario(userId, carritoItems -> {
            // REEMPLAZA el carrito en memoria
            carrito.setValue(new ArrayList<>(carritoItems));
        }, e -> {
            // manejar error
        });
    }


    public void agregar(Producto producto) {
        String uid = repo.obtenerUserId();

        List<CarritoItem> actual = carrito.getValue();
        if (actual == null) actual = new ArrayList<>();

        boolean existe = false;
        for (CarritoItem item : actual) {
            if (item.producto.id.equals(producto.id)) {
                item.cantidad++;
                existe = true;
                repo.agregarAlCarrito(uid, producto.id, item.cantidad, aVoid -> {}, e -> {});
                break;
            }
        }

        if (!existe) {
            actual.add(new CarritoItem(producto, 1));
            repo.agregarAlCarrito(uid, producto.id, 1, aVoid -> {}, e -> {});
        }

        carrito.setValue(new ArrayList<>(actual)); //UI instantánea
    }

    public void eliminar(CarritoItem item) {
        String uid = repo.obtenerUserId();

        repo.eliminarDelCarrito(uid, item.producto.id,
                aVoid -> {
                    List<CarritoItem> actual = carrito.getValue();
                    if (actual == null) actual = new ArrayList<>();
                    List<CarritoItem> copia = new ArrayList<>(actual);
                    copia.remove(item);
                    carrito.setValue(copia); // UI instantánea
                },
                e -> {}
        );
    }

    public void vaciar() {
        carrito.setValue(new ArrayList<>());
    }

    public void reset() {
        cargado = false;
        vaciar();
    }

    public void cargarRawDesdeFirebase(java.util.function.Consumer<List<CarritoItem>> onLoaded) {
        if (cargado) {
            onLoaded.accept(carrito.getValue());
            return;
        }
        cargado = true;

        String userId = repo.obtenerUserId();
        repo.obtenerCarritoUsuario(userId,
                carritoItems -> {
                    carrito.setValue(new ArrayList<>(carritoItems)); // raw (solo id + cantidad)
                    onLoaded.accept(carritoItems);
                },
                e -> onLoaded.accept(new ArrayList<>())
        );
    }

}