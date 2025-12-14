package com.example.eternal_games.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.repository.CartRepository;
import com.example.eternal_games.repository.CompraRepository;

import java.util.ArrayList;
import java.util.List;

public class CarritoViewModel extends ViewModel {

    private final CartRepository cartRepo = CartRepository.getInstance();

    private final CompraViewModel compraViewModel;

    // Carrito compartido (mismo para todas las pantallas)
    private final LiveData<List<CarritoItem>> carrito = cartRepo.getCarrito();

    // Totales calculados en base al carrito (se actualizan solos)
    private final MediatorLiveData<Integer> cantidadTotal = new MediatorLiveData<>();
    private final MediatorLiveData<Double> totalGeneral = new MediatorLiveData<>();

    public CarritoViewModel() {
        this.compraViewModel = new CompraViewModel(); //traemos el viwmodel de compra
        // cada vez que cambia el carrito, recalculamos
        cantidadTotal.addSource(carrito, items -> cantidadTotal.setValue(calcularCantidad(items)));
        totalGeneral.addSource(carrito, items -> totalGeneral.setValue(calcularTotal(items)));

        // valores iniciales
        cantidadTotal.setValue(0);
        totalGeneral.setValue(0.0);
    }

    public LiveData<List<CarritoItem>> getCarrito() {
        return carrito;
    }

    public LiveData<Integer> getCantidadTotal() {
        return cantidadTotal;
    }

    public LiveData<Double> getTotalGeneral() {
        return totalGeneral;
    }

    // Eliminar del carrito (repo actualiza memoria + Firebase)
    public void eliminarProducto(CarritoItem item) {
        if (item == null) return;
        cartRepo.eliminar(item);
    }

    // Finalizar compra: borrar todos (Firebase) y vaciar memoria
    public void finalizarCompra() {
        List<CarritoItem> items = carrito.getValue();
        if (items == null || items.isEmpty()) return;
        compraViewModel.registrarCompra(items);
        for (CarritoItem item : new ArrayList<>(items)) {
            cartRepo.eliminar(item);
        }
        cartRepo.vaciar();
    }

    private int calcularCantidad(List<CarritoItem> items) {
        int cantidad = 0;
        if (items != null) {
            for (CarritoItem item : items) {
                cantidad += item.cantidad;
            }
        }
        return cantidad;
    }

    private double calcularTotal(List<CarritoItem> items) {
        double total = 0.0;
        if (items != null) {
            for (CarritoItem item : items) {
                total += item.getTotal();
            }
        }
        return total;
    }
}
