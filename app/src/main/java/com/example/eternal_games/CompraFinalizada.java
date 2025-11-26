package com.example.eternal_games;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CompraFinalizada extends DialogFragment {
    private int total;
    private int cantidad;

    public CompraFinalizada(int total, int cantidad) {
        this.total = total;
        this.cantidad = cantidad;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.compra_finalizada, null);

        TextView txtResumen = view.findViewById(R.id.txtResumen);
        txtResumen.setText("Total: $" + total + "\nCantidad: " + cantidad);

        Button btnCerrar = view.findViewById(R.id.btnCerrar);
        btnCerrar.setOnClickListener(v -> {
            // Cerramos msg
            dismiss();
            // volvemos a home MainActivity
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        builder.setView(view);
        return builder.create();
    }
}