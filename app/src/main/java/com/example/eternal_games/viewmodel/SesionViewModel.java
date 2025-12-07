package com.example.eternal_games.viewmodel;

import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eternal_games.repository.FirebaseRepository;

public class SesionViewModel extends ViewModel {

    private FirebaseRepository repo = new FirebaseRepository();

    private MutableLiveData<Boolean> usuarioLogueado = new MutableLiveData<>();
    private MutableLiveData<String> usuarioMail = new MutableLiveData<>();
    private MutableLiveData<Boolean> sesionCerrada = new MutableLiveData<>();

    public LiveData<Boolean> getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public LiveData<String> getUsuarioMail() {
        return usuarioMail;
    }

    public LiveData<Boolean> getSesionCerrada() {
        return sesionCerrada;
    }

    // Verifica si hay sesión activa
    public void verificarSesion() {
        usuarioLogueado.setValue(repo.estaLogueado());
        if (repo.estaLogueado()) {
            usuarioMail.setValue(repo.obtenerMailActual());
        }
    }

    // Cierra sesión
    public void cerrarSesion() {
        repo.cerrarSesion();
        sesionCerrada.setValue(true);
    }

}