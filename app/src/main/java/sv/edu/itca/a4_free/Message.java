package sv.edu.itca.a4_free;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {
    private String id; // ID del mensaje en Firestore
    private String texto;
    private String senderId; // ID del usuario que envía
    private String chatId; // ID del chat

    @ServerTimestamp // Marca la fecha en el servidor
    private Date timestamp;

    // Constructor vacío requerido por Firestore
    public Message() {}

    // Constructor original modificado: 'enviado' se determina por lógica en el Adapter, usando senderId
    public Message(String texto, String senderId, String chatId) {
        this.texto = texto;
        this.senderId = senderId;
        this.chatId = chatId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    // Método obsoleto del modelo original, mantenido por compatibilidad si es usado en UI,
    // pero la lógica debe ser: senderId.equals(currentUserId)
    public boolean isEnviado() { return false; }
}