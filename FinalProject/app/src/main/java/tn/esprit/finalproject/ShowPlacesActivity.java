package tn.esprit.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.finalproject.PlaceAdapter;
import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Place;

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
            finish();
        });

        // Fetch and display places on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Place> places = AppDatabase.getInstance(this).placeDao().getAllPlaces();
            runOnUiThread(() -> {
                if (places.isEmpty()) {
                    Toast.makeText(this, "No places available. Please add one.", Toast.LENGTH_SHORT).show();
                } else {
                    PlaceAdapter adapter = new PlaceAdapter(this, places, place -> {
                        // Navigate to PlaceDetailActivity with the selected place
                        Intent intent = new Intent(ShowPlacesActivity.this, PlaceDetailActivity.class);
                        intent.putExtra("place", place); // Pass the Place object to the next activity
                        startActivity(intent);
                        finish();
                    });
                    placesRecyclerView.setAdapter(adapter);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlaces();
    }

    private void loadPlaces() {
        // Use ExecutorService to perform database operations in the background
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Place> places = AppDatabase.getInstance(this).placeDao().getAllPlaces();

            // Run on the main thread to update the UI after fetching the places
            runOnUiThread(() -> {
                if (places.isEmpty()) {
                    Toast.makeText(this, "No places available. Please add one.", Toast.LENGTH_SHORT).show();
                } else {
                    PlaceAdapter adapter = new PlaceAdapter(this, places, place -> {
                        // Navigate to PlaceDetailActivity with the selected place
                        Intent intent = new Intent(ShowPlacesActivity.this, PlaceDetailActivity.class);
                        intent.putExtra("place", place);  // Pass the Place object to the next activity
                        startActivity(intent);
                        finish();
                    });
                    placesRecyclerView.setAdapter(adapter);
                }
            });
        });
    }
}
