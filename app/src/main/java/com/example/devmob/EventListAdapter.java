package com.example.devmob;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.devmob.entity.Event;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private List<Event> eventList;
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onDeleteClick(Event event);
    }

    public EventListAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    // Méthode pour mettre à jour la liste d'événements
    public void updateEventList(List<Event> newEventList) {
        this.eventList = newEventList;
        notifyDataSetChanged();
    }

    // ViewHolder interne pour l'affichage des items
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, addressTextView, priceTextView, descriptionTextView;
        private ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            // Clic sur l'élément pour afficher les détails
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Event event = eventList.get(position);
                    listener.onEventClick(event);
                }
            });

            // Clic sur le bouton de suppression
            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Event event = eventList.get(position);
                    listener.onDeleteClick(event);
                }
            });
        }

        public void bind(Event event) {
            nameTextView.setText(event.getName());
            addressTextView.setText(event.getAddress());
            priceTextView.setText(String.valueOf(event.getPrice()));
            descriptionTextView.setText(event.getDescription());
        }
    }
}
