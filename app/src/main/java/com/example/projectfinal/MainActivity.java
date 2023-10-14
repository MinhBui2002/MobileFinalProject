package com.example.projectfinal;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.projectfinal.adapter.HikeAdapter;
import com.example.projectfinal.db.DatabaseHelper;
import com.example.projectfinal.db.entity.Hike;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // NO ROOM Database Project
    // Using SQLITE


    // Variables
    private HikeAdapter hikeAdapter;
    private ArrayList<Hike> hikeArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Favorite Hikes");


        // RecyclerVIew
        recyclerView = findViewById(R.id.recycler_view_hikes);
        db = new DatabaseHelper(this);

        // Hikes List
        hikeArrayList.addAll(db.getAllHikes());

        hikeAdapter = new HikeAdapter(this, hikeArrayList, MainActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hikeAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndEditHikes(false, null, -1);
            }
        });
    }

    public void addAndEditHikes(final boolean isUpdated, final Hike hike, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.insert_hike, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView hike_title = view.findViewById(R.id.hike_title);
        EditText hikeName = view.findViewById(R.id.edtName);
        EditText hikeLocation = view.findViewById(R.id.edtLocation);
        Button hikeDatePicker = view.findViewById(R.id.btnDatePicker);
        RadioGroup hikeParking = view.findViewById(R.id.rgrpParking);
        TextView hkeDate = view.findViewById(R.id.txtDate);
        EditText hikeLength = view.findViewById(R.id.edtLength);
        EditText hikeLevel = view.findViewById(R.id.edtLevel);
        EditText hikeDescription = view.findViewById(R.id.edtDescription);
        hikeDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDatePickerDialog(hikeDatePicker, hkeDate   );
            }
        });

        hike_title.setText(!isUpdated ? "Add New Hike" : "Edit Hike");

        if (isUpdated && hike != null) {
            hikeName.setText(hike.getName());
            hikeLocation.setText(hike.getLocation());
            hkeDate.setText(hike.getDate());
            hikeLength.setText(String.valueOf(hike.getLength()));
            hikeLevel.setText(hike.getLevel());
            hikeDescription.setText(hike.getDescription());

            int parkingAvailable = hike.isParkingAvailable();
            RadioButton radioButton;
            if (parkingAvailable != 1) {
                radioButton = view.findViewById(R.id.rbtnNo);
            } else {
                radioButton = view.findViewById(R.id.rbtnYes);
            }
            radioButton.setChecked(true);
        }

        alertDialogBuilderUserInput.setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(hikeName.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Please Enter a Name", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.dismiss();
                        }

                        if (hikeName.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's name invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLocation.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's location invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hkeDate.getText().toString().isEmpty() || hkeDate.getText().toString().equals("Select Date")) {
                            Toast.makeText(MainActivity.this, "Hike's date invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLength.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's length invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeParking.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(MainActivity.this, "Hike's parking available is invalid", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLevel.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Hike's level invalid", Toast.LENGTH_SHORT).show();
                            return;

                        }

                        if (isUpdated && hike != null) {
                            updateHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hkeDate.getText().toString(), hikeParking.getCheckedRadioButtonId(), hikeLength.getText().toString(), hikeLevel.getText().toString(), hikeDescription.getText().toString(), position);
                        } else {
                            createHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hkeDate.getText().toString(), hikeParking.getCheckedRadioButtonId(), hikeLength.getText().toString(), hikeLevel.getText().toString(), hikeDescription.getText().toString());
                        }
                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isUpdated){
                            DeleteHike(hike, position);
                        }else {
                            dialogInterface.cancel();
                        }
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isUpdated){
                            dialogInterface.cancel();
                        }else {
                            dialogInterface.cancel();
                        }

                    }
                });
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.setCanceledOnTouchOutside(true);

    }

    private void DeleteHike(Hike hike, int position) {

        hikeArrayList.remove(position);
        db.deleteHike(hike);
        hikeAdapter.notifyDataSetChanged();


    }

    private void popupDatePickerDialog(Button hikeDatePicker,TextView hkeDate) {
        final Calendar c = Calendar.getInstance();


        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                       hkeDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);


                    }
                },

                year, month, day);

        datePickerDialog.show();
    }


    private void createHike(String name, String location, String date, int parking, String length, String level, String description) {

        if(description.isEmpty()){
            description = "No description available";
        }
        long id = db.insertHike(name, location, date, parking, length, level, description);
        Hike hike = db.getHike(id);
        if (hike != null) {
            hikeArrayList.add(0, hike);
            hikeAdapter.notifyDataSetChanged();
        }
    }

    private void updateHike(String name, String location, String date, int parking, String length, String level, String description, int position) {
        if(description.isEmpty()){
            description = "No description available";
        }
        Hike hike = hikeArrayList.get(position);
        hike.setName(name);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParkingAvailable(parking);
        hike.setLength(length);
        hike.setLevel(level);
        hike.setDescription(description);

        db.updateHike(hike);
        hikeArrayList.set(position, hike);
        hikeAdapter.notifyDataSetChanged();

    }


    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Menu bar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}