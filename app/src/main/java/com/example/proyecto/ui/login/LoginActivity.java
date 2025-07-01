package com.example.proyecto.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto.DrawerActivity;
import com.example.proyecto.MainActivity;
import com.example.proyecto.data.FirestoreService;
import com.example.proyecto.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.example.proyecto.R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Forzar cierre de sesión para permitir selección de cuenta cada vez
        binding.btnGoogleSignIn.setOnClickListener(view -> {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                launcher.launch(signInIntent);
            });
        });
    }

    // Verificar si ya está logueado y redirigir directamente
    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, DrawerActivity.class));
            finish();
        }
    }

    // Resultado del intent de login con Google
    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    // Autenticación con Firebase y registro en Firestore
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            FirestoreService firestoreService = new FirestoreService();
                            firestoreService.registrarUsuarioSiNoExiste(user, () -> {
                                Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        }
                    } else {
                        Toast.makeText(this, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
