package com.example.projectfinal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinal.ObservationActivity;
import com.example.projectfinal.R;
import com.example.projectfinal.db.entity.Observation;

import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {
    //  Variables
    private Context context;
    private ArrayList<Observation> observationsList;
    private ObservationActivity observationActivity;

    //  ViewHolder
    public class ObservationViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView time;
        public ImageButton editButton;

        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.time = itemView.findViewById(R.id.time);
            this.editButton = itemView.findViewById(R.id.editButton);
        }
    }

    //  Constructor
    public ObservationAdapter(Context context, ArrayList<Observation> observations, ObservationActivity observationActivity) {
        this.context = context;
        this.observationsList = observations;
        this.observationActivity = observationActivity;
    }

    //  Create ViewHolder
    @NonNull
    @Override
    public ObservationAdapter.ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_obs, parent, false);
        return new ObservationViewHolder(itemView);
    }

    //  Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ObservationAdapter.ObservationViewHolder holder, @SuppressLint("RecyclerView") int positions) {
        final Observation observation = observationsList.get(positions);
        holder.name.setText(observation.getName());
        holder.time.setText(observation.getTime());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observationActivity.addAndEditObservation(true, observation, positions, observation.getHikeId());
            }
        });

        //open popup on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observationActivity.openObsDetail(observation);
            }
        });
    }

    //create new observation
    public void createNewObservation(Observation observation) {
        observationsList.add(observation);
        notifyDataSetChanged();
    }

    //update observation
    public void updateObservation(Observation observation, int position) {
        observationsList.set(position, observation);
        notifyDataSetChanged();
    }

    //delete observation
    public void deleteObservation(int position) {
        observationsList.remove(position);
        notifyDataSetChanged();
    }



    //  Get number of items in list
    @Override
    public int getItemCount() {
        return observationsList.size();
    }
}