package com.example.eternal_games.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.model.Compra;
import com.example.eternal_games.repository.CompraRepository;

import java.util.List;

public class CompraViewModel extends ViewModel {

    private final CompraRepository repo = CompraRepository.getInstance();

    // LiveData que expone la lista de compras
    public LiveData<List<Compra>> getCompras() {
        return repo.getCompras();
    }

    // Cargar todas las compras del usuario desde Firebase
    public void cargarCompras() {
        repo.cargarCompras();
    }

    //Registrar una nueva compra
    public void registrarCompra(List<CarritoItem> items) {
        repo.registrarCompra(items);
    }

}
