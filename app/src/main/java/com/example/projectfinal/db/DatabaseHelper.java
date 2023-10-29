package com.example.projectfinal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projectfinal.db.entity.Hike;
import com.example.projectfinal.db.entity.Observation;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hike_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Perform foreign key constraints when support is enabled
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Hike.CREATE_TABLE);
        sqLiteDatabase.execSQL(Observation.CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Hike.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Observation.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }


    // Getting Observation from DataBase
    public Observation getObservation(long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Observation.TABLE_NAME,
                new String[]{
                        Observation.COL_ID,
                        Observation.COL_NAME,
                        Observation.COL_TIME,
                        Observation.COL_COMMENT,
                        Observation.COL_HIKE_CONSTRAINT},
                Observation.COL_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Observation observation = new Observation(
                cursor.getString(cursor.getColumnIndexOrThrow(Observation.COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Observation.COL_TIME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Observation.COL_COMMENT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(Observation.COL_HIKE_CONSTRAINT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Observation.COL_ID))
        );

        cursor.close();
        return observation;
    }

    // insert Observation
    public long insertObservation(String name, String time, String comment, long hikeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Observation.COL_NAME, name);
        values.put(Observation.COL_TIME, time);
        values.put(Observation.COL_COMMENT, comment);
        values.put(Observation.COL_HIKE_CONSTRAINT, hikeId);

        long id = db.insert(Observation.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    // update Observation
    public int updateObservation(Observation observation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Observation.COL_NAME, observation.getName());
        values.put(Observation.COL_TIME, observation.getTime());
        values.put(Observation.COL_COMMENT, observation.getComment());
        values.put(Observation.COL_HIKE_CONSTRAINT, observation.getHikeId());

        return db.update(Observation.TABLE_NAME, values, Observation.COL_ID + "=?", new String[]{String.valueOf(observation.getId())});
    }

    // delete Observation
    public void deleteObservation(Observation observation) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Observation.TABLE_NAME, Observation.COL_ID + "=?", new String[]{String.valueOf(observation.getId())});
        db.close();
    }

    // delete all Observations

    public void deleteAllObservations() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + Observation.TABLE_NAME);
        db.close();
    }

    // get all Observations by hikeId
    public ArrayList<Observation> getAllObservationsByHikeId(long hikeId) {
        ArrayList<Observation> observations = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Observation.TABLE_NAME + " WHERE " + Observation.COL_HIKE_CONSTRAINT + " = " + hikeId + " ORDER BY " + Observation.COL_TIME + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Observation observation = new Observation();
                observation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Observation.COL_ID)));
                observation.setName(cursor.getString(cursor.getColumnIndexOrThrow(Observation.COL_NAME)));
                observation.setTime(cursor.getString(cursor.getColumnIndexOrThrow(Observation.COL_TIME)));
                observation.setComment(cursor.getString(cursor.getColumnIndexOrThrow(Observation.COL_COMMENT)));
                observation.setHikeId(cursor.getLong(cursor.getColumnIndexOrThrow(Observation.COL_HIKE_CONSTRAINT)));
                observations.add(observation);
            } while (cursor.moveToNext());
        }

        db.close();
        return observations;
    }


    // Getting Hike from DataBase
    public Hike getHike(long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Hike.TABLE_NAME,
                new String[]{
                        Hike.COL_ID,
                        Hike.COL_NAME,
                        Hike.COL_LOCATION,
                        Hike.COL_DATE,
                        Hike.COL_PARKING,
                        Hike.COL_LENGTH,
                        Hike.COL_LEVEL,
                        Hike.COL_DESCRIPTION,
                        Hike.COL_IMAGE},
                Hike.COL_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null,
                null);


        if (cursor != null)
            cursor.moveToFirst();

        Hike contact = new Hike(
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_LOCATION)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_DATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COL_PARKING)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_LENGTH)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_LEVEL)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_DESCRIPTION)),
                cursor.getBlob(cursor.getColumnIndexOrThrow(Hike.COL_IMAGE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(Hike.COL_ID))

        );

        cursor.close();
        return contact;

    }

    // Getting all Hikes
    public ArrayList<Hike> getAllHikes() {
        ArrayList<Hike> hikes = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + Hike.TABLE_NAME + " ORDER BY " +
                Hike.COL_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Hike hike = new Hike();
                hike.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COL_ID)));
                hike.setName(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_NAME)));
                hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_LOCATION)));
                hike.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_DATE)));
                hike.setParkingAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COL_PARKING)));
                hike.setLength(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_LENGTH)));
                hike.setLevel(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_LEVEL)));
                hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COL_DESCRIPTION)));
                hike.setImage(cursor.getBlob(cursor.getColumnIndexOrThrow(Hike.COL_IMAGE)));
                hikes.add(hike);


            } while (cursor.moveToNext());
        }

        db.close();

        return hikes;
    }

    // Insert Data into Database
    public long insertHike(String name, String location, String date, int parkingAvailable, String length, String level, String description, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Hike.COL_NAME, name);
        values.put(Hike.COL_LOCATION, location);
        values.put(Hike.COL_DATE, date);
        values.put(Hike.COL_PARKING, parkingAvailable);
        values.put(Hike.COL_LENGTH, length);
        values.put(Hike.COL_LEVEL, level);
        values.put(Hike.COL_DESCRIPTION, description);
        values.put(Hike.COL_IMAGE, image);



        long id = db.insert(Hike.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public int updateHike(Hike contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Hike.COL_NAME, contact.getName());
        values.put(Hike.COL_LOCATION, contact.getLocation());
        values.put(Hike.COL_DATE, contact.getDate());
        values.put(Hike.COL_PARKING, contact.isParkingAvailable());
        values.put(Hike.COL_LENGTH, contact.getLength());
        values.put(Hike.COL_LEVEL, contact.getLevel());
        values.put(Hike.COL_DESCRIPTION, contact.getDescription());
        values.put(Hike.COL_IMAGE, contact.getImage());


        int rowsUpdated = db.update(Hike.TABLE_NAME, values, Hike.COL_ID + " = ? ",
                new String[]{String.valueOf(contact.getId())});

        db.close(); // Close the database

        return rowsUpdated;

    }

    public void deleteHike(Hike contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Hike.TABLE_NAME, Hike.COL_ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
        db.close();
    }

    public void deleteAllHikes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Hike.TABLE_NAME, null, null);
        db.close();
    }


}
