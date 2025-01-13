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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.List;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class RecyclerViewFragment extends Fragment implements UserAdapter.OnEditClickListener {

    private UserAdapter userAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userAdapter = new UserAdapter(requireContext(), this); // Pass the OnEditClickListener
        recyclerView.setAdapter(userAdapter);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = view.findViewById(R.id.drawer_layout);
        navigationView = view.findViewById(R.id.nav_view);

        // Set up toolbar with toggle for drawer
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("User List");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle navigation menu item clicks
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Fetch and display users from the database
        fetchAndDisplayUsers();

        // Dynamically update navigation header
        updateNavigationHeader();

        return view;
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Navigate back to HomeFragment
            navigateToHomeFragment();
        } else if (id == R.id.nav_show_users) {
            // Navigate to ShowUsersFragment
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_logout) {
            // Handle Logout
            logoutUser();
        } else if (id == R.id.nav_kais) {
            navigateToKais();
        } else if (id == R.id.nav_kais2) {
            navigateToKais2();
        } else if (id == R.id.nav_houssem) {
            navigateToHoussem();
        } else if (id == R.id.nav_houssem2) {
            navigateToHoussem2();
        } else if (id == R.id.nav_ahmed) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void navigateToKais() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new All_AccF());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToKais2() {
        // Navigate to HomeFragment
        Intent registerIntent = new Intent(requireContext(), AddAccommodationActivity.class);
        startActivity(registerIntent); // Launch AddEventActivity

    }

    private void navigateToHoussem() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new All_EventF());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToHoussem2() {
        // Navigate to HomeFragment
        Intent registerIntent = new Intent(requireContext(), AddEventActivity.class);
        startActivity(registerIntent); // Launch AddEventActivity

    }

    private void logoutUser() {
        // Clear shared preferences and navigate to LoginFragment
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("isLoggedIn", false)
                .apply();

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    private void navigateToHomeFragment() {
        // Navigate to HomeFragment
        FragmentTransaction transaction = requireFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchAndDisplayUsers() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            List<User> userList = db.userDao().getAllUsers();

            requireActivity().runOnUiThread(() -> {
                if (userList.isEmpty()) {
                    Toast.makeText(requireContext(), "No users found!", Toast.LENGTH_SHORT).show();
                } else {
                    userAdapter.setUsers(userList);
                }
            });
        }).start();
    }

    @Override
    public void onEditClick(User user) {
        UpdateUserDialogFragment dialogFragment = UpdateUserDialogFragment.newInstance(user);

        dialogFragment.setOnUserUpdatedListener(updatedUser -> {
            // Update the user in the Room database
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                db.userDao().update(updatedUser); // Update the user in Room

                requireActivity().runOnUiThread(() -> {
                    userAdapter.updateUser(updatedUser);
                    Toast.makeText(requireContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
        dialogFragment.show(getChildFragmentManager(), "update_user");
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
