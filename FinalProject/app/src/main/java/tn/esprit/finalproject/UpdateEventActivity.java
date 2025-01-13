package tn.esprit.finalproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tn.esprit.finalproject.DAO.EventDAO;
import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Event;

public class UpdateEventActivity extends AppCompatActivity {

    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_event); // Assurez-vous d'avoir ce layout

        // Récupérer l'ID de l'événement à partir de l'intent
        eventId = getIntent().getLongExtra("EVENT_ID", -1);

        if (eventId != -1) {
            new LoadEventTask().execute(eventId);
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onSaveButtonClick(View view) {
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText locationEditText = findViewById(R.id.addressEditText);
        EditText priceEditText = findViewById(R.id.priceEditText);
        EditText descriptionEditText = findViewById(R.id.descriptionEditText);

        try {
            // Récupérer et valider les valeurs saisies
            String name = nameEditText.getText().toString().trim();
            String address = locationEditText.getText().toString().trim();
            String priceString = priceEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            // Vérification des champs vides
            if (name.isEmpty() || address.isEmpty() || priceString.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir le prix en double
            double price = Double.parseDouble(priceString);

            // Créer un objet Event mis à jour
            Event updatedEvent = new Event(name, address, price, description);
            updatedEvent.setEventId(eventId);

            // Lancer la tâche asynchrone pour mettre à jour
            new UpdateEventTask().execute(updatedEvent);

        } catch (NumberFormatException e) {
            // Si la conversion échoue, afficher un message d'erreur
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred. Please check your input.", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadEventTask extends AsyncTask<Long, Void, Event> {
        @Override
        protected Event doInBackground(Long... ids) {
            EventDAO eventDao = AppDatabase.getInstance(getApplicationContext()).eventDao();
            return eventDao.getEventById(ids[0]);
        }

        @Override
        protected void onPostExecute(Event event) {
            if (event != null) {
                // Remplir les champs avec les données existantes
                ((EditText) findViewById(R.id.nameEditText)).setText(event.getName());
                ((EditText) findViewById(R.id.addressEditText)).setText(event.getAddress());
                // Convertir le prix en String pour l'afficher dans l'EditText
                ((EditText) findViewById(R.id.priceEditText)).setText(String.valueOf(event.getPrice()));
                ((EditText) findViewById(R.id.descriptionEditText)).setText(event.getDescription());
            } else {
                Toast.makeText(UpdateEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private class UpdateEventTask extends AsyncTask<Event, Void, Void> {
        @Override
        protected Void doInBackground(Event... events) {
            // Mise à jour de l'événement dans la base de données
            EventDAO eventDao = AppDatabase.getInstance(getApplicationContext()).eventDao();
            eventDao.updateEvent(events[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Afficher un message de succès
            Toast.makeText(UpdateEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();

            // Retourner le résultat à l'activité précédente
            setResult(RESULT_OK); // Signaler que l'update a été réussie
            finish(); // Fermer l'activité pour revenir à la précédente
        }
    }
}
