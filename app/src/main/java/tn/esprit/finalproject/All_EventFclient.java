package tn.esprit.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

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
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);

        // Dynamically update navigation header
        updateNavigationHeader();

        // Setup toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Events");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                requireActivity(), drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialisation de la base de données et du DAO
        eventDAO = AppDatabase.getInstance(requireContext()).eventDao();

        // Initialisation de l'adaptateur
        adapter = new EventListAdapter_Client(eventList, requireActivity().getSupportFragmentManager());
        eventRecyclerView.setAdapter(adapter);

        // Charger les données
        loadEvents();

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

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

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navigateToHomeFragment();
        } else if (id == R.id.nav_user_profile) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new UserProfileFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_logout) {
            logoutUser();
        } else if (id == R.id.nav_kais) {
            navigateToKais();
        } else if (id == R.id.nav_houssem) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_Step) {
            navigateToSteps();
        } else if (id == R.id.nav_aziz) {
            navigateToAziz();
        } else if (id == R.id.nav_ahmed) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void updateNavigationHeader() {
        if (navigationView != null && navigationView.getHeaderCount() > 0) {
            View headerView = navigationView.getHeaderView(0);

            // Find TextViews in the header
            TextView navHeaderTitle = headerView.findViewById(R.id.nav_header_title);
            TextView navHeaderSubtitle = headerView.findViewById(R.id.nav_header_subtitle);

            // Fetch username and email from SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "Default User");
            String email = sharedPreferences.getString("email", "default@example.com");

            // Set the values in the header
            navHeaderTitle.setText("Hello, " + username + "!");
            navHeaderSubtitle.setText(email);
        }
    }

    private void navigateToAziz() {
        // Navigate to HomeFragment
        Intent registerIntent = new Intent(requireContext(), ShowPlacesActivity.class);
        startActivity(registerIntent); // Launch AddEventActivity
    }

    private void logoutUser() {
        Context context = requireContext();
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
                .apply();

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private void navigateToKais() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new All_AccF_Client());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToHoussem() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new All_EventFclient());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToHomeFragment() {
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UserHomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToSteps() {
        // Navigate to HomeFragment
        Intent registerIntent = new Intent(requireContext(), StepCounterActivity.class);
        startActivity(registerIntent); // Launch AddEventActivity
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNavigationHeader();
    }
}
