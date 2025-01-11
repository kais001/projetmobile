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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        // Clear shared preferences and navigate to LoginFragment
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .edit()
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

                // Update the Firestore database
                updateFirestoreUser(updatedUser);

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

    private void updateFirestoreUser(User updatedUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", updatedUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("username", updatedUser.getUsername());
                        updatedData.put("email", updatedUser.getEmail());
                        updatedData.put("emailVerified", updatedUser.isEmailVerified());
                        updatedData.put("password", updatedUser.getPassword());

                        document.getReference().update(updatedData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Firestore updated successfully", Toast.LENGTH_SHORT).show();
                                    updatePasswordInFirebase(updatedUser.getPassword());
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Failed to update Firestore", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(requireContext(), "User not found in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePasswordInFirebase(String newPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Password updated successfully in Firebase Authentication", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update password in Firebase Authentication", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }

}