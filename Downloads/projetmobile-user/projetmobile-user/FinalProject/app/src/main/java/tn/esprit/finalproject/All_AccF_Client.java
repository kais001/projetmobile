package tn.esprit.finalproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tn.esprit.finalproject.DAO.AccommodationDAO;
import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Accommodation;

import java.util.ArrayList;
import java.util.List;

public class All_AccF_Client extends Fragment {

    private RecyclerView accommodationRecyclerView;
    private AccommodationListAdapter_Client adapter;
    private List<Accommodation> accommodationList = new ArrayList<>();
    private AccommodationDAO accommodationDAO;

    public All_AccF_Client() {
        // Required empty public constructor
    }

    public static All_AccF_Client newInstance() {
        return new All_AccF_Client();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_accommodation_client, container, false);

        // Initialisation de RecyclerView
        accommodationRecyclerView = view.findViewById(R.id.accommodationRecyclerView);
        accommodationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialisation de la base de données et du DAO
        accommodationDAO = AppDatabase.getInstance(requireContext()).accommodationDao();

        // Initialisation de l'adaptateur
        adapter = new AccommodationListAdapter_Client(accommodationList, requireActivity().getSupportFragmentManager());
        accommodationRecyclerView.setAdapter(adapter);

        // Charger les données
        loadAccommodations();

        return view;
    }

    private void loadAccommodations() {
        new LoadAccommodationsTask().execute();
    }

    // Tâche pour charger les accommodations en arrière-plan
    private class LoadAccommodationsTask extends AsyncTask<Void, Void, List<Accommodation>> {
        @Override
        protected List<Accommodation> doInBackground(Void... voids) {
            return accommodationDAO.getAllAccommodations();
        }

        @Override
        protected void onPostExecute(List<Accommodation> accommodations) {
            accommodationList.clear();
            accommodationList.addAll(accommodations);
            adapter.notifyDataSetChanged();
        }
    }
}
