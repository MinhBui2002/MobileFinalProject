package com.example.projectfinal;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.projectfinal.adapter.ObserveAdapter;
import com.example.projectfinal.db.DatabaseHelper;
import com.example.projectfinal.db.entity.Hike;
import com.example.projectfinal.db.entity.Observation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class ObserveActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private FloatingActionButton addNewObs;
    private ObserveAdapter observationAdapter;

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


        TextView hikeName = findViewById(R.id.txtName);
        TextView hikeLocation = findViewById(R.id.txtLocation);
        TextView hikeDate = findViewById(R.id.txtDate);
        TextView hikeParking = findViewById(R.id.txtParking);
        TextView hikeLength = findViewById(R.id.txtLength);
        TextView hikeLevel = findViewById(R.id.txtLevel);
        TextView hikeDescription = findViewById(R.id.txtDescription);


        // Retrieve data passed from the source activity
        Intent intent = getIntent();
        if (intent != null) {

            RecyclerView obsList = findViewById(R.id.recycler_view_observations);
            long hikeId = intent.getLongExtra("hikeId", -1);
            dbHelper = new DatabaseHelper(this);
            Hike hike = dbHelper.getHike(hikeId);

            observationArrayList = dbHelper.getAllObservationsByHikeId(hikeId);

            observationAdapter = new ObserveAdapter(this, observationArrayList, this);
            obsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            obsList.setItemAnimator(new DefaultItemAnimator());
            obsList.setAdapter(observationAdapter);

            addNewObs = findViewById(R.id.addNewObservation);
            addNewObs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createOrUpdateObservation(false, null, -1, hikeId);
                }
            });

            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View popupView = layoutInflater.inflate(R.layout.insert_hike, null);
            RadioButton hikeParkingAvailableSelected = popupView.findViewById(hike.isParkingAvailable());
            String selectedRadioButtonText = hikeParkingAvailableSelected.getText().toString();

            hikeName.setText(hike.getName());
            hikeLocation.setText(hike.getLocation());
            hikeDate.setText(hike.getDate());
            hikeParking.setText(selectedRadioButtonText);
            hikeLength.setText(hike.getLength());
            hikeLevel.setText(hike.getLevel());
            hikeDescription.setText(hike.getDescription());


        }
    }

    public void openObsDetail(Observation observation) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View popupView = layoutInflater.inflate(R.layout.detail_obs, null);
        //AlertDialog.builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ObserveActivity.this);
        alertDialogBuilder.setView(popupView);

        final TextView obsName = popupView.findViewById(R.id.detailObsName);
        final TextView obsTime = popupView.findViewById(R.id.detailObsTime);
        final TextView obsComment = popupView.findViewById(R.id.detailObsComment);

        obsName.setText("Name: " + observation.getName());
        obsTime.setText("Time: " + observation.getTime());
        obsComment.setText("\"" + observation.getComment() + "\"");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void createOrUpdateObservation(final boolean isUpdated, final Observation obs, final int position, final long hikeId) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View popupView = layoutInflater.inflate(R.layout.detail_obs, null);
        //AlertDialog.builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ObserveActivity.this);
        alertDialogBuilder.setView(popupView);

        TextView popupTitle = popupView.findViewById(R.id.popupTitle);
        final TextView obsName = popupView.findViewById(R.id.detailObsName);
        final Button obsTime = popupView.findViewById(R.id.detailObsTime);
        final TextView obsComment = popupView.findViewById(R.id.detailObsComment);

        if (!isUpdated && obsTime.getText().toString().isEmpty()) {
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
                popupTimePickerDialog(obsTime);
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
                            Toast.makeText(ObserveActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.dismiss();
                        }

                        if (obsName.getText().toString().isEmpty()) {
                            Toast.makeText(ObserveActivity.this, "Hike's name invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (obsTime.getText().toString().isEmpty() || obsTime.getText().toString().equals("Pick a time")) {
                            Toast.makeText(ObserveActivity.this, "Hike's location invalid", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (isUpdated && obs != null) {
                            updateObservation(obsName.getText().toString(), obsTime.getText().toString(), obsComment.getText().toString(), position);
                        } else {
                            createObservation(obsName.getText().toString(), obsTime.getText().toString(), obsComment.getText().toString(), hikeId);
                        }
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isUpdated) {
                            // confirm delete
                            AlertDialog.Builder builder = new AlertDialog.Builder(ObserveActivity.this);
                            builder.setTitle("Delete observation");
                            builder.setMessage("Are you sure you want to delete this observation?");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteObservation(obs, position);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).show();

                        } else {
                            dialogInterface.cancel();
                        }
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void createObservation(String name, String time, String comment, long hikeId) {
        long id = dbHelper.insertObservation(name, time, comment, hikeId);
        Observation obs = dbHelper.getObservation(id);

        if (obs != null) {
            observationAdapter.createNewObservation(obs);
        }
    }

    private void updateObservation(String name, String time, String comment, int position) {
        Observation obs = observationArrayList.get(position);
        obs.setName(name);
        obs.setTime(time);
        obs.setComment(comment);

        dbHelper.updateObservation(obs);
        observationAdapter.updateObservation(obs, position);
    }

    private void deleteObservation(Observation obs, int position) {
        observationArrayList.remove(position);
        dbHelper.deleteObservation(obs);
        observationAdapter.notifyDataSetChanged();
    }

    private void popupTimePickerDialog(Button obsTime) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(ObserveActivity.this, new TimePickerDialog.OnTimeSetListener() {
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