package com.example.proyecto.ui.login;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto.R;
import com.example.proyecto.data.FirestoreService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CrearEquipoActivity extends AppCompatActivity {

    private EditText etNombreEquipo, etDescripcion, etFechaEntrega;
    private Button btnCrearEquipo;
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_equipo);

        firestoreService = new FirestoreService();

        etNombreEquipo = findViewById(R.id.etNombreEquipo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etFechaEntrega = findViewById(R.id.etFechaEntrega);
        btnCrearEquipo = findViewById(R.id.btnCrearEquipo);

        // Selector de fecha (DatePicker)
        etFechaEntrega.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(this, (view, y, m, d) -> {
                String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y);
                etFechaEntrega.setText(fecha);
            }, year, month, day);
            picker.show();
        });

        btnCrearEquipo.setOnClickListener(v -> {
            String nombre = etNombreEquipo.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String fechaEntrega = etFechaEntrega.getText().toString().trim();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (nombre.isEmpty() || descripcion.isEmpty() || fechaEntrega.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date fecha = sdf.parse(fechaEntrega);
                Date hoy = new Date();

                if (fecha.before(hoy)) {
                    Toast.makeText(this, "La fecha de entrega no puede ser anterior a hoy", Toast.LENGTH_SHORT).show();
                    return;
                }

                firestoreService.crearEquipo(nombre, descripcion, fecha, user,
                        equipoId -> {
                            new AlertDialog.Builder(this)
                                    .setTitle("Equipo creado")
                                    .setMessage("Código del equipo:\n" + equipoId)
                                    .setPositiveButton("Copiar", (dialog, which) -> {
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        clipboard.setPrimaryClip(ClipData.newPlainText("ID equipo", equipoId));
                                        Toast.makeText(this, "ID copiado", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNeutralButton("Compartir", (dialog, which) -> {
                                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                        sendIntent.setType("text/plain");
                                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                                "Únete a mi equipo usando este código en la app:\n" + equipoId);
                                        Intent chooser = Intent.createChooser(sendIntent, "Compartir por...");
                                        try {
                                            startActivity(chooser);
                                        } catch (Exception e) {
                                            Toast.makeText(this, "No se pudo abrir el menú de compartir", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("Cerrar", null)
                                    .show();
                        },
                        e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );

            } catch (ParseException e) {
                Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}