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

import com.example.projectfinal.ObserveActivity;
import com.example.projectfinal.R;
import com.example.projectfinal.db.entity.Observation;

import java.util.ArrayList;

public class ObserveAdapter extends RecyclerView.Adapter<ObserveAdapter.ObservationViewHolder> {
    //  Variables
    private final Context context;
    private final ArrayList<Observation> obsList;
    private final ObserveActivity observeActivity;

    //  ViewHolder
    public class ObservationViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView time;
        public ImageButton editButton;

        public ObservationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.txtObserveName);
            this.time = itemView.findViewById(R.id.txtObserveTime);
            this.editButton = itemView.findViewById(R.id.editButton);
        }
    }

    //  Constructor
    public ObserveAdapter(Context context, ArrayList<Observation> observations, ObserveActivity observeActivity) {
        this.context = context;
        this.obsList = observations;
        this.observeActivity = observeActivity;
    }

    //  Create ViewHolder
    @NonNull
    @Override
    public ObserveAdapter.ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_obs, parent, false);
        return new ObservationViewHolder(itemView);
    }

    //  Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ObserveAdapter.ObservationViewHolder holder, @SuppressLint("RecyclerView") int positions) {
        final Observation observation = obsList.get(positions);
        holder.name.setText(observation.getName());
        holder.time.setText(observation.getTime());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observeActivity.createOrUpdateObservation(true, observation, positions, observation.getHikeId());
            }
        });

        //open popup on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observeActivity.openObsDetail(observation);
            }
        });
    }

    //create new observation
    public void createNewObservation(Observation observation) {
        obsList.add(observation);
        notifyDataSetChanged();
    }

    //update observation
    public void updateObservation(Observation observation, int position) {
        obsList.set(position, observation);
        notifyDataSetChanged();
    }

    //delete observation
    public void deleteObservation(int position) {
        obsList.remove(position);
        notifyDataSetChanged();
    }



    //  Get number of items in list
    @Override
    public int getItemCount() {
        return obsList.size();
    }
}