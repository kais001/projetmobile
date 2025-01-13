package tn.esprit.cov;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.cov.Entity.Ride;

public class UserRideAdapter extends RecyclerView.Adapter<UserRideAdapter.RideViewHolder> {
    private List<Ride> rideList;
    private OnRideClickListener onRideClickListener;

    public UserRideAdapter(List<Ride> rideList, OnRideClickListener listener) {
        this.rideList = rideList;
        this.onRideClickListener = listener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride_user, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.nameTextView.setText(ride.getName());
        holder.dateTextView.setText(ride.getDate());
        holder.destinationTextView.setText(ride.getDestination());
        holder.latitudeTextView.setText(String.valueOf(ride.getLatitude()));
        holder.longitudeTextView.setText(String.valueOf(ride.getLongitude()));

        holder.buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRideClickListener != null) {
                    onRideClickListener.onRideSelected(ride); // Trigger callback when selected
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRideList(List<Ride> newRideList) {
        this.rideList = newRideList;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    // ViewHolder for the RecyclerView
    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, destinationTextView, latitudeTextView, longitudeTextView;
        Button buttonSelect;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            destinationTextView = itemView.findViewById(R.id.textViewDestination);
            latitudeTextView = itemView.findViewById(R.id.textViewLatitude);
            longitudeTextView = itemView.findViewById(R.id.textViewLongitude);
            buttonSelect = itemView.findViewById(R.id.buttonSelect); // Reference the Select button
        }
    }

    // Listener interface to notify when a ride is selected
    public interface OnRideClickListener {
        void onRideSelected(Ride ride);
    }
}
