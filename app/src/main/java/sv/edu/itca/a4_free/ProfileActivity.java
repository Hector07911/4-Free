package sv.edu.itca.a4_free;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class ProfileActivity extends AppCompatActivity {
    private TextView txtUserName, txtUserEmail, txtUserPhone;
    private Button btnEditProfile, btnLogout;
    private RecyclerView recyclerMyProducts;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private DummyAdapter productAdapter;
    private List<Product> productList;
    private ListenerRegistration productsListener;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Error: Usuario no disponible.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUserPhone = findViewById(R.id.txtUserPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        recyclerMyProducts = findViewById(R.id.recyclerMyProducts);

        // Mostrar ID de usuario anónimo y datos simulados
        txtUserName.setText("Usuario Anónimo: " + currentUser.getUid().substring(0, 8));
        txtUserEmail.setText("Anónimo"); // No tenemos email real
        txtUserPhone.setText("N/A"); // No tenemos teléfono real

        // Configurar lista de productos del usuario
        productList = new ArrayList<>();
        productAdapter = new DummyAdapter(this, productList);
        recyclerMyProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerMyProducts.setAdapter(productAdapter);

        loadMyProductsRealtime();

        // Eventos
        btnEditProfile.setOnClickListener(v -> {
            // El perfil es solo diseño, pero lo redirigimos a Settings como en tu código original
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> signOutUser());
    }

    private void loadMyProductsRealtime() {
        // Query: Obtener productos donde el donante es el usuario actual
        Query query = db.collection("products")
                .whereEqualTo("donante", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        productsListener = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Escucha de productos falló.", e);
                return;
            }

            if (snapshots != null) {
                productList.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                    Product product = doc.toObject(Product.class);
                    if (product != null) {
                        product.setId(doc.getId());
                        productList.add(product);
                    }
                }
                productAdapter.updateProducts(productList);
            }
        });
    }

    private void signOutUser() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Sesión cerrada. Volviendo a selección de idioma.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfileActivity.this, LenguageActivity.class);
        // Limpia el stack para que el usuario no pueda volver con el botón atrás
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (productsListener != null) {
            productsListener.remove();
        }
    }
}