package com.example.devmob;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.devmob.DAO.AccommodationDAO;
import com.example.devmob.DataBase.AccommodationDB;
import com.example.devmob.entity.Accommodation;
import java.util.ArrayList;
import java.util.List;

public class All_AccF extends Fragment {

    private RecyclerView accommodationRecyclerView;
    private AccommodationListAdapter adapter;
    private List<Accommodation> accommodationList = new ArrayList<>();
    private AccommodationDAO accommodationDAO;

    public All_AccF() {
        // Required empty public constructor
    }

    public static All_AccF newInstance() {
        return new All_AccF();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_accommodation, container, false);

        // Initialisation de RecyclerView
        accommodationRecyclerView = view.findViewById(R.id.accommodationRecyclerView);
        accommodationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialisation de la base de données et du DAO
        accommodationDAO = AccommodationDB.getInstance(requireContext()).accommodationDao();

        // Initialisation de l'adaptateur
        adapter = new AccommodationListAdapter(accommodationList, new AccommodationListAdapter.OnAccommodationClickListener() {
            @Override
            public void onAccommodationClick(Accommodation accommodation) {
                // Lancer une activité pour modifier l'accommodation
                Intent intent = new Intent(getContext(), UpdateAccommodationActivity.class);
                intent.putExtra("ACCOMMODATION_ID", accommodation.getAccommodationId());
                startActivityForResult(intent, 1); // Code de demande pour l'activité
            }

            @Override
            public void onDeleteClick(Accommodation accommodation) {
                // Supprimer un logement via une tâche en arrière-plan
                new DeleteAccommodationTask().execute(accommodation);
            }
        });
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

    // Tâche pour supprimer une accommodation en arrière-plan
    private class DeleteAccommodationTask extends AsyncTask<Accommodation, Void, Void> {
        @Override
        protected Void doInBackground(Accommodation... accommodations) {
            accommodationDAO.deleteAccommodation(accommodations[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadAccommodations(); // Recharger les données après suppression
        }
    }

    // Mise à jour de la liste après l'update dans l'activité précédente
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Si l'update a été effectué, recharger la liste des accommodations
            loadAccommodations();
        }
    }
}
