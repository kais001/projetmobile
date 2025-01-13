package tn.esprit.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class UserHomeFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        // Initialize views
        TextView welcomeTextView = view.findViewById(R.id.welcomeTextView);
        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);
        Button iconSettings = view.findViewById(R.id.icon_settings);
        Button iconLogout = view.findViewById(R.id.icon_logout);
        Button iconKais = view.findViewById(R.id.icon_kais);
        Button iconHoussem = view.findViewById(R.id.icon_houssem);
        Button iconAziz = view.findViewById(R.id.icon_aziz);
        Button iconSteps = view.findViewById(R.id.icon_steps);

        // Setup toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Home");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                requireActivity(), drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set welcome message
        welcomeTextView.setText("Welcome to the App!");

        // Dynamically update navigation header
        updateNavigationHeader();

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Set onClickListeners for the icons
        iconSettings.setOnClickListener(v -> openSettings());
        iconLogout.setOnClickListener(v -> logoutUser());
        iconKais.setOnClickListener(v -> navigateToKais());
        iconHoussem.setOnClickListener(v -> navigateToHoussem());
        iconAziz.setOnClickListener(v -> navigateToAziz());
        iconSteps.setOnClickListener(v -> navigateToSteps());

        return view;
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
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

    private void openSettings() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new UserProfileFragment())
                .addToBackStack(null)
                .commit();
    }

    private void logoutUser() {
        Context context = requireContext();
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", false)
                .apply();

        // Navigate to LoginFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
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



    @Override
    public void onResume() {
        super.onResume();
        updateNavigationHeader();
    }

    private void navigateToKais() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new All_AccF_Client());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToSteps() {
        // Navigate to HomeFragment
        Intent registerIntent = new Intent(requireContext(), StepCounterActivity.class);
        startActivity(registerIntent); // Launch AddEventActivity
    }

    private void navigateToAziz() {
        // Navigate to HomeFragment
        Intent registerIntent = new Intent(requireContext(), ShowPlacesActivity.class);
        startActivity(registerIntent); // Launch AddEventActivity
    }

    private void navigateToHoussem() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new All_EventFclient());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
