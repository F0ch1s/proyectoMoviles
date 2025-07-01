package com.example.proyecto.data;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.function.Consumer;

public class FirestoreService {

    private static final String TAG = "FirestoreService";
    private static final String USERS_COLLECTION = "usuarios";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void registrarUsuarioSiNoExiste(FirebaseUser user, Runnable onSuccess) {
        if (user == null) return;

        String uid = user.getUid();

        db.collection(USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "El usuario ya existe en Firestore.");
                        onSuccess.run();
                    } else {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("uid", uid);
                        userData.put("nombre", user.getDisplayName());
                        userData.put("correo", user.getEmail());
                        userData.put("foto", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
                        userData.put("registrado_en", System.currentTimeMillis());

                        db.collection(USERS_COLLECTION).document(uid).set(userData)
                                .addOnSuccessListener(unused -> {
                                    Log.d(TAG, "Usuario registrado exitosamente.");
                                    onSuccess.run();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al registrar el usuario", e);
                                    onSuccess.run(); // continúa a pesar del error
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al verificar usuario", e);
                    onSuccess.run();
                });
    }

    public void obtenerEquiposDelUsuario(String correo, FirestoreEquiposCallback callback) {
        db.collection("equipos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> equiposDelUsuario = new ArrayList<>();

                    for (var doc : queryDocumentSnapshots) {
                        Map<String, Object> miembros = (Map<String, Object>) doc.get("miembros");
                        if (miembros != null && miembros.containsKey(correo)) {
                            Map<String, Object> equipoInfo = new HashMap<>();
                            equipoInfo.put("id", doc.getId());
                            equipoInfo.put("nombre_equipo", doc.getString("nombre_equipo"));
                            equipoInfo.put("descripcion", doc.getString("descripcion"));
                            equipoInfo.put("proyecto_terminado_en", doc.get("proyecto_terminado_en"));
                            equipoInfo.put("tareas", doc.get("tareas"));
                            equiposDelUsuario.add(equipoInfo);
                        }
                    }

                    callback.onSuccess(equiposDelUsuario);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface FirestoreEquiposCallback {
        void onSuccess(List<Map<String, Object>> equipos);
        void onFailure(Exception e);
    }

    public void crearEquipo(String nombreEquipo, String descripcion, Date fechaEntrega, FirebaseUser usuario,
                            Consumer<String> onSuccess, Consumer<Exception> onFailure) {
        if (usuario == null) {
            onFailure.accept(new Exception("Usuario no autenticado"));
            return;
        }

        String uid = usuario.getUid();
        String correo = usuario.getEmail();
        String nombre = usuario.getDisplayName();
        String equipoId = "equipo_" + System.currentTimeMillis();

        Map<String, Object> equipoData = new HashMap<>();
        equipoData.put("nombre_equipo", nombreEquipo);
        equipoData.put("descripcion", descripcion);
        equipoData.put("proyecto_terminado_en", new Timestamp(fechaEntrega));
        equipoData.put("tareas", new HashMap<>());

        Map<String, Object> miembros = new HashMap<>();
        Map<String, String> datosMiembro = new HashMap<>();
        datosMiembro.put("nombre", nombre);
        datosMiembro.put("rol", "Líder");
        miembros.put(correo, datosMiembro);
        equipoData.put("miembros", miembros);

        db.collection("equipos")
                .document(equipoId)
                .set(equipoData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Equipo creado exitosamente.");
                    onSuccess.accept(equipoId);
                })
                .addOnFailureListener(e -> onFailure.accept(e));
    }

    public void unirseAEquipo(String equipoId, FirebaseUser user,
                              Runnable onSuccess, Consumer<Exception> onFailure) {
        String correo = user.getEmail();
        String nombre = user.getDisplayName();

        DocumentReference docRef = db.collection("equipos").document(equipoId);

        docRef.get().addOnSuccessListener(docSnapshot -> {
            if (!docSnapshot.exists()) {
                onFailure.accept(new Exception("No se encontró el equipo"));
                return;
            }

            // Verificar si el usuario ya es miembro
            Map<String, Object> miembros = (Map<String, Object>) docSnapshot.get("miembros");
            if (miembros != null && miembros.containsKey(correo)) {
                onFailure.accept(new Exception("Ya formas parte de este equipo"));
                return;
            }

            // Agregar nuevo miembro
            Map<String, Object> miembro = new HashMap<>();
            miembro.put("nombre", nombre);
            miembro.put("rol", "Miembro");

            docRef.update("miembros." + correo, miembro)
                    .addOnSuccessListener(aVoid -> onSuccess.run())
                    .addOnFailureListener(onFailure::accept);

        }).addOnFailureListener(onFailure::accept);
    }

}
