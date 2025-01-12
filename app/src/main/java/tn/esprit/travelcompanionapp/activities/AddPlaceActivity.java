package tn.esprit.travelcompanionapp.activities;




import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.travelcompanionapp.R;
import tn.esprit.travelcompanionapp.database.RoomDB;
import tn.esprit.travelcompanionapp.models.Place;

public class AddPlaceActivity extends AppCompatActivity {

    private EditText nameInput, stateInput, descriptionInput, photoUrlInput, gpsCoordinatesInput;
    private Button saveButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        nameInput = findViewById(R.id.nameInput);
        stateInput = findViewById(R.id.stateInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        photoUrlInput = findViewById(R.id.photoUrlInput);
        gpsCoordinatesInput = findViewById(R.id.gpsCoordinatesInput);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String state = stateInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String photoUrl = photoUrlInput.getText().toString();
            String gpsCoordinates = gpsCoordinatesInput.getText().toString();

            if (name.isEmpty() || state.isEmpty() || gpsCoordinates.isEmpty()) {
                Toast.makeText(this, "Please fill out all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            Place place = new Place();
            place.setName(name);
            place.setState(state);
            place.setDescription(description);
            place.setPhotoUrl(photoUrl);
            place.setGpsCoordinates(gpsCoordinates);

            RoomDB.getInstance(this).placeDAO().insert(place);
            Toast.makeText(this, "Place added successfully!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(AddPlaceActivity.this, ShowPlacesActivity.class));
            finish();
        });
    }
}