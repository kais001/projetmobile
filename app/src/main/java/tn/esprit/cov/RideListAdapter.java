package tn.esprit.cov.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.cov.Entity.Ride;
import tn.esprit.cov.R;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.RideViewHolder> {
    private List<Ride> rideList;
    private final Context context;

    public RideListAdapter(List<Ride> rideList, Context context) {
        this.rideList = rideList;
        this.context = context;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        // Bind the data to the view
        holder.nameTextView.setText(ride.getName());
        holder.dateTextView.setText(ride.getDate());
        holder.destinationTextView.setText(ride.getDestination());
        holder.latitudeTextView.setText(String.valueOf(ride.getLatitude()));
        holder.longitudeTextView.setText(String.valueOf(ride.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    // Update the ride list and refresh the adapter
    public void updateRideList(List<Ride> newRideList) {
        this.rideList = newRideList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView dateTextView;
        TextView destinationTextView;
        TextView latitudeTextView;
        TextView longitudeTextView;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views
            nameTextView = itemView.findViewById(R.id.textViewName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            destinationTextView = itemView.findViewById(R.id.textViewDestination);
            latitudeTextView = itemView.findViewById(R.id.textViewLatitude);
            longitudeTextView = itemView.findViewById(R.id.textViewLongitude);
        }
    }
}
