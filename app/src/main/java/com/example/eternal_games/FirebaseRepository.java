package com.example.eternal_games;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseRepository {
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseUser obtenerUsuarioActual() {
        return auth.getCurrentUser();
    }

    public boolean estaLogueado() {
        return obtenerUsuarioActual() != null;
    }
    //Login
    public void login(String email, String password,
                      OnSuccessListener<FirebaseUser> success,
                      OnFailureListener failure) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) success.onSuccess(user);
                    else failure.onFailure(new Exception("Usuario no existe"));
                })
                .addOnFailureListener(failure);
    }

    public void cerrarSesion() {
        auth.signOut();
    }

    //Registrar Usuarioen forma tradicional
    public void registrarUsuario(String email, String password,
                                 OnSuccessListener<AuthResult> success,
                                 OnFailureListener failure) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }
    // agregar producto carrito
    public void agregarAlCarrito(String userId, String idProducto, int cantidad,
                                 OnSuccessListener<Void> success,
                                 OnFailureListener failure) {
        DocumentReference itemRef = db.collection("users")
                .document(userId)
                .collection("cart")
                .document(idProducto);

        Map<String, Object> item = new HashMap<>();
        item.put("idProducto", idProducto);
        item.put("cantidad", cantidad);
        item.put("fechaAgregado", FieldValue.serverTimestamp());

        itemRef.set(item, SetOptions.merge())
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }
    public String obtenerUserId() {
        FirebaseUser user = obtenerUsuarioActual();
        return user != null ? user.getUid() : null;
    }

    // obtener carrito del usuario
    public void obtenerCarritoUsuario(String userId,
                                      OnSuccessListener<List<CarritoItem>> success,
                                      OnFailureListener failure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<CarritoItem> carrito = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String idProducto = doc.getString("idProducto");
                        Long cantidad = doc.getLong("cantidad");

                        if (idProducto != null && cantidad != null) {
                            Producto producto = new Producto();
                            producto.id = idProducto; // aca mapear mas campos si neseistamos
                            carrito.add(new CarritoItem(producto, cantidad.intValue()));
                        }
                    }
                    success.onSuccess(carrito);
                })
                .addOnFailureListener(failure);
    }
}