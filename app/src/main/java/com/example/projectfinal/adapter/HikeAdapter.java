package com.example.projectfinal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinal.MainActivity;
import com.example.projectfinal.R;
import com.example.projectfinal.db.entity.Hike;

import java.util.ArrayList;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.HikeViewHolder> {
    //  Variables
    private Context context;
    private ArrayList<Hike> hikesList;
    private MainActivity mainActivity;


    public class HikeViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView location;

        public HikeViewHolder(@NonNull View itemView){
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.location = itemView.findViewById(R.id.location);
        }
    }

    public HikeAdapter(Context context, ArrayList<Hike> hikes, MainActivity mainActivity){
        this.context = context;
        this.hikesList = hikes;
        this.mainActivity = mainActivity;
    }


    @NonNull
    @Override
    public HikeAdapter.HikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.hike_list_layout,parent,false);
        return new HikeViewHolder(itemView);
    }

    //  Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull HikeAdapter.HikeViewHolder holder, @SuppressLint("RecyclerView") int positions) {
        final Hike hike = hikesList.get(positions);

        holder.name.setText(hike.getName());
        holder.location.setText(hike.getLocation());

        ImageButton editButton = holder.itemView.findViewById(R.id.editButton);


        editButton.setOnClickListener(new View.OnClickListener() {
              @Override
            public void onClick(View view) {
                mainActivity.addAndEditHike(true,hike, positions);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to detail hike's detail view
                Intent intent = new Intent(context, ObservationActivity.class);
                intent.putExtra("hikeId", hike.getId());
                Log.d(
                        "Hike ID",
                        String.valueOf(hike.getId())
                );
                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return hikesList.size();
    }
}