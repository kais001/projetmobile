package com.example.mobhoussem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobhoussem.DAO.EventDAO;
import com.example.mobhoussem.DataBase.EventDB;
import com.example.mobhoussem.entity.Event;

import java.util.ArrayList;
import java.util.List;

public class All_EventF extends Fragment {

    private RecyclerView eventRecyclerView;
    private EventListAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private EventDAO eventDAO;

    public All_EventF() {
        // Required empty public constructor
    }

    public static All_EventF newInstance() {
        return new All_EventF();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_event, container, false);

        // Initialisation de RecyclerView
        eventRecyclerView = view.findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialisation de la base de données et du DAO
        eventDAO = EventDB.getInstance(requireContext()).eventDao();

        // Initialisation de l'adaptateur
        adapter = new EventListAdapter(eventList, new EventListAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(Event event) {
                // Lancer une activité pour modifier l'événement
                Intent intent = new Intent(getContext(), UpdateEventActivity.class);
                intent.putExtra("EVENT_ID", event.getEventId());
                startActivityForResult(intent, 1); // Code de demande pour l'activité
            }

            @Override
            public void onDeleteClick(Event event) {
                // Supprimer un événement via une tâche en arrière-plan
                new DeleteEventTask().execute(event);
            }
        });
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

    // Tâche pour supprimer un événement en arrière-plan
    private class DeleteEventTask extends AsyncTask<Event, Void, Void> {
        @Override
        protected Void doInBackground(Event... events) {
            eventDAO.deleteEvent(events[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadEvents(); // Recharger les données après suppression
        }
    }

    // Mise à jour de la liste après l'update dans l'activité précédente
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Si l'update a été effectué, recharger la liste des événements
            loadEvents();
        }
    }
}
