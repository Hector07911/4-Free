package sv.edu.itca.a4_free;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class ConfirmRequestActivity extends AppCompatActivity {
    private TextView tvDesc;
    private Button btnCancelar, btnConfirm;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String productId, productTitle, ownerId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_request);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        tvDesc = findViewById(R.id.tv_desc_confirm);
        btnCancelar = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);

        Intent intent = getIntent();
        if (intent != null) {
            productId = intent.getStringExtra("PRODUCT_ID");
            productTitle = intent.getStringExtra("PRODUCT_TITLE");
            ownerId = intent.getStringExtra("OWNER_ID");

            if (productTitle != null) {
                tvDesc.setText("¿Deseas solicitar el producto “" + productTitle + "”?");
            }
        }

        btnCancelar.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> confirmAndCreateRequest());
    }

    private void confirmAndCreateRequest() {
        if (currentUser == null || productId == null || productTitle == null || ownerId == null) {
            Toast.makeText(this, "Error: Faltan datos para crear la solicitud.", Toast.LENGTH_LONG).show();
            return;
        }

        // Crear el objeto Request
        Request newRequest = new Request(productId, productTitle, currentUser.getUid(), ownerId);

        // Guardar la solicitud en Firestore
        db.collection("requests")
                .add(newRequest)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(ConfirmRequestActivity.this, "Solicitud creada con éxito.", Toast.LENGTH_LONG).show();
                    // Navegar a la lista de solicitudes
                    Intent activeIntent = new Intent(ConfirmRequestActivity.this, ActivityRequests.class);
                    startActivity(activeIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ConfirmRequestActivity.this, "Error al crear la solicitud: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
