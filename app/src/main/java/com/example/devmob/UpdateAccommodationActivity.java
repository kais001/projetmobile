package com.example.devmob;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.devmob.DAO.AccommodationDAO;
import com.example.devmob.DataBase.AccommodationDB;
import com.example.devmob.entity.Accommodation;

public class UpdateAccommodationActivity extends AppCompatActivity {

    private long accommodationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_accommodation);

        // Récupérer l'ID de l'accommodation à partir de l'intent
        accommodationId = getIntent().getLongExtra("ACCOMMODATION_ID", -1);

        if (accommodationId != -1) {
            new LoadAccommodationTask().execute(accommodationId);
        } else {
            Toast.makeText(this, "Accommodation not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onSaveButtonClick(View view) {
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText addressEditText = findViewById(R.id.addressEditText);
        EditText capacityEditText = findViewById(R.id.capacityEditText);
        EditText latitudeEditText = findViewById(R.id.latitudeEditText);
        EditText longitudeEditText = findViewById(R.id.longitudeEditText);
        EditText priceEditText = findViewById(R.id.priceEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);

        try {
            // Récupérer et valider les valeurs saisies
            String name = nameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String capacityText = capacityEditText.getText().toString().trim();
            String priceText = priceEditText.getText().toString().trim();
            String latitudeText = latitudeEditText.getText().toString().trim();
            String longitudeText = longitudeEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            // Vérification des champs vides
            if (name.isEmpty() || address.isEmpty() || capacityText.isEmpty() || priceText.isEmpty() ||
                    latitudeText.isEmpty() || longitudeText.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérification de la capacité (doit être supérieure à 0)
            int capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                Toast.makeText(this, "Capacity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Vérification des formats numériques pour le prix, la latitude, et la longitude
            double price = Double.parseDouble(priceText);
            double latitude = Double.parseDouble(latitudeText);
            double longitude = Double.parseDouble(longitudeText);

            // Créer un objet Accommodation mis à jour
            Accommodation updatedAccommodation = new Accommodation(name, address, capacity, latitude, longitude, price, description);
            updatedAccommodation.setAccommodationId(accommodationId);

            // Lancer la tâche asynchrone pour mettre à jour
            new UpdateAccommodationTask().execute(updatedAccommodation);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Veuillez vérifier les champs numériques", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadAccommodationTask extends AsyncTask<Long, Void, Accommodation> {
        @Override
        protected Accommodation doInBackground(Long... ids) {
            AccommodationDAO accommodationDao = AccommodationDB.getInstance(getApplicationContext()).accommodationDao();
            return accommodationDao.getAccommodationById(ids[0]);
        }

        @Override
        protected void onPostExecute(Accommodation accommodation) {
            if (accommodation != null) {
                // Remplir les champs avec les données existantes
                ((EditText) findViewById(R.id.nameEditText)).setText(accommodation.getName());
                ((EditText) findViewById(R.id.addressEditText)).setText(accommodation.getAddress());
                ((EditText) findViewById(R.id.capacityEditText)).setText(String.valueOf(accommodation.getCapacity()));
                ((EditText) findViewById(R.id.latitudeEditText)).setText(String.valueOf(accommodation.getLatitude()));
                ((EditText) findViewById(R.id.longitudeEditText)).setText(String.valueOf(accommodation.getLongitude()));
                ((EditText) findViewById(R.id.priceEditText)).setText(String.valueOf(accommodation.getPricePerNight()));
                ((EditText) findViewById(R.id.descriptionEditText)).setText(accommodation.getDescription());
            } else {
                Toast.makeText(UpdateAccommodationActivity.this, "Accommodation not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class UpdateAccommodationTask extends AsyncTask<Accommodation, Void, Void> {
        @Override
        protected Void doInBackground(Accommodation... accommodations) {
            // Mise à jour de l'accommodation dans la base de données
            AccommodationDAO accommodationDao = AccommodationDB.getInstance(getApplicationContext()).accommodationDao();
            accommodationDao.updateAccommodation(accommodations[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Afficher un message de succès
            Toast.makeText(UpdateAccommodationActivity.this, "Accommodation updated successfully", Toast.LENGTH_SHORT).show();

            // Retourner le résultat à l'activité précédente
            setResult(RESULT_OK); // Signaler que l'update a été réussie
            finish(); // Fermer l'activité pour revenir à la précédente
        }
    }
}
