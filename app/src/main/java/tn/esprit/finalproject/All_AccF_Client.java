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
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);

        // Dynamically update navigation header
        updateNavigationHeader();

        // Setup toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Accommodations");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                requireActivity(), drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialisation de la base de données et du DAO
        accommodationDAO = AppDatabase.getInstance(requireContext()).accommodationDao();

        // Initialisation de l'adaptateur
        adapter = new AccommodationListAdapter_Client(accommodationList, requireActivity().getSupportFragmentManager());
        accommodationRecyclerView.setAdapter(adapter);

        // Charger les données
        loadAccommodations();

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        return view;
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
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_houssem) {
            navigateToHoussem();
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


    private void loadAccommodations() {
        new LoadAccommodationsTask().execute();
    }

    // Tâche pour charger les accommodations en arrière-plan
    private class LoadAccommodationsTask extends AsyncTask<Void, Void, List<Accommodation>> {
        @Override
        protected List<Accommodation> doInBackground(Void... voids) {
            return accommodationDAO.getAllAccommodations();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<Accommodation> accommodations) {
            accommodationList.clear();
            accommodationList.addAll(accommodations);
            adapter.notifyDataSetChanged();
        }
    }
}
