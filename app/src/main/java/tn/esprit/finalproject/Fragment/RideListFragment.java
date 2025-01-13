package tn.esprit.finalproject.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.List;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Ride;
import tn.esprit.finalproject.R;



public class RideListFragment extends Fragment {

    private ListView listViewRides;
    private tn.esprit.finalproject.RideListAdapter rideListAdapter;
    private List<Ride> rideList;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride_list, container, false);

        // Initialize the Room database instance
        db = AppDatabase.getInstance(getContext());

        // Get the ListView and set the adapter
        listViewRides = view.findViewById(R.id.recyclerViewRides);

        // Fetch data from Room database and update the adapter
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform database operations off the main thread
                rideList = db.rideDao().getAllRides();  // Get the list of rides from Room
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Run this code on the main thread to update UI
                        rideListAdapter = new tn.esprit.finalproject.RideListAdapter(rideList, getContext());  // Create an adapter
                        listViewRides.setAdapter((ListAdapter) rideListAdapter);  // Set the adapter to ListView
                    }
                });
            }
        }).start();

        return view; // Ensure this is returned from the onCreateView method
    }
}



