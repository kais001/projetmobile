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

import tn.esprit.finalproject.DAO.EventDAO;
import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.Event;

import java.util.ArrayList;
import java.util.List;

public class All_EventFclient extends Fragment {

    private RecyclerView eventRecyclerView;
    private EventListAdapter_Client adapter;
    private List<Event> eventList = new ArrayList<>();
    private EventDAO eventDAO;

    public All_EventFclient() {
        // Required empty public constructor
    }

    public static All_EventFclient newInstance() {
        return new All_EventFclient();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_event_client, container, false);

        // Initialisation de RecyclerView
        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialisation de la base de données et du DAO
        eventDAO = AppDatabase.getInstance(requireContext()).eventDao();

        // Initialisation de l'adaptateur
        adapter = new EventListAdapter_Client(eventList, requireActivity().getSupportFragmentManager());
        eventRecyclerView.setAdapter(adapter);

        // Charger les données
        loadEvents();

        return view;
    }

    private void loadEvents() {
        new LoadEventsTask().execute();
    }

    // Tâche pour charger les événements en arrière-plan
    private class LoadEventsTask extends AsyncTask<Void, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(Void... voids) {
            return eventDAO.getAllEvents();
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            eventList.clear();
            eventList.addAll(events);
            adapter.notifyDataSetChanged();
        }
    }
}
