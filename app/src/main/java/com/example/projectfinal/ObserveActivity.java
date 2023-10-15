package com.example.projectfinal;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.projectfinal.adapter.ObservationAdapter;
import com.example.projectfinal.db.DatabaseHelper;
import com.example.projectfinal.db.entity.Hike;
import com.example.projectfinal.db.entity.Observation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;

public class ObservationActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private FloatingActionButton addNewObs;
    private ObservationAdapter observationAdapter;

    private ArrayList<Observation> observationArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_hike);

        //add return button to toolbar w copilot
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Hike Details");


        TextView hikeName = findViewById(R.id.hikeName);
        TextView hikeLocation = findViewById(R.id.hikeLocation);
        TextView hikeDate = findViewById(R.id.hikeDate);
        TextView hikeParking = findViewById(R.id.hikeParking);
        TextView hikeLength = findViewById(R.id.hikeLength);
        TextView hikeDifficulty = findViewById(R.id.hikeDifficulty);
        TextView hikeDescription = findViewById(R.id.hikeDescription);


        // Retrieve data passed from the source activity
        Intent intent = getIntent();
        if (intent != null) {

            RecyclerView obsList = findViewById(R.id.recycler_view_observations);
            long hikeId = intent.getLongExtra("hikeId", -1);
            db = new DatabaseHelper(this);
            Hike hike = db.getHike(hikeId);

            observationArrayList = db.getAllObservationsByHikeId(hikeId);

            observationAdapter = new ObservationAdapter(this, observationArrayList, this);
            obsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            obsList.setItemAnimator(new DefaultItemAnimator());
            obsList.setAdapter(observationAdapter);

            addNewObs = findViewById(R.id.addNewObservation);
            addNewObs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addAndEditObservation(false, null, -1, hikeId);
                }
            });

            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View popupView = layoutInflater.inflate(R.layout.popup_layout, null);
            RadioGroup hikeDifficultyGroup = popupView.findViewById(R.id.popupHikeDifficulty);
            RadioButton selectedDifficulty = popupView.findViewById(hike.getDifficulty());
            String selectedRadioButtonText = selectedDifficulty.getText().toString();

            hikeName.setText("Hike name: " + hike.getName());
            hikeLocation.setText("Hike location: " + hike.getLocation());
            hikeDate.setText("Hike date: " + hike.getDate());
            hikeParking.setText("Parking available: " + (hike.getParking() ? "Yes" : "No"));
            hikeLength.setText("Hike length: " + hike.getLength());
            hikeDifficulty.setText("Hike difficulty: " + selectedRadioButtonText);
            hikeDescription.setText("\"" + hike.getDescription() + "\"");




        }
    }

    public void openObsDetail(Observation observation){
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View popupView = layoutInflater.inflate(R.layout.detail_obs, null);
        //AlertDialog.builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ObservationActivity.this);
        alertDialogBuilder.setView(popupView);

        final TextView obsName = popupView.findViewById(R.id.popupObsName);
        final TextView obsTime = popupView.findViewById(R.id.popupObsTime);
        final TextView obsComment = popupView.findViewById(R.id.popupObsComment);

        obsName.setText("Name: "+observation.getName());
        obsTime.setText("Time: "+observation.getTime());
        obsComment.setText("\""+observation.getComment()+ "\"");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void addAndEditObservation(final boolean isUpdated, final Observation obs, final int position, final long hikeId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View popupView = layoutInflater.inflate(R.layout.obs_popup_layout, null);
        //AlertDialog.builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ObservationActivity.this);
        alertDialogBuilder.setView(popupView);

        TextView popupTitle = popupView.findViewById(R.id.popupTitle);
        final TextView obsName = popupView.findViewById(R.id.popupObsName);
        final Button obsTime = popupView.findViewById(R.id.popupObsTime);
        final TextView obsComment = popupView.findViewById(R.id.popupObsComment);

        if(!isUpdated && obsTime.getText().toString().isEmpty()){
            //set current time to default (HH:MM) format
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String time = hour + ":" + minute;
            obsTime.setText(time);
        }

        obsTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(obsTime);
            }
        });

        popupTitle.setText(isUpdated ? "Edit Hike" : "Add New Hike");

        if (isUpdated && obs != null) {
            obsName.setText(obs.getName());
            obsTime.setText(obs.getTime());
            obsComment.setText(obs.getComment());

        }

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(obsName.getText().toString())) {
                            Toast.makeText(ObservationActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.dismiss();
                        }

                        if (obsName.getText().toString().isEmpty()) {
                            Toast.makeText(ObservationActivity.this, "Hike's name invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (obsTime.getText().toString().isEmpty()|| obsTime.getText().toString().equals("Pick a time")) {
                            Toast.makeText(ObservationActivity.this, "Hike's location invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (isUpdated && obs != null) {
                            updateObservation(obsName.getText().toString(), obsTime.getText().toString(), obsComment.getText().toString(), position);
                        } else {
                            createObservation(obsName.getText().toString(), obsTime.getText().toString(), obsComment.getText().toString(), hikeId);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void createObservation(String name, String time, String comment, long hikeId) {
        long id = db.insertObservation(name, time, comment, hikeId);
        Observation obs = db.getObservation(id);

        if (obs != null) {
            observationAdapter.createNewObservation(obs);
        }
    }

    private void updateObservation(String name, String time, String comment, int position) {
        Observation obs = observationArrayList.get(position);
        obs.setName(name);
        obs.setTime(time);
        obs.setComment(comment);

        db.updateObservation(obs);
        observationAdapter.updateObservation(obs, position);
    }

    private void deleteObservation(Observation obs, int position){
        //create confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ObservationActivity.this);
        builder.setTitle("Delete Observation");
        builder.setMessage("Are you sure you want to delete this observation?");
        builder.setCancelable(true);
        //delete on confirm
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                db.deleteObservation(obs);
                observationAdapter.deleteObservation(position);
                dialog.dismiss();
            }
        });
        //cancel delete
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                observationAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showTimePickerDialog(Button obsTime) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(ObservationActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                obsTime.setText(hour + ":" + minute);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}