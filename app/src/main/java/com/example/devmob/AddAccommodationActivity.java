package com.example.devmob;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.devmob.DAO.AccommodationDAO;
import com.example.devmob.DataBase.AccommodationDB;
import com.example.devmob.entity.Accommodation;

public class AddAccommodationActivity extends AppCompatActivity {
    AccommodationDB appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);

        // Initialize the database
        appDatabase = AccommodationDB.getInstance(this);

        // Find views
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText addressEditText = findViewById(R.id.addressEditText);
        EditText capacityEditText = findViewById(R.id.capacityEditText);
        EditText priceEditText = findViewById(R.id.priceEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        EditText latitudeEditText = findViewById(R.id.latitudeEditText);
        EditText longitudeEditText = findViewById(R.id.longitudeEditText);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input values
                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String capacityString = capacityEditText.getText().toString();
                String priceString = priceEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String latitudeString = latitudeEditText.getText().toString();
                String longitudeString = longitudeEditText.getText().toString();

                // Validate fields
                if (name.isEmpty() || address.isEmpty() || capacityString.isEmpty() ||
                        priceString.isEmpty() || description.isEmpty() ||
                        latitudeString.isEmpty() || longitudeString.isEmpty()) {
                    Toast.makeText(AddAccommodationActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Parse input values
                    int capacity = Integer.parseInt(capacityString);
                    double pricePerNight = Double.parseDouble(priceString);
                    double latitude = Double.parseDouble(latitudeString);
                    double longitude = Double.parseDouble(longitudeString);

                    // Create an accommodation object
                    Accommodation newAccommodation = new Accommodation(
                            name, address, capacity, latitude, longitude, pricePerNight, description
                    );

                    // Insert into database asynchronously
                    new InsertAccommodationTask(appDatabase.accommodationDao(), newAccommodation).execute();

                } catch (NumberFormatException e) {
                    Toast.makeText(AddAccommodationActivity.this, "Invalid number format!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class InsertAccommodationTask extends AsyncTask<Void, Void, Boolean> {
        private AccommodationDAO accommodationDAO;
        private Accommodation accommodation;

        public InsertAccommodationTask(AccommodationDAO accommodationDAO, Accommodation accommodation) {
            this.accommodationDAO = accommodationDAO;
            this.accommodation = accommodation;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                accommodationDAO.insertAccommodation(accommodation);
                return true;
            } catch (Exception e) {
                Log.e("InsertAccommodationTask", "Error inserting accommodation: " + e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(AddAccommodationActivity.this, "Accommodation added successfully!", Toast.LENGTH_SHORT).show();

                // Redirect to another activity (e.g., AccommodationListActivity)
                Intent intent = new Intent(AddAccommodationActivity.this, AccommodationListAdapter.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AddAccommodationActivity.this, "Failed to add accommodation!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
