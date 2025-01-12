package tn.esprit.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AccommodationDetailsFragment extends Fragment {

    private String name, address, description;
    private double pricePerNight, latitude, longitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accommodation_details, container, false);

        // Récupérer les données depuis le Bundle
        if (getArguments() != null) {
            name = getArguments().getString("name");
            address = getArguments().getString("address");
            pricePerNight = getArguments().getDouble("pricePerNight");
            description = getArguments().getString("description");
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");

            // Remplir les champs de l'UI avec les données
            TextView nameTextView = view.findViewById(R.id.nameTextView);
            TextView addressTextView = view.findViewById(R.id.addressTextView);
            TextView priceTextView = view.findViewById(R.id.priceTextView);
            TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);

            nameTextView.setText(name);
            addressTextView.setText(address);
            priceTextView.setText("$" + pricePerNight + " per night");
            descriptionTextView.setText(description);
        }

        // Gérer le clic sur le bouton "Réserver"
        Button reserveButton = view.findViewById(R.id.reserveButton);
        reserveButton.setOnClickListener(v -> onReserveClicked());

        // Gérer le clic sur le bouton "Directions"
        Button directionsButton = view.findViewById(R.id.directionButton);
        directionsButton.setOnClickListener(v -> onDirectionsClicked());

        return view;
    }

    private void onReserveClicked() {
        // Implémenter la logique de réservation ici
    }

    private void onDirectionsClicked() {
        CompassToAccFragment compassFragment = CompassToAccFragment.newInstance(latitude, longitude);

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Remplacez l'ancien ID par celui défini dans votre XML : R.id.main_content
        transaction.replace(R.id.fragment_container, compassFragment);
        transaction.addToBackStack(null); // Ajouter à la pile de retour
        transaction.commit();
    }

}
