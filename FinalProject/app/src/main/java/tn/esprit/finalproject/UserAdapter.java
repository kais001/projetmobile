package tn.esprit.finalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users = new ArrayList<>();
    private final Context context;
    private final OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void onEditClick(User user);
    }

    public UserAdapter(Context context, OnEditClickListener listener) {
        this.context = context;
        this.onEditClickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.emailTextView.setText(user.getEmail());
        holder.usernameTextView.setText(user.getUsername());
        holder.idTextView.setText(String.valueOf(user.getId()));

        // Handle edit button click
        holder.editButton.setOnClickListener(v -> {
            if (onEditClickListener != null) {
                onEditClickListener.onEditClick(user);
            }
        });

        // Delete button functionality
        holder.deleteButton.setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                db.userDao().delete(user);

                // Get the currently logged-in user's email from SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                String loggedInEmail = sharedPreferences.getString("email", "");

                // Remove the user from the list
                users.remove(position);

                // If the deleted user is the currently logged-in user, force logout
                if (user.getEmail().equals(loggedInEmail)) {
                    // Clear SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Navigate to the Login screen
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "User deleted. Logging out...", Toast.LENGTH_SHORT).show();
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).loadFragment(new LoginFragment());
                        }
                    });
                } else {
                    // Update UI for non-logged-in user deletion
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        notifyDataSetChanged();
                        Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView, idTextView, usernameTextView;
        Button deleteButton;
        Button editButton;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            idTextView = itemView.findViewById(R.id.idTextView);
            deleteButton = itemView.findViewById(R.id.deleteUserButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    public void updateUser(User updatedUser) {
        // Find and update the user in the list
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                notifyItemChanged(i); // Notify the adapter that the item has changed
                break;
            }
        }
    }
}
