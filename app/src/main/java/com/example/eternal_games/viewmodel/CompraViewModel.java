package com.example.eternal_games.viewmodel;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

    // LiveData para una compra seleccionada
    private final MutableLiveData<Compra> compraSeleccionada = new MutableLiveData<>();
    public LiveData<Compra> getCompraSeleccionada() {
        return compraSeleccionada;
    }

    // Cargar todas las compras del usuario desde Firebase
    public void cargarCompras() {
        repo.cargarCompras();
    }

    // Registrar una nueva compra
    public void registrarCompra(List<CarritoItem> items) {
        repo.registrarCompra(items);
    }

    // Actualizar campo 'leido'
    public void marcarComoLeido(String compraId, boolean leido) {
        repo.marcarComoLeido(compraId, leido);
    }

    // Cargar una compra específica
    public void cargarCompra(String compraId) {
        repo.getCompra(compraId,
                compraSeleccionada::setValue,
                e -> Log.e("ViewModel", "Error al cargar compra", e));
    }
}
