package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private TextView textNombre, textCorreo;
    private Button btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imgPerfil = findViewById(R.id.imgPerfil);
        textNombre = findViewById(R.id.textNombre);
        textCorreo = findViewById(R.id.textCorreo);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            textNombre.setText(user.getDisplayName());
            textCorreo.setText(user.getEmail());
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .circleCrop() // Recorte circular
                    .into(imgPerfil);
        }

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
