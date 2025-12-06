package com.example.eternal_games.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegistroActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister;
    private TextView headerError;

    private FirebaseRepository repo;
    private GoogleSignInClient googleClient;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        repo = new FirebaseRepository();
        googleClient = GoogleSignInManager.configurarGoogle(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        headerError = findViewById(R.id.headerError);

        btnRegister.setOnClickListener(v -> registrarUsuario());

        // Google Sign-In
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
            Intent signInIntent = googleClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });
    }

    //Registramos Usuarios con validacionees basicas
    private void registrarUsuario() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarError("Completa todos los campos");
            return;
        }

        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        repo.registrarUsuario(email, password,
                authResult -> {
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                    navegarAMain();
                },
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