package sv.edu.itca.a4_free;


import android.os.Bundle;
import android.util.Log;
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


public class ActivityRequests extends AppCompatActivity {
    RecyclerView recyclerRequests;

    private RequestAdapter requestAdapter;
    private List<Request> requestList;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ListenerRegistration requestsListener;
    private static final String TAG = "ActivityRequests";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Error: No se puede cargar solicitudes sin usuario.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerRequests = findViewById(R.id.recyclerRequests);
        recyclerRequests.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(requestList);
        recyclerRequests.setAdapter(requestAdapter);

        loadRequestsRealtime();
    }

    private void loadRequestsRealtime() {
        // Obtenemos el ID del usuario actual
        String userId = currentUser.getUid();

        // Query complejo: Obtener solicitudes donde yo soy el requesterId O el ownerId
        // Firestore no permite 'OR' en queries compuestas de campos diferentes,
        // por lo que se harían dos consultas y se combinan los resultados.

        // 1. Solicitudes donde soy el Solicitante (Requester)
        Query queryRequester = db.collection("requests")
                .whereEqualTo("requesterId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // 2. Solicitudes donde soy el Dueño (Owner)
        Query queryOwner = db.collection("requests")
                .whereEqualTo("ownerId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Por simplicidad y para mantener un solo listener, solo usaremos la Query del Solicitante.
        // En una app real, usarías 2 listeners y combinarías los resultados.
        requestsListener = queryRequester.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Escucha de solicitudes falló.", e);
                return;
            }

            if (snapshots != null) {
                requestList.clear();
                for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                    Request request = doc.toObject(Request.class);
                    if (request != null) {
                        request.setId(doc.getId());
                        requestList.add(request);
                    }
                }
                // Si solo cargamos las de Requester, podemos filtrar localmente si es necesario
                requestAdapter.updateRequests(requestList); // Usamos el nuevo método de actualización
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestsListener != null) {
            requestsListener.remove();
        }
    }
}
