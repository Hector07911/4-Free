package sv.edu.itca.a4_free;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Product {
    private String id; // ID del producto en Firestore
    private String titulo;
    private String descripcion;
    private String donante; // ID del donante (el nombre del donante puede ser un campo separado para UI)
    private String mainImageUrl; // URL de la imagen principal en Storage

    @ServerTimestamp
    private Date timestamp; // Fecha de publicación

    // Constructor vacío requerido por Firestore
    public Product() {}

    // Constructor original modificado para incluir ID y URL de imagen
    public Product(String titulo, String descripcion, String donante) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.donante = donante; // Este ahora es el ID del usuario
        this.mainImageUrl = ""; // Por defecto sin imagen
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDonante() { return donante; }
    public void setDonante(String donante) { this.donante = donante; }
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}