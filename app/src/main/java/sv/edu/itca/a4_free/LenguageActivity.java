package sv.edu.itca.a4_free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LenguageActivity extends AppCompatActivity {
    private Button btnSpanish, btnEnglish, btnPortuguese;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        btnSpanish = findViewById(R.id.btnSpanish);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnPortuguese = findViewById(R.id.btnPortuguese);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar que el usuario anónimo esté activo antes de ir al Home
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LenguageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Esto no debería pasar si SplashActivity funcionó correctamente
                    Toast.makeText(LenguageActivity.this, "Error de autenticación. Reinicia la app.", Toast.LENGTH_LONG).show();
                }
            }
        };

        btnSpanish.setOnClickListener(listener);
        btnEnglish.setOnClickListener(listener);
        btnPortuguese.setOnClickListener(listener);
    }
}