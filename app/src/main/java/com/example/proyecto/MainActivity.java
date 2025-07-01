package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView textSaludo;
    private Button btnCrearEquipo, btnUnirseEquipo;
    private ImageButton btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Asegúrate de que el XML se llama exactamente así

        // Enlazar vistas del layout
        textSaludo = findViewById(R.id.textSaludo);
        btnCrearEquipo = findViewById(R.id.btnCrearEquipo);
        btnUnirseEquipo = findViewById(R.id.btnUnirseEquipo);
        btnPerfil = findViewById(R.id.btnPerfil);

        // Obtener usuario actual de Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String nombre = user.getDisplayName();
            if (nombre != null && !nombre.isEmpty()) {
                textSaludo.setText("Hola, " + nombre);
            } else {
                textSaludo.setText("Hola, usuario");
            }
        } else {
            textSaludo.setText("Hola, invitado");
        }

        // Botones
        btnCrearEquipo.setOnClickListener(v ->
                Toast.makeText(this, "Crear equipo (próximamente)", Toast.LENGTH_SHORT).show());

        btnUnirseEquipo.setOnClickListener(v ->
                Toast.makeText(this, "Unirse a equipo (próximamente)", Toast.LENGTH_SHORT).show());

        btnPerfil.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, PerfilActivity.class)));
    }
}
