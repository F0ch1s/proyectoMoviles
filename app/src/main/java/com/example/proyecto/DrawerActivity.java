package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.proyecto.fragments.AyudaFragment;
import com.example.proyecto.fragments.MisEquiposFragment;
import com.example.proyecto.fragments.PrincipalFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);  // 🔥 Asegura la inicialización
        setContentView(R.layout.activity_drawer);

        // 🔍 Paso de prueba: Verificar conexión con Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> testData = new HashMap<>();
        testData.put("mensaje", "Conexión exitosa");
        testData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("test")
                .add(testData)
                .addOnSuccessListener(documentReference ->
                        Log.d("FIREBASE_TEST", "Documento escrito con ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.w("FIREBASE_TEST", "Error al escribir documento", e));

        // Configurar Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Mostrar ícono hamburguesa personalizado
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        // Cambiar color del ícono de hamburguesa a blanco
        toolbar.post(() -> {
            if (toolbar.getNavigationIcon() != null) {
                toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.white));
            }
        });

        // Configurar botón de perfil
        btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, PerfilActivity.class);
            startActivity(intent);
        });

        // Drawer y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Fragmento inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedorFragment, new PrincipalFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_principal);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_principal) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedorFragment, new PrincipalFragment())
                    .commit();
        } else if (id == R.id.nav_mis_equipos) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedorFragment, new MisEquiposFragment())
                    .commit();
        } else if (id == R.id.nav_ayuda) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedorFragment, new AyudaFragment())
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
