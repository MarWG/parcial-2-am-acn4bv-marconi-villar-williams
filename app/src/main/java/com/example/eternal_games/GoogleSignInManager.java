package com.example.eternal_games;

import android.app.Activity;
import android.widget.Toast;
import android.content.Context;

import com.example.eternal_games.repository.FirebaseRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInManager {

    public static GoogleSignInClient configurarGoogle(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    public static void autenticarConFirebase(String idToken, FirebaseAuth firebaseAuth, Context context, Runnable onSuccess) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // Sincronizar con Firestore
                            FirebaseRepository repo = new FirebaseRepository();
                            repo.crearDocumentoUsuario(user,
                                    aVoid -> {
                                        Toast.makeText(context, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                        onSuccess.run();
                                    },
                                    error -> Toast.makeText(context, "Error Firestore: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        } else {
                            Toast.makeText(context, "Error: usuario nulo", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Error en autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}