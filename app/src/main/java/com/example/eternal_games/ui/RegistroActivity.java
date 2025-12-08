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
import androidx.lifecycle.ViewModelProvider;

import com.example.eternal_games.GoogleSignInManager;
import com.example.eternal_games.R;
import com.example.eternal_games.viewmodel.SesionViewModel;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister;
    private TextView headerError;

    private SesionViewModel sesionViewModel;
    private GoogleSignInClient googleClient;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Usamos SesionViewModel
        sesionViewModel = new ViewModelProvider(this).get(SesionViewModel.class);
        googleClient = GoogleSignInManager.configurarGoogle(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        headerError = findViewById(R.id.headerError);

        // Registro con email/contraseña
        btnRegister.setOnClickListener(v ->
                sesionViewModel.registrarUsuario(
                        etEmail.getText().toString().trim(),
                        etPassword.getText().toString().trim()
                )
        );

        // Observamos resultados del registro
        sesionViewModel.getRegistroExitoso().observe(this, exitoso -> {
            if (Boolean.TRUE.equals(exitoso)) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                navegarAMain();
            }
        });

        sesionViewModel.getErrorMensaje().observe(this, mensaje -> {
            if (mensaje != null) {
                headerError.setText(mensaje);
                headerError.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                headerError.setVisibility(TextView.VISIBLE);
            }
        });

        // Google Sign-In
        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                sesionViewModel.loginConGoogle(
                                        account.getIdToken(),
                                        FirebaseAuth.getInstance(),
                                        this
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

        // Observamos si el login con Google fue exitoso
        sesionViewModel.getUsuarioLogueado().observe(this, logueado -> {
            if (Boolean.TRUE.equals(logueado)) {
                navegarAMain();
            }
        });
    }

    private void mostrarError(String mensaje) {
        headerError.setText(mensaje);
        headerError.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        headerError.setVisibility(TextView.VISIBLE);
    }

    private void navegarAMain() {
        headerError.setVisibility(TextView.GONE);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}