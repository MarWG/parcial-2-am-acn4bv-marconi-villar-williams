package com.example.eternal_games;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
//import de firebase
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProductoRepository {

    public static List<Producto> cargarProductos(Context context) {
        List<Producto> lista = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("productos.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Producto producto = new Producto();

                //producto.id = obj.getInt("id");
                producto.title = obj.getString("title");
                producto.description = obj.getString("description");
                producto.price = obj.getInt("price");
                //producto.code = obj.getString("code");
                producto.status = obj.getBoolean("status");
                producto.platform = obj.optString("platform", "");
                producto.topSell = obj.optBoolean("topSell", false);
                producto.genre = obj.optString("genre", "");
                producto.category = obj.optString("category", "");

                // Obtener el String de la imagen del JSON
                String imgString = obj.getString("img");

                String nombreImagenLimpio = imgString
                        .replace("../images/", "")
                        .replace(".jpg", "")
                        .replace(".png", "")
                        .replace(".jpeg", "")
                        .replace("/", "_")
                        .toLowerCase();

                // Obtener el ID del recurso (R.drawable.nombre)
                int resId = context.getResources().getIdentifier(nombreImagenLimpio, "drawable", context.getPackageName());

                // Asignar el ID de recurso (int) a producto.img
                if (resId != 0) {
                    producto.img = resId;
                } else {
                    // Usar una imagen por defecto si no se encuentra
                    producto.img = R.drawable.imagen_no_disponible;
                }

                lista.add(producto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public static void obtenerProductosDemo() {
        FirebaseRepository repo = new FirebaseRepository();

        List<Producto> demo = new ArrayList<>();

        Producto p1 = new Producto();
        p1.title = "Zelda";
        p1.description = "Mundo Abierto";
        p1.price = 4999;
        p1.status = true;
        p1.platform = "Nintendo";
        p1.topSell = true;
        p1.genre = "RPG";
        p1.category = "Aventura";
        p1.imgUrl = "https://tse3.mm.bing.net/th/id/OIP.YKkOg7bMSJtlO0zNdsPB2gHaLr?w=1459&h=2300&rs=1&pid=ImgDetMain&o=7&rm=3";
        demo.add(p1);

        Producto p2 = new Producto();
        p2.title = "Zelda 2";
        p2.description = "Mundo Abierto";
        p2.price = 3999;
        p2.status = true;
        p2.platform = "Nintendo";
        p2.topSell = false;
        p2.genre = "RPG";
        p2.category = "Acción";
        p2.imgUrl = "https://tse4.mm.bing.net/th/id/OIP.JbKS0l6JbnRqVv950eZBwwHaJT?rs=1&pid=ImgDetMain&o=7&rm=3";
        demo.add(p2);

        // Insertamos todos en Firebase
        for (Producto p : demo) {
            repo.insertarProducto(p,
                    aVoid -> {}, //maneja el activity exito o errore
                    e -> {}
            );
        }
    }


    public static void cargarDesdeFirebase(Context context, Consumer<List<Producto>> callback) {
        //Log.d("Firebase", "LLEGA: ");
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //refactr usamos repositorio defiberebase qu egeneramos
        FirebaseRepository repo = new FirebaseRepository();
        repo.obtenerProductos(querySnapshot -> {
            List<Producto> lista = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Producto producto = new Producto();

                producto.id = doc.getId();
                producto.title = doc.getString("title");
                //Log.d("Firebase", "producto leido: " + doc.getString("title"));
                producto.description = doc.getString("description");
                producto.price = doc.getLong("price").intValue();
                producto.status = doc.getBoolean("status");
                producto.platform = doc.getString("platform");
                producto.topSell = doc.getBoolean("topSell");
                producto.genre = doc.getString("genre");
                producto.category = doc.getString("category");

                Log.d("Firebase", "producto leido: " + doc.getString("title"));
                // URL de la imagen
                String imgUrl = doc.getString("img");
                producto.imgUrl = (imgUrl != null && !imgUrl.isEmpty()) ? imgUrl : null;

                // sto porsi no hay URL, usamos imagen por defecto ya cargada
                producto.img = R.drawable.imagen_no_disponible;

                lista.add(producto);

            }
            callback.accept(lista);
        }, e -> {
            e.printStackTrace();
            callback.accept(new ArrayList<>());
        });

    }
}