package tn.esprit.finalproject;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import tn.esprit.finalproject.Database.AppDatabase;
import tn.esprit.finalproject.Entity.User;

public class UpdateUserDialogFragment extends DialogFragment {

    private EditText usernameEditText, updateConfirmPasswordEditText, updatePasswordEditText;
    private User user;
    private OnUserUpdatedListener listener;

    public interface OnUserUpdatedListener {
        void onUserUpdated(User updatedUser);
    }

    public void setOnUserUpdatedListener(OnUserUpdatedListener listener) {
        this.listener = listener;
    }

    public static UpdateUserDialogFragment newInstance(User user) {
        UpdateUserDialogFragment fragment = new UpdateUserDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_user, container, false);

        usernameEditText = view.findViewById(R.id.updateUsernameEditText);
        updateConfirmPasswordEditText = view.findViewById(R.id.updateConfirmPasswordEditText);
        updatePasswordEditText = view.findViewById(R.id.updatePasswordEditText);
        Button updateButton = view.findViewById(R.id.updateButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        ImageView togglePasswordVisibility = view.findViewById(R.id.togglePasswordVisibility);
        ImageView toggleConfirmPasswordVisibility = view.findViewById(R.id.toggleConfirmPasswordVisibility);

        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility(updatePasswordEditText, togglePasswordVisibility));
        toggleConfirmPasswordVisibility.setOnClickListener(v -> togglePasswordVisibility(updateConfirmPasswordEditText, toggleConfirmPasswordVisibility));

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            if (user != null) {
                usernameEditText.setText(user.getUsername());
            }
        }

        updateButton.setOnClickListener(v -> {
            String newUsername = usernameEditText.getText().toString().trim();
            String newPassword = updatePasswordEditText.getText().toString().trim();
            String confirmPassword = updateConfirmPasswordEditText.getText().toString().trim();

            if (validateInput(newUsername, newPassword, confirmPassword)) {
                user.setUsername(newUsername);

                if (!TextUtils.isEmpty(newPassword)) {
                    user.setPassword(newPassword);
                } else {
                    // Keep the old password
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(requireContext());
                        User currentUser = db.userDao().findById(user.getId());
                        if (currentUser != null) {
                            user.setPassword(currentUser.getPassword());
                        }
                    }).start();
                }

                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(requireContext());
                    db.userDao().update(user); // Update the Room database

                    requireActivity().runOnUiThread(() -> {
                        if (listener != null) {
                            listener.onUserUpdated(user); // Notify the listener to update the UI
                        }
                        Toast.makeText(getActivity(), "User updated successfully", Toast.LENGTH_SHORT).show();
                        dismiss(); // Close the dialog
                    });
                }).start();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private boolean validateInput(String username, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return false;
        }
        if (!TextUtils.isEmpty(password)) {
            if (password.length() < 6) {
                Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!password.matches(".*[A-Z].*")) {
                Toast.makeText(getActivity(), "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~].*")) {
                Toast.makeText(getActivity(), "Password must contain at least one special character", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_visibility_on);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.ic_visibility_off);
        }
        editText.setSelection(editText.getText().length());
    }
}
