package sv.edu.itca.a4_free;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Request {
    private String id; // ID de la solicitud en Firestore
    private String productId;
    private String productTitle; // Título del producto solicitado
    private String requesterId; // ID del usuario que solicita
    private String ownerId; // ID del usuario dueño del producto
    private String titulo; // Usado para mostrar el título en el listado
    private String estado; // Pendiente, Aceptada, Completada

    @ServerTimestamp
    private Date timestamp; // Fecha de la solicitud

    // Constructor vacío requerido por Firestore
    public Request() {}

    // Constructor original modificado para compatibilidad y campos de Firebase
    public Request(String titulo, String estado, String fecha) {
        this.titulo = titulo;
        this.estado = estado;
        // La fecha real se manejará con timestamp de Firebase
    }

    public Request(String productId, String productTitle, String requesterId, String ownerId) {
        this.productId = productId;
        this.productTitle = productTitle;
        this.requesterId = requesterId;
        this.ownerId = ownerId;
        this.titulo = productTitle;
        this.estado = "Pendiente"; // Estado inicial
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public String getRequesterId() { return requesterId; }
    public void setRequesterId(String requesterId) { this.requesterId = requesterId; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    // Función getFecha se reemplaza por lógica de formato en el adaptador
    public String getFecha() {
        return timestamp != null ? android.text.format.DateFormat.format("dd MMM yyyy", timestamp).toString() : "";
    }
}
