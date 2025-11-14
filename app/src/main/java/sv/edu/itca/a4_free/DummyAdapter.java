package sv.edu.itca.a4_free;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.ViewHolder> {
    private List<Product> listaProductos;
    private Context context;

    // Adaptador actualizado para recibir la lista en el constructor
    public DummyAdapter(Context context, List<Product> productos) {
        this.context = context;
        this.listaProductos = productos;
    }

    // Método para actualizar la lista de productos desde Firestore
    public void updateProducts(List<Product> newProducts) {
        this.listaProductos = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product producto = listaProductos.get(position);
        holder.title.setText(producto.getTitulo());
        holder.desc.setText(producto.getDescripcion());
        // El 'donante' es ahora el UID, por lo que mostramos un ID corto para simular
        String shortDonanteId = producto.getDonante().substring(0, 6) + "...";
        holder.user.setText("Publicado por: " + shortDonanteId);

        // 🔹 Carga de imagen con Glide (Asumiendo dependencia añadida)
        if (producto.getMainImageUrl() != null && !producto.getMainImageUrl().isEmpty()) {
            // Reemplaza 'holder.imgProduct' si no existe en item_product
            // Glide.with(context).load(producto.getMainImageUrl()).into(holder.imgProduct);
        }

        // 🔹 Clic sobre el producto → abre detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            // Pasamos el ID del producto para cargarlo en detalle
            intent.putExtra("PRODUCT_ID", producto.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc, user;
        // ImageView imgProduct; // Descomentar si existe en R.layout.item_product

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_product);
            desc = itemView.findViewById(R.id.desc_product);
            user = itemView.findViewById(R.id.user_product);
            // imgProduct = itemView.findViewById(R.id.img_product); // Descomentar si existe
        }
    }
}