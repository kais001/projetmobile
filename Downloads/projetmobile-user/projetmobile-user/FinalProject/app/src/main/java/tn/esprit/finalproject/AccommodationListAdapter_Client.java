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

import tn.esprit.finalproject.Entity.Accommodation;

import java.util.List;

public class AccommodationListAdapter_Client extends RecyclerView.Adapter<AccommodationListAdapter_Client.ViewHolder> {

    private final List<Accommodation> accommodationList;
    private final FragmentManager fragmentManager;

    public AccommodationListAdapter_Client(List<Accommodation> accommodationList, FragmentManager fragmentManager) {
        this.accommodationList = accommodationList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accommodation_list_item_client, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Accommodation accommodation = accommodationList.get(position);

        holder.nameTextView.setText(accommodation.getName());
        holder.addressTextView.setText(accommodation.getAddress());
        holder.priceTextView.setText("$" + accommodation.getPricePerNight() + " per night");

        holder.itemView.setOnClickListener(v -> {
            // Passer les données dans un Bundle
            Bundle bundle = new Bundle();
            bundle.putString("name", accommodation.getName());
            bundle.putString("address", accommodation.getAddress());
            bundle.putDouble("pricePerNight", accommodation.getPricePerNight());
            bundle.putString("description", accommodation.getDescription());
            bundle.putDouble("latitude", accommodation.getLatitude());
            bundle.putDouble("longitude", accommodation.getLongitude());

            // Ouvrir le fragment avec les détails de l'accommodation
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            AccommodationDetailsFragment detailFragment = new AccommodationDetailsFragment();
            detailFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return accommodationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView addressTextView;
        private final TextView priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
