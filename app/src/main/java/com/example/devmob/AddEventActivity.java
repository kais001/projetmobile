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

import com.example.devmob.DAO.EventDAO;
import com.example.devmob.DataBase.AccommodationDB;
import com.example.devmob.entity.Event;

public class AddEventActivity extends AppCompatActivity {
    AccommodationDB appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize the database
        appDatabase = AccommodationDB.getInstance(this);

        // Find views
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText addressEditText = findViewById(R.id.addressEditText);
        EditText priceEditText = findViewById(R.id.priceEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input values
                String name = nameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String priceString = priceEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                // Validate fields
                if (name.isEmpty() || address.isEmpty() || priceString.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddEventActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Parse input values
                    double price = Double.parseDouble(priceString);

                    // Create an event object
                    Event newEvent = new Event(name, address, price, description);

                    // Insert into database asynchronously
                    new InsertEventTask(appDatabase.eventDao(), newEvent).execute();

                } catch (NumberFormatException e) {
                    Toast.makeText(AddEventActivity.this, "Invalid number format!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class InsertEventTask extends AsyncTask<Void, Void, Boolean> {
        private EventDAO eventDAO;
        private Event event;

        public InsertEventTask(EventDAO eventDAO, Event event) {
            this.eventDAO = eventDAO;
            this.event = event;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                eventDAO.insertEvent(event);
                return true;
            } catch (Exception e) {
                Log.e("InsertEventTask", "Error inserting event: " + e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(AddEventActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();

                // Redirect to another activity (e.g., EventListActivity)
                Intent intent = new Intent(AddEventActivity.this, EventListAdapter.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AddEventActivity.this, "Failed to add event!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
