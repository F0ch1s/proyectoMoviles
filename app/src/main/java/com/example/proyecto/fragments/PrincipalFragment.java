package com.example.proyecto.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto.R;
import com.example.proyecto.data.FirestoreService;
import com.example.proyecto.ui.login.CrearEquipoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PrincipalFragment extends Fragment {

    private TextView textSaludo;
    private Button btnCrearEquipo, btnUnirseEquipo;

    public PrincipalFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_principal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enlazar vistas
        textSaludo = view.findViewById(R.id.textSaludo);
        btnCrearEquipo = view.findViewById(R.id.btnCrearEquipo);
        btnUnirseEquipo = view.findViewById(R.id.btnUnirseEquipo);

        // Mostrar saludo personalizado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String nombre = user.getDisplayName();
            textSaludo.setText("Hola, " + (nombre != null && !nombre.isEmpty() ? nombre : "usuario"));
        } else {
            textSaludo.setText("Hola, invitado");
        }

        // Botón: Crear equipo
        btnCrearEquipo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CrearEquipoActivity.class);
            startActivity(intent);
        });

        // Botón: Unirse a equipo con AlertDialog
        btnUnirseEquipo.setOnClickListener(v -> {
            EditText input = new EditText(getContext());
            input.setHint("Código del equipo");

            new AlertDialog.Builder(requireContext())
                    .setTitle("Unirse a un equipo")
                    .setMessage("Ingresa el código del equipo proporcionado:")
                    .setView(input)
                    .setPositiveButton("Unirse", (dialog, which) -> {
                        String codigo = input.getText().toString().trim();
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                        if (codigo.isEmpty()) {
                            Toast.makeText(getContext(), "Debes ingresar un código", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (currentUser == null) {
                            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Unirse a equipo en Firestore
                        new FirestoreService().unirseAEquipo(codigo, currentUser,
                                () -> Toast.makeText(getContext(), "Te uniste al equipo correctamente", Toast.LENGTH_SHORT).show(),
                                e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
}
