package tn.esprit.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import tn.esprit.finalproject.Entity.Event;

import java.util.List;

public class EventListAdapter_Client extends RecyclerView.Adapter<EventListAdapter_Client.ViewHolder> {

    private final List<Event> eventList;
    private final FragmentManager fragmentManager;

    public EventListAdapter_Client(List<Event> eventList, FragmentManager fragmentManager) {
        this.eventList = eventList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item_client, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = eventList.get(position);

        holder.nameTextView.setText(event.getName());
        holder.priceTextView.setText("$" + event.getPrice());

        holder.itemView.setOnClickListener(v -> {
            // Passer les données dans un Bundle
            Bundle bundle = new Bundle();
            bundle.putString("name", event.getName());
            bundle.putString("address", event.getAddress());
            bundle.putDouble("price", event.getPrice());
            bundle.putString("description", event.getDescription());

            // Ouvrir le fragment avec les détails de l'événement
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            EventDetailsFragment detailFragment = new EventDetailsFragment();
            detailFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;
        private final TextView priceTextView;
        private final TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
