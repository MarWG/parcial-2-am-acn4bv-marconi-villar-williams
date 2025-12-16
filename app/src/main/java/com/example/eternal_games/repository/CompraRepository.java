package com.example.eternal_games.repository;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.eternal_games.R;
import com.example.eternal_games.model.CarritoItem;
import com.example.eternal_games.model.Compra;
import com.example.eternal_games.model.Producto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CompraRepository {
    private static CompraRepository instance;
    private final FirebaseRepository repo = new FirebaseRepository();
    private final MutableLiveData<List<Compra>> compras =
            new MutableLiveData<>(new ArrayList<>());
    private boolean cargado = false;

    public static synchronized CompraRepository getInstance() {
        if (instance == null) instance = new CompraRepository();
        return instance;
    }

    public LiveData<List<Compra>> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> lista) {
        compras.setValue(new ArrayList<>(lista));
    }

    public void registrarCompra(List<CarritoItem> items) {
        if (items == null || items.isEmpty()) return;
        //encabezado
        Map<String, Object> compra = new HashMap<>();
        compra.put("fecha", FieldValue.serverTimestamp());
        compra.put("leido", false);
        String direccion = "Av. Corrientes 1234, CABA";
        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(direccion);
        compra.put("ubicacionRetiro", mapsUrl);
        //detalle
        List<Map<String, Object>> productos = new ArrayList<>();
        int cantidadTotal = 0;
        double totalGeneral = 0.0;
        for (CarritoItem item : new ArrayList<>(items)) {
            Map<String, Object> p = new HashMap<>();
            p.put("idProducto", item.producto.id);
            p.put("title", item.producto.title);
            p.put("price", item.producto.price);
            p.put("cantidad", item.cantidad);
           /*p.put("img", item.producto.imgUrl);
           p.put("idProducto", item.producto.id);
           p.put("titulo", item.producto.title);
           p.put("cantidad", item.cantidad);
           p.put("precioUnitario", item.producto.price);*/
            productos.add(p);
            // Calcular totales
            cantidadTotal += item.cantidad;
            totalGeneral += item.cantidad * item.producto.price;

        }
        compra.put("productos", productos);
        compra.put("cantidadTotal", cantidadTotal);
        compra.put("totalGeneral", totalGeneral);
        //Delegar en FirebaseRepository
        repo.insertarCompra(compra,
                aVoid -> {},   // éxito sin acción
                e -> {}    // error sin acción
        );
    }

    public void cargarCompras() {
        String userId = repo.obtenerUserId();
        if (userId == null) return;
        repo.obtenerComprasUsuario(userId, lista -> {
            compras.setValue(new ArrayList<>(lista)); // reemplaza en memoria
        }, e -> {
            // manejo de error
        });
    }

    private Compra construirCompra(List<CarritoItem> items) {
        Compra compra = new Compra();
        compra.fecha = new Date();
        compra.leido = false;
        compra.ubicacionRetiro = "Av. Corrientes 1234, CABA";
        compra.productos = new ArrayList<>();
        int cantidadTotal = 0;
        double totalGeneral = 0.0;
        for (CarritoItem item : items) {
            Producto p = item.producto;
            p.cantidad = item.cantidad;
            compra.productos.add(p);
            cantidadTotal += item.cantidad;
            totalGeneral += item.cantidad * item.producto.price;
        }

        compra.cantidadTotal = cantidadTotal;
        compra.totalGeneral = totalGeneral;
        return compra;
    }

    public void marcarComoLeido(String compraId, boolean leido) {
        repo.actualizarLeido(compraId, leido);
    }

    public void getCompra(String compraId,
                          OnSuccessListener<Compra> success,
                          OnFailureListener failure) {
        repo.obtenerCompraPorId(compraId, (DocumentSnapshot doc) -> {
            if (doc != null && doc.exists()) {
                Compra compra = new Compra();
                compra.id = doc.getId();
                compra.fecha = doc.getDate("fecha");
                compra.leido = doc.getBoolean("leido");
                compra.ubicacionRetiro = doc.getString("ubicacionRetiro");
                compra.totalGeneral = doc.getDouble("totalGeneral");
                compra.cantidadTotal = doc.getLong("cantidadTotal").intValue();
                List<Map<String, Object>> productosDocs = (List<Map<String, Object>>) doc.get("productos");
                List<Producto> productos = new ArrayList<>();
                if (productosDocs != null) {
                    AtomicInteger pendientes = new AtomicInteger(productosDocs.size());
                    for (Map<String, Object> pDoc : productosDocs) {
                        Producto p = new Producto();
                        p.title = (String) pDoc.get("title");
                        p.price = ((Number) pDoc.get("price")).intValue();
                        p.cantidad = ((Number) pDoc.get("cantidad")).intValue();
                        p.img = R.drawable.imagen_no_disponible;
                        String idProducto = (String) pDoc.get("idProducto");
                        //Log.d("Firebase", "producto leido: " + idProducto);
                        repo.obtenerProductoPorId(idProducto, productoDoc -> {
                            if (productoDoc != null && productoDoc.exists()) {
                                p.imgUrl = productoDoc.getString("img");
                            }
                            productos.add(p);
                            if (pendientes.decrementAndGet() == 0) {
                                compra.productos = productos;
                                success.onSuccess(compra);
                            }
                        }, e -> {
                            productos.add(p);
                            if (pendientes.decrementAndGet() == 0) {
                                compra.productos = productos;
                                success.onSuccess(compra);
                            }
                        });
                    }
                }
            } else {
                failure.onFailure(new Exception("Compra no encontrada"));
            }
        }, failure);
    }
}
