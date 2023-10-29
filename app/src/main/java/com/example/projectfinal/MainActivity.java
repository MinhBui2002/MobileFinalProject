package com.example.projectfinal;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.projectfinal.adapter.HikeAdapter;
import com.example.projectfinal.db.DatabaseHelper;
import com.example.projectfinal.db.entity.Hike;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

    ImageView hikeImage;


    final int REQUEST_CODE_GALLERY = 999;
    final int REQUEST_CODE_LOCATION = 888;

    private EditText hikeLocation; // Add this line to your existing variables
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Hikes");


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
                createOrUpdateHike(false, null, -1);
            }
        });

        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteAllHikes();
            }
        });


    }


    // Create and update hike based on the boolean isUpdated and the position of the hike
    // ideas from Mr. Manh 's "Contact App" project
    public void createOrUpdateHike(final boolean isUpdated, final Hike hike, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.insert_hike, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView hike_title = view.findViewById(R.id.hike_title);
        EditText hikeName = view.findViewById(R.id.edtName);
        hikeLocation = view.findViewById(R.id.edtLocation);
        Button hikeLocationButton = view.findViewById(R.id.btnLocation);
        Button hikeDatePicker = view.findViewById(R.id.btnDatePicker);
        RadioGroup hikeParking = view.findViewById(R.id.rgrpParking);
        TextView hkeDate = view.findViewById(R.id.txtDate);
        EditText hikeLength = view.findViewById(R.id.edtLength);
        EditText hikeLevel = view.findViewById(R.id.edtLevel);
        EditText hikeDescription = view.findViewById(R.id.edtDescription);
        hikeImage = view.findViewById(R.id.imgHike);

        hikeDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDatePickerDialog(hikeDatePicker, hkeDate);
            }
        });

        hikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read external storage permission to select image from gallery
                //runtime permission for devices android 6.0 and above
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);





        hikeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
                } else {
                    // Fetch the location once
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        try {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);

                            if (!addresses.isEmpty()) {
                                String address = addresses.get(0).getAddressLine(0);
                                // Set the location in the EditText
                                hikeLocation.setText(address);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
            hikeImage.setImageBitmap(BitmapFactory.decodeByteArray(hike.getImage(), 0, hike.getImage().length));
            int parkingAvailable = hike.isParkingAvailable();
            if (parkingAvailable != 1) {
                RadioButton radioButton = view.findViewById(parkingAvailable);
                radioButton.setChecked(true);
            }

        }


        // alert dialog to create or update hike based on the input
        alertDialogBuilderUserInput.setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        // Data validation
                        // ideas from Mr. Manh 's "Contact App" project
                        if (hikeName.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "You have entered an invalid hike name. Please enter the hike name again", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLocation.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "You have entered an invalid hike location. Please enter the hike location again", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hkeDate.getText().toString().isEmpty() || hkeDate.getText().toString().equals("Select Date")) {
                            Toast.makeText(MainActivity.this, "You have entered an invalid hike date. Please enter the hike date again", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLength.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "You have entered an invalid hike date. Please enter the hike date again", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeParking.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(MainActivity.this, "You haven't choose the parking available.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (hikeLevel.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "You have entered an invalid hike level. Please enter the hike level again", Toast.LENGTH_SHORT).show();
                            return;

                        }

                        if (isUpdated && hike != null) {
                            updateHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hkeDate.getText().toString(), hikeParking.getCheckedRadioButtonId(), hikeLength.getText().toString(), hikeLevel.getText().toString(), hikeDescription.getText().toString(), imageViewToByte(hikeImage), position);
                        } else {
                            createHike(hikeName.getText().toString(), hikeLocation.getText().toString(), hkeDate.getText().toString(), hikeParking.getCheckedRadioButtonId(), hikeLength.getText().toString(), hikeLevel.getText().toString(), hikeDescription.getText().toString(), imageViewToByte(hikeImage));
                        }
                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isUpdated) {
                            // confirm delete
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Delete hike");
                            builder.setMessage("Are you sure you want to delete this hike?");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DeleteHike(hike, position);
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
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        dialogInterface.cancel();


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

    private void DeleteAllHikes() {
        db.deleteAllHikes();
        hikeArrayList.clear();
        hikeAdapter.notifyDataSetChanged();
    }


    // Ideas is referenced from https://www.digitalocean.com/community/tutorials/android-date-time-picker-dialog
    private void popupDatePickerDialog(Button hikeDatePicker, TextView hkeDate) {
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


    // create new hike
    // ideas from Mr. Manh 's "Contact App" project
    private void createHike(String name, String location, String date, int parking, String length, String level, String description, byte[] image) {


        long id = db.insertHike(name, location, date, parking, length, level, description, image);
        Hike hike = db.getHike(id);
        if (hike != null) {
            hikeArrayList.add(0, hike);
            hikeAdapter.notifyDataSetChanged();
        }
    }


    // ideas from Mr. Manh 's "Contact App" project
    private void updateHike(String name, String location, String date, int parking, String length, String level, String description, byte[] image, int position) {

        Hike hike = hikeArrayList.get(position);
        hike.setName(name);
        hike.setLocation(location);
        hike.setDate(date);
        hike.setParkingAvailable(parking);
        hike.setLength(length);
        hike.setLevel(level);
        hike.setDescription(description);
        hike.setImage(image);

        db.updateHike(hike);
        hikeArrayList.set(position, hike);
        hikeAdapter.notifyDataSetChanged();

    }


    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_GALLERY:
                handleGalleryPermissionResult(grantResults);
                break;
            case REQUEST_CODE_LOCATION:
                handleLocationPermissionResult(grantResults);
                break;
            // Add more cases for other permission request codes if needed
        }
    }

    private void handleGalleryPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Gallery permission granted, open gallery intent
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
        } else {
            Toast.makeText(this, "Don't have permission to access file location", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLocationPermissionResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Location permission granted, request location updates
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            Toast.makeText(this, "Don't have permission to access location", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enable image guidlines
                    .setAspectRatio(1, 1)// image will be square
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //set image choosed from gallery to image view
                hikeImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                hikeAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }


//
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