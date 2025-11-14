package sv.edu.itca.a4_free;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME = 1500;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // 1. Inicializar Firebase
        try {
            FirebaseApp.initializeApp(this);
            mAuth = FirebaseAuth.getInstance();
            // Esto es opcional, pero ayuda a debuggear si hay problemas
            // FirebaseFirestore.getInstance().setLoggingEnabled(true);
        } catch (Exception e) {
            Toast.makeText(this, "Error: No se pudo inicializar Firebase. Revisa google-services.json.", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(this::finish, SPLASH_TIME); // Cerrar después del tiempo de splash
            return;
        }

        // 2. Intentar autenticar anónimamente o usar la sesión existente
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            // Si ya hay un usuario logueado (ej: con correo, aunque el usuario no lo pide, lo manejamos)
            goToLanguageScreen();
        } else if (currentUser != null && currentUser.isAnonymous()) {
            // Usuario anónimo ya autenticado
            goToLanguageScreen();
        } else {
            // Iniciar sesión anónimamente
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Éxito: Usuario anónimo autenticado, su UID es el userId
                        goToLanguageScreen();
                    } else {
                        // Fallo en la autenticación
                        Toast.makeText(SplashActivity.this, "Error de autenticación: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(this::finish, SPLASH_TIME); // Cerrar
                    }
                });
    }

    private void goToLanguageScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LenguageActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME );
    }
}
