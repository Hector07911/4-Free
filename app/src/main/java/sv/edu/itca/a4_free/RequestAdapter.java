package sv.edu.itca.a4_free;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> requestList;

    public RequestAdapter(List<Request> requestList) {
        this.requestList = requestList;
    }

    // Nuevo método para actualizar la lista
    public void updateRequests(List<Request> newRequests) {
        this.requestList = newRequests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.title.setText(request.getTitulo());
        holder.status.setText(request.getEstado());
        holder.date.setText("Fecha: " + request.getFecha()); // Usa el método getFecha del modelo

        // Cambiar color según estado
        switch (request.getEstado()) {
            case "Pendiente":
                holder.status.setTextColor(0xFF2563EB); // Azul
                break;
            case "Aceptada":
                holder.status.setTextColor(0xFF22C55E); // Verde
                break;
            case "Completada":
                holder.status.setTextColor(0xFF6B7280); // Gris
                break;
        }

        // Implementar click: Por ejemplo, para abrir un Chat con el dueño/solicitante
        holder.itemView.setOnClickListener(v -> {
            // Ejemplo de cómo iniciar un chat
            /*
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            // Crea un ID de chat único (ej: combinando los UIDs de los dos usuarios)
            String chatId = request.getRequesterId().compareTo(request.getOwnerId()) < 0 ?
                            request.getRequesterId() + "_" + request.getOwnerId() :
                            request.getOwnerId() + "_" + request.getRequesterId();
            intent.putExtra("CHAT_ID", chatId);
            v.getContext().startActivity(intent);
            */
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, status, date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.request_title);
            status = itemView.findViewById(R.id.request_status);
            date = itemView.findViewById(R.id.request_date);
        }
    }
}