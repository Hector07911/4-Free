package sv.edu.itca.a4_free;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerChat;
    private EditText editMessage;
    private ImageButton btnSend;

    private ChatAdapter adapter;
    private List<Message> mensajes;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ListenerRegistration chatListener;
    private String chatId;
    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Error: No se puede usar el chat sin usuario.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerChat = findViewById(R.id.recyclerChat);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        // Obtener el ID del chat. Usamos un ID de prueba si no se proporciona
        chatId = getIntent().getStringExtra("CHAT_ID");
        if (chatId == null) {
            // ID de chat de prueba entre el usuario actual y un ID de 'sistema'
            chatId = currentUser.getUid().substring(0, 5) + "_TEST_CHAT";
            setTitle("Chat de Prueba (" + chatId + ")");
        }

        mensajes = new ArrayList<>();
        adapter = new ChatAdapter(mensajes, currentUser.getUid()); // Pasamos el UID al adaptador
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        loadMessagesRealtime();

        btnSend.setOnClickListener(v -> {
            String texto = editMessage.getText().toString().trim();
            if (!texto.isEmpty()) {
                sendMessage(texto);
                editMessage.setText("");
            }
        });
    }

    private void loadMessagesRealtime() {
        // Referencia a la colección de mensajes para este chat específico
        CollectionReference messagesRef = db.collection("chats").document(chatId).collection("messages");

        chatListener = messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Escucha de mensajes falló.", e);
                        return;
                    }

                    if (snapshots != null) {
                        mensajes.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            if (message != null) {
                                message.setId(doc.getId());
                                mensajes.add(message);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        // Scroll al último mensaje
                        if (!mensajes.isEmpty()) {
                            recyclerChat.scrollToPosition(mensajes.size() - 1);
                        }
                    }
                });
    }

    private void sendMessage(String texto) {
        Message newMessage = new Message(texto, currentUser.getUid(), chatId);

        db.collection("chats").document(chatId).collection("messages")
                .add(newMessage)
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Error al enviar mensaje.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error al enviar mensaje: " + e.getMessage());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener la escucha del chat
        if (chatListener != null) {
            chatListener.remove();
        }
    }
}
