package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.eternal_games.repository.FirebaseRepository;
import com.example.eternal_games.GoogleSignInManager;
import com.example.eternal_games.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseRepository repo;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;

    private EditText etUsuario, etContrasena;
    private TextView headerError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repo = new FirebaseRepository();

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        headerError = findViewById(R.id.headerError);

        Button btnLogin = findViewById(R.id.btnLogin);

        // Redirige a registro
        TextView linkRegistro = findViewById(R.id.linkRegistro);
        linkRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
        });

        // Login con email/contraseña
        btnLogin.setOnClickListener(v -> login());

        // Configuración Google Sign-In
        googleSignInClient = GoogleSignInManager.configurarGoogle(this);

        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                GoogleSignInManager.autenticarConFirebase(
                                        account.getIdToken(),
                                        FirebaseAuth.getInstance(),
                                        this,
                                        this::navegarAMain
                                );
                            }
                        } catch (ApiException e) {
                            mostrarError("Error al iniciar con Google");
                        }
                    }
                }
        );

        findViewById(R.id.btnLoginGoogle).setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });
    }

    private void login() {
        String email = etUsuario.getText().toString().trim();
        String password = etContrasena.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Completa todos los campos");
            return;
        }
        repo.login(email, password,
                user -> navegarAMain(),
                error -> mostrarError("Error: " + error.getMessage())
        );
    }

    // mostrar error en el la vista
    private void mostrarError(String mensaje) {
        headerError.setText(mensaje);
        headerError.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        headerError.setVisibility(TextView.VISIBLE);
    }

    // va al main activity
    private void navegarAMain() {
        headerError.setVisibility(TextView.GONE);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}