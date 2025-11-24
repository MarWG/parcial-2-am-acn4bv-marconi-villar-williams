package com.example.eternal_games;
import java.util.List;

//USAMOS INTERFACE PARA ACTUALIZAR EL ACTIVITY DEL CARRITO
public interface CarritoCallback {
    void onCarritoActualizado(List<CarritoItem> nuevoCarrito);

    void onProductoEliminado(CarritoItem eliminado);
}