package tn.esprit.travelcompanionapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.travelcompanionapp.R;
import tn.esprit.travelcompanionapp.adapters.PlaceAdapter;
import tn.esprit.travelcompanionapp.database.RoomDB;
import tn.esprit.travelcompanionapp.models.Place;

public class ShowPlacesActivity extends AppCompatActivity {

    private RecyclerView placesRecyclerView;
    private Button addPlaceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_places);

        // Initialize RecyclerView
        placesRecyclerView = findViewById(R.id.placesRecyclerView);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Add Place Button
        addPlaceButton = findViewById(R.id.addPlaceButton);
        addPlaceButton.setOnClickListener(v -> {
            // Navigate to AddPlaceActivity
            Intent intent = new Intent(ShowPlacesActivity.this, AddPlaceActivity.class);
            startActivity(intent);
        });

        // Fetch and display places
        List<Place> places = RoomDB.getInstance(this).placeDAO().getAllPlaces();
        if (places.isEmpty()) {
            Toast.makeText(this, "No places available. Please add one.", Toast.LENGTH_SHORT).show();
        } else {
            PlaceAdapter adapter = new PlaceAdapter(this, places, place -> {
                // Navigate to PlaceDetailActivity with the selected place
                Intent intent = new Intent(ShowPlacesActivity.this, PlaceDetailActivity.class);
                intent.putExtra("place", place); // Pass the Place object to the next activity
                startActivity(intent);
            });
            placesRecyclerView.setAdapter(adapter);
        }
    }
}
