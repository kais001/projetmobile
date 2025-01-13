package tn.esprit.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import tn.esprit.finalproject.Entity.Accommodation;

import java.util.List;

public class AccommodationListAdapter extends RecyclerView.Adapter<AccommodationListAdapter.ViewHolder> {

    private List<Accommodation> accommodationList;
    private final OnAccommodationClickListener listener;

    public interface OnAccommodationClickListener {
        void onAccommodationClick(Accommodation accommodation);
        void onDeleteClick(Accommodation accommodation);
    }

    public AccommodationListAdapter(List<Accommodation> accommodationList, OnAccommodationClickListener listener) {
        this.accommodationList = accommodationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accommodation_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Accommodation accommodation = accommodationList.get(position);
        holder.bind(accommodation);
    }

    @Override
    public int getItemCount() {
        return accommodationList != null ? accommodationList.size() : 0;
    }

    // Méthode pour mettre à jour la liste d'accommodations
    public void updateAccommodationList(List<Accommodation> newAccommodationList) {
        this.accommodationList = newAccommodationList;
        notifyDataSetChanged();  // Notifie l'adaptateur que les données ont changé
    }

    // ViewHolder interne pour l'affichage des items
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, addressTextView, priceTextView;
        private ImageView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            // Clic sur l'élément pour afficher les détails
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Accommodation accommodation = accommodationList.get(position);
                    listener.onAccommodationClick(accommodation);
                }
            });

            // Clic sur le bouton de suppression
            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Accommodation accommodation = accommodationList.get(position);
                    listener.onDeleteClick(accommodation);
                }
            });
        }

        public void bind(Accommodation accommodation) {
            nameTextView.setText(accommodation.getName());
            addressTextView.setText(accommodation.getAddress());
            priceTextView.setText(String.valueOf(accommodation.getPricePerNight()));
        }
    }
}
