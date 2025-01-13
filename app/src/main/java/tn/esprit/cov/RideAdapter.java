package tn.esprit.cov;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.cov.Entity.Ride;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private List<Ride> rideList;
    private OnDeleteClickListener deleteClickListener;
    private OnUpdateClickListener updateClickListener;

    public RideAdapter(List<Ride> rideList, OnDeleteClickListener deleteClickListener, OnUpdateClickListener updateClickListener) {
        this.rideList = rideList;
        this.deleteClickListener = deleteClickListener;
        this.updateClickListener = updateClickListener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.nameTextView.setText(ride.getName());
        holder.dateTextView.setText(ride.getDate());
        holder.destinationTextView.setText(ride.getDestination());
        holder.longitudeTextView.setText(String.valueOf(ride.getLongitude()));
        holder.latitudeTextView.setText(String.valueOf(ride.getLatitude()));

        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) { // Ensure the listener is not null
                deleteClickListener.onDeleteClick(ride);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Delete action not available", Toast.LENGTH_SHORT).show();
            }
        });

        holder.buttonUpdate.setOnClickListener(v -> {
            if (updateClickListener != null) {
                updateClickListener.onUpdateClick(ride);
            } else {
                Toast.makeText(holder.itemView.getContext(), "Update action not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public interface OnUpdateClickListener {
        void onUpdateClick(Ride ride);
    }

    // Method to update the list of rides in the adapter
    public void updateRideList(List<Ride> newRideList) {
        this.rideList = newRideList;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, dateTextView, destinationTextView, longitudeTextView, latitudeTextView;
        Button buttonDelete, buttonUpdate;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            destinationTextView = itemView.findViewById(R.id.textViewDestination);
            longitudeTextView = itemView.findViewById(R.id.textViewLongitude);
            latitudeTextView = itemView.findViewById(R.id.textViewLatitude);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Ride ride);
    }
}
