package sv.edu.itca.a4_free;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private final List<Message> mensajes;
    private final String currentUserId; // ID del usuario actual para determinar el tipo de vista

    public ChatAdapter(List<Message> mensajes, String currentUserId) {
        this.mensajes = mensajes;
        this.currentUserId = currentUserId;
    }

    // Determina si el mensaje fue enviado (tipo 1) o recibido (tipo 0)
    @Override
    public int getItemViewType(int position) {
        // Lógica real: Si el senderId es igual al ID del usuario actual, es enviado.
        return mensajes.get(position).getSenderId().equals(currentUserId) ? 1 : 0;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_message_sent : R.layout.item_message_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = mensajes.get(position);
        holder.txtMessage.setText(message.getTexto());
        // Opcional: mostrar timestamp si el layout lo soporta
        // holder.txtTimestamp.setText(message.getFechaFormateada());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asumiendo que item_message_sent/received solo tienen un TextView o que el ID es txtMessage
            // Si el TextView es el root, usa itemView como TextView. Si tiene un ID, usa findViewById.
            // Para simplicidad, asumiremos findViewById
            txtMessage = itemView.findViewById(R.id.editMessage); // Asumiendo este ID
            // Si tu layout de item_message usa el TextView como root view, descomenta la siguiente línea y comenta la anterior:
            // txtMessage = (TextView) itemView;
        }
    }
}
