package sv.edu.itca.a4_free;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class SettingsActivity extends AppCompatActivity {
    private Spinner spinnerLanguage;
    private Button btnLogout;
    private TextView txtVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        btnLogout = findViewById(R.id.btnLogout);
        txtVersion = findViewById(R.id.txtVersion);

        // Idioma seleccionado (solo simulado)
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idioma = parent.getItemAtPosition(position).toString();
                Toast.makeText(SettingsActivity.this, "Idioma seleccionado: " + idioma, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Botón cerrar sesión → cierra sesión de Firebase y vuelve al selector de idioma
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(SettingsActivity.this, "Sesión cerrada.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, LenguageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}