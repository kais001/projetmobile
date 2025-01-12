package tn.esprit.travelcompanionapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import tn.esprit.travelcompanionapp.R;
import tn.esprit.travelcompanionapp.models.Place;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Place place);
    }

    private final Context context;
    private final List<Place> places;
    private final OnItemClickListener listener;

    public PlaceAdapter(Context context, List<Place> places, OnItemClickListener listener) {
        this.context = context;
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);

        // Set text for name and state
        holder.name.setText(place.getName());
        holder.state.setText(place.getState());

        // Load image using Glide
        Glide.with(context)
                .load(place.getPhotoUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("GlideError", "Image load failed", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GlideSuccess", "Image loaded successfully");
                        return false;
                    }
                })
                .into(holder.image);

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(place));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, state;
        ImageView image; // Added ImageView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.placeName);
            state = itemView.findViewById(R.id.placeState);
            image = itemView.findViewById(R.id.placeImage); // Initialize ImageView
        }
    }
}
