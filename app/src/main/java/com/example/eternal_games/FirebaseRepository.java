package com.example.eternal_games;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
}