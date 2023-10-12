package com.example.projectfinal;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    // NO ROOM Database Project
    // Using SQLITE



    // Variables
    private HikeAdapter hikeAdapter;
    private ArrayList<Hike> hikeArrayList  = new ArrayList<>();
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

        hikeAdapter = new HikeAdapter(this, hikeArrayList,MainActivity.this);
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

    public void addAndEditHikes(final boolean isUpdated,final Hike hike,final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_hike, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        TextView newHikeTitle = view.findViewById(R.id.new_hike_title);
        final EditText inputHikeName = view.findViewById(R.id.name);
        final EditText inputHikeEmail = view.findViewById(R.id.email);
        ImageView imageView = view.findViewById(R.id.image_select);

        newHikeTitle.setText(!isUpdated ? "Add New Hike" : "Edit Hike");

        if (isUpdated && hike != null){
            inputHikeName.setText(hike.getName());
            inputHikeEmail.setText(hike.getEmail());
            imageView.setImageBitmap(getImage(hike.getImage()));
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdated ? "Update" : "Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isUpdated){
                            DeleteHike(hike, position);
                        }else {
                            dialogInterface.cancel();
                        }
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(inputHikeName.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter Hike Name!", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    alertDialog.dismiss();
                }

                if (isUpdated && hike != null){
                    // update hike by it's id with new information (name , email, image)
                    hike.setName(inputHikeName.getText().toString());
                    hike.setEmail(inputHikeEmail.getText().toString());

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
                    hike.setImage(getBytes(bitmap));


                }else {

                }
            }
        });

    }

    private void DeleteHike(Hike hike, int position) {

        hikeArrayList.remove(position);
        db.deleteHike(hike);
        hikeAdapter.notifyDataSetChanged();


    }


//    private void UpdateHike(String name, String email, int position){
//        Hike hike = hikeArrayList.get(position);
//
//        hike.setName(name);
//        hike.setEmail(email);
//
//        db.updateHike(hike);
//
//        hikeArrayList.set(position, hike);
//        hikeAdapter.notifyDataSetChanged();
//
//
//    }

    // update hike method with image



    //    private void CreateHike(String name, String email){
//
//        long id = db.insertHike(name, email);
//        Hike hike = db.getHike(id);
//
//        if (hike != null){
//            hikeArrayList.add(0, hike);
//            hikeAdapter.notifyDataSetChanged();
//        }
//
//    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}