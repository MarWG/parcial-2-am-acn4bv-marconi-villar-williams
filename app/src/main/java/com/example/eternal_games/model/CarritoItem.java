package com.example.eternal_games.model;

import java.io.Serializable;

public class CarritoItem implements Serializable {
    public Producto producto;
    public int cantidad;

    public CarritoItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return producto.price * cantidad;
    }
}