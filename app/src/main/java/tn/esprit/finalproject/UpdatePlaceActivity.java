package tn.esprit.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Place;

public class UpdatePlaceActivity extends AppCompatActivity {

    private EditText nameInput, stateInput, descriptionInput, photoUrlInput, gpsCoordinatesInput;
    private Button updateButton;
    private Place place;
    private AppDatabase db;  // Declare the database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_place);

        // Initialize the database instance
        db = AppDatabase.getInstance(this);

        nameInput = findViewById(R.id.nameInput);
        stateInput = findViewById(R.id.stateInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        photoUrlInput = findViewById(R.id.photoUrlInput);
        gpsCoordinatesInput = findViewById(R.id.gpsCoordinatesInput);
        updateButton = findViewById(R.id.updateButton);

        // Get the place ID from the intent
        int placeId = getIntent().getIntExtra("placeId", -1);
        if (placeId == -1) {
            Toast.makeText(this, "Invalid place ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch the place details in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            place = db.placeDao().getPlaceById(placeId);

            runOnUiThread(() -> {
                if (place != null) {
                    // Populate the fields
                    nameInput.setText(place.getName());
                    stateInput.setText(place.getState());
                    descriptionInput.setText(place.getDescription());
                    photoUrlInput.setText(place.getPhotoUrl());
                    gpsCoordinatesInput.setText(place.getGpsCoordinates());
                } else {
                    Toast.makeText(this, "Place not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });

        // Handle update button click
        updateButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String state = stateInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String photoUrl = photoUrlInput.getText().toString().trim();
            String gpsCoordinates = gpsCoordinatesInput.getText().toString().trim();

            if (name.isEmpty() || state.isEmpty() || gpsCoordinates.isEmpty()) {
                Toast.makeText(this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update the place object
            place.setName(name);
            place.setState(state);
            place.setDescription(description);
            place.setPhotoUrl(photoUrl);
            place.setGpsCoordinates(gpsCoordinates);

            // Perform update in a background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                db.placeDao().update(place);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Place updated successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UpdatePlaceActivity.this, ShowPlacesActivity.class));
                    finish();
                });
            });
        });
    }
}
