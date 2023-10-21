package com.example.projectfinal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinal.MainActivity;
import com.example.projectfinal.ObserveActivity;
import com.example.projectfinal.R;
import com.example.projectfinal.db.entity.Hike;

import java.util.ArrayList;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> implements Filterable {
    // Variables
    private Context context;
    private ArrayList<Hike> hikesList;

    // This list is used to store all hikes (before filtering).
    private ArrayList<Hike> hikesListAll;

    // A reference to the MainActivity.
    private MainActivity mainActivity;

    // Inner class representing the ViewHolder for each item in the RecyclerView.
    public class HikeViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView location;

        public HikeViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views within the ViewHolder.
            this.name = itemView.findViewById(R.id.txtName);
            this.location = itemView.findViewById(R.id.txtLocation);
        }
    }

    // Constructor for the adapter.
    public HikeAdapter(Context context, ArrayList<Hike> hikes, MainActivity mainActivity) {
        this.context = context;
        this.hikesList = hikes;
        // Make a copy of the original list to be used for filtering.
        this.hikesListAll = new ArrayList<>();
        this.hikesListAll.addAll(hikes);
        this.mainActivity = mainActivity;
    }

    // onCreateViewHolder is called when creating a new ViewHolder.
    @NonNull
    @Override
    public HikeAdapter.HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item and return a new ViewHolder.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_hike, parent, false);
        return new HikeViewHolder(itemView);
    }

    // onBindViewHolder is called to bind data to a ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull HikeAdapter.HikeViewHolder holder, @SuppressLint("RecyclerView") int positions) {
        final Hike hike = hikesList.get(positions);

        // Set the name and location views in the ViewHolder.
        holder.name.setText(hike.getName());
        holder.location.setText(hike.getLocation());

        // Handle click events on the edit button.
        ImageButton editButton = holder.itemView.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method in the MainActivity to create or update the hike.
                mainActivity.createOrUpdateHike(true, hike, positions);
            }
        });

        // Handle click events on the list item to navigate to the hike's detail view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to go to the detail hike's view and pass the hike ID.
                Intent intent = new Intent(context, ObserveActivity.class);
                intent.putExtra("hikeId", hike.getId());
                Log.d("Hike ID", String.valueOf(hike.getId()));
                context.startActivity(intent);
            }
        });
    }

    // Returns the total number of items in the RecyclerView.
    @Override
    public int getItemCount() {
        return hikesList.size();
    }

    // Implement the Filterable interface to enable filtering of the list.
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    // Define the filter logic using a Filter object.
    Filter myFilter = new Filter() {
        // This method performs filtering on a background thread.
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Hike> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                // If the search query is empty, show all hikes.
                filteredList.addAll(hikesListAll);
            } else {
                // Filter hikes based on the name.
                for (Hike hike : hikesListAll) {
                    if (hike.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(hike);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        // This method is called on the UI thread to update the RecyclerView with filtered data.
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if (filterResults.values != null) {
                // Update the list with the filtered results.
                hikesList.clear();
                hikesList.addAll((ArrayList<Hike>) filterResults.values);
            } else {
                // Handle the case when filterResults.values is null by showing the original unfiltered list.
                hikesList.clear();
                hikesList.addAll(hikesListAll);
            }
            notifyDataSetChanged(); // Notify the adapter of data changes.
        }
    };
}


