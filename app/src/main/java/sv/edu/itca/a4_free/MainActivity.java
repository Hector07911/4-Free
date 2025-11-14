package sv.edu.itca.a4_free;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    RecyclerView recycler;
    BottomNavigationView bottomNavigation;
    FloatingActionButton fabAdd;

    private FirebaseFirestore db;
    private DummyAdapter productAdapter;
    private List<Product> productList;
    private ListenerRegistration productsListener;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        recycler = findViewById(R.id.recyclerProducts);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fabAdd = findViewById(R.id.fabAddProduct);

        // Configuración del RecyclerView
        productList = new ArrayList<>();
        // Pasamos la lista vacía al adaptador
        productAdapter = new DummyAdapter(this, productList);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(productAdapter);

        // Inicia la escucha en tiempo real
        loadProductsRealtime();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_chat) {
                // Navegar a la lista de chats o a un chat de prueba
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadProductsRealtime() {
        // Query para obtener productos ordenados por fecha de forma descendente
        Query query = db.collection("products")
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
                        product.setId(doc.getId()); // Asignar el ID de Firestore
                        productList.add(product);
                    }
                }
                productAdapter.updateProducts(productList);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener la escucha de Firestore
        if (productsListener != null) {
            productsListener.remove();
        }
    }
}