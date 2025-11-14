package sv.edu.itca.a4_free;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView titleProduct, descProduct, contactProduct;
    private Button btnRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        imgProduct = findViewById(R.id.img_product_detail);
        titleProduct = findViewById(R.id.title_product_detail);
        descProduct = findViewById(R.id.desc_product_detail);
        contactProduct = findViewById(R.id.contact_product_detail);
        btnRequest = findViewById(R.id.btn_request_product);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String desc = intent.getStringExtra("desc");
            String contact = intent.getStringExtra("contact");

            if (title != null) titleProduct.setText(title);
            if (desc != null) descProduct.setText(desc);
            if (contact != null) contactProduct.setText("Donante: " + contact);
        }

        btnRequest.setOnClickListener(v -> {
            Intent intent1 = new Intent(ProductDetailActivity.this, ConfirmRequestActivity.class);
            startActivity(intent1);
        });

        btnRequest.setOnClickListener(v -> {
            Intent confirmIntent = new Intent(ProductDetailActivity.this, ConfirmRequestActivity.class);
            confirmIntent.putExtra("title", titleProduct.getText().toString());
            startActivity(confirmIntent);
        });
    }
}
