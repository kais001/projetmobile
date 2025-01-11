package tn.esprit.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        TextView welcomeTextView = view.findViewById(R.id.welcomeTextView);
        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);

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
        welcomeTextView.setText("Admin, Welcome to the App!");

        // Dynamically update navigation header
        updateNavigationHeader();

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        return view;
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle Home click
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_show_users) {
            // Navigate to RecyclerViewFragment to show users
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new RecyclerViewFragment())
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_logout) {
            // Handle Logout
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        Context context = requireContext();
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
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
}
