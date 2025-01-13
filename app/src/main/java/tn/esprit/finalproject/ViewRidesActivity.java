package tn.esprit.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Ride;
public class ViewRidesActivity extends AppCompatActivity implements UserRideAdapter.OnRideClickListener {

    private RecyclerView recyclerViewAvailableRides;
    private UserRideAdapter rideAdapter;
    private AppDatabase db;
    private ExecutorService executorService;
    private List<Ride> allRides; // Store the unfiltered list of rides
    private List<Ride> filteredRides; // Store the filtered list of rides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rides);

        // Initialize the Room database and ExecutorService
        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        // Set up RecyclerView
        recyclerViewAvailableRides = findViewById(R.id.recyclerViewAvailableRides);
        recyclerViewAvailableRides.setLayoutManager(new LinearLayoutManager(this));

        rideAdapter = new UserRideAdapter(new ArrayList<>(), this); // Pass this as listener
        recyclerViewAvailableRides.setAdapter(rideAdapter);

        // Set up SearchView
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterRides(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterRides(newText);
                return false;
            }
        });

        // Load the available rides from the database
        loadAvailableRides();
    }

    private void loadAvailableRides() {
        // Fetch the list of available rides from the database on a background thread
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Fetching all rides from database
                List<Ride> rideList = db.rideDao().getAllRides();

                // Store the unfiltered list
                allRides = rideList;
                filteredRides = new ArrayList<>(allRides); // Initially, show all rides

                // Update the RecyclerView on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rideAdapter.updateRideList(filteredRides);
                    }
                });
            }
        });
    }

    private void filterRides(String query) {
        // Filter the list based on the destination
        List<Ride> filteredList = new ArrayList<>();
        for (Ride ride : allRides) {
            if (ride.getDestination().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(ride);
            }
        }

        // Update the RecyclerView with the filtered list
        filteredRides = filteredList;
        rideAdapter.updateRideList(filteredRides);
    }

    @Override
    public void onRideSelected(Ride ride) {
        // Handle ride selection
        Toast.makeText(this, "Ride selected: " + ride.getName(), Toast.LENGTH_SHORT).show();
        // Navigate to the map activity or perform any other action
        // For example, using Intent to pass data
        Intent intent = new Intent(ViewRidesActivity.this, UserMapActivity.class);
        intent.putExtra("latitude", ride.getLatitude());
        intent.putExtra("longitude", ride.getLongitude());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the ExecutorService to release resources
    }
}
