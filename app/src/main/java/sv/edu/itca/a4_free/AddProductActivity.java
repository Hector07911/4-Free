package sv.edu.itca.a4_free;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {
    ImageView imgMain, imgExtra1, imgExtra2;
    Button btnSelectMain, btnSelectExtra, btnPublish;
    EditText edtTitle, edtDescription;

    private static final int PICK_MAIN_IMAGE = 1;
    private static final int PICK_EXTRA_IMAGES = 2;

    private Uri mainImageUri = null;
    private final List<Uri> extraImageUris = new ArrayList<>(); // Soporte para imágenes extra

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Error de autenticación. Intenta de nuevo.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        imgMain = findViewById(R.id.imgMain);
        imgExtra1 = findViewById(R.id.imgExtra1);
        imgExtra2 = findViewById(R.id.imgExtra2);
        btnSelectMain = findViewById(R.id.btnSelectMainImage);
        btnSelectExtra = findViewById(R.id.btnSelectExtraImages);
        btnPublish = findViewById(R.id.btnPublish);
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);

        btnSelectMain.setOnClickListener(v -> selectImage(PICK_MAIN_IMAGE));
        btnSelectExtra.setOnClickListener(v -> selectImage(PICK_EXTRA_IMAGES));
        btnPublish.setOnClickListener(v -> publishProduct());
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Si el requestCode es PICK_EXTRA_IMAGES y el usuario quiere seleccionar múltiples,
        // puedes agregar: intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (requestCode == PICK_MAIN_IMAGE) {
                mainImageUri = selectedImage;
                imgMain.setImageURI(selectedImage);
            } else if (requestCode == PICK_EXTRA_IMAGES) {
                // Lógica original para mostrar hasta 2 imágenes extra
                if (imgExtra1.getDrawable() == null) {
                    imgExtra1.setImageURI(selectedImage);
                    extraImageUris.add(selectedImage);
                } else if (imgExtra2.getDrawable() == null) {
                    imgExtra2.setImageURI(selectedImage);
                    extraImageUris.add(selectedImage);
                } else {
                    Toast.makeText(this, "Máximo de 3 imágenes alcanzado.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void publishProduct() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (mainImageUri == null) {
            Toast.makeText(this, "Debes seleccionar una imagen principal", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublish.setEnabled(false); // Deshabilitar botón durante la carga

        // 1. Subir la imagen principal
        uploadImage(mainImageUri, "main")
                .addOnSuccessListener(mainImageUrl -> {
                    // 2. Subir las imágenes extra (si existen)
                    List<Task<String>> uploadTasks = new ArrayList<>();
                    for (Uri uri : extraImageUris) {
                        uploadTasks.add(uploadImage(uri, "extra_" + UUID.randomUUID().toString().substring(0, 4)));
                    }

                    // Esperar a que todas las imágenes extra terminen de subir
                    Tasks.whenAllSuccess(uploadTasks)
                            .addOnSuccessListener(results -> {
                                // results es una List<String> de las URLs de las imágenes extra
                                List<String> extraUrls = new ArrayList<>();
                                for (Object result : results) {
                                    if (result instanceof String) {
                                        extraUrls.add((String) result);
                                    }
                                }
                                // 3. Guardar el producto en Firestore
                                saveProductToFirestore(title, description, mainImageUrl, extraUrls);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AddProductActivity.this, "Error al subir imágenes extra: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                btnPublish.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Error al subir imagen principal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnPublish.setEnabled(true);
                });
    }

    // Función helper para subir una imagen a Storage y devolver la URL de descarga
    private Task<String> uploadImage(Uri uri, String type) {
        String filename = type + "_" + UUID.randomUUID().toString();
        StorageReference ref = storage.getReference()
                .child("products/" + currentUser.getUid() + "/" + filename);

        return ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                })
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().toString();
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Función helper para guardar el producto en Firestore
    private void saveProductToFirestore(String title, String description, String mainImageUrl, List<String> extraUrls) {
        // Usamos el UID anónimo como 'donante'
        Product newProduct = new Product(title, description, currentUser.getUid());
        newProduct.setMainImageUrl(mainImageUrl);
        // Si necesitas guardar URLs extra: newProduct.setExtraImageUrls(extraUrls); // Asumiendo que el modelo Product tiene este setter

        db.collection("products")
                .add(newProduct)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddProductActivity.this, "Producto publicado con éxito.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddProductActivity.this, "Error al guardar producto: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnPublish.setEnabled(true);
                });
    }
}