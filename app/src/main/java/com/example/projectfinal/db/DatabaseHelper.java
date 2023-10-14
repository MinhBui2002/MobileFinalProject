package com.example.projectfinal.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.projectfinal.db.entity.Hike;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hike_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Hike.CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Hike.TABLE_NAME);

        onCreate(sqLiteDatabase);

    }

    // Getting Hike from DataBase
    public Hike getHike(long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Hike.TABLE_NAME,
                new String[]{
                        Hike.COLUMN_ID,
                        Hike.COLUMN_NAME,
                        Hike.COLUMN_LOCATION,
                        Hike.COLUMN_DATE,
                        Hike.COLUMN_PARKING,
                        Hike.COLUMN_LENGTH,
                        Hike.COLUMN_LEVEL,
                        Hike.COLUMN_DESCRIPTION,},
                Hike.COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null,
                null);

        int parkingAvailableValue = cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_PARKING));
        boolean parkingAvailability = (parkingAvailableValue == 1);

        if (cursor != null)
            cursor.moveToFirst();

        Hike contact = new Hike(
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_PARKING)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LENGTH)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LEVEL)),
                cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_ID))
        );

        cursor.close();
        return contact;

    }

    // Getting all Hikes
    public ArrayList<Hike> getAllHikes() {
        ArrayList<Hike> hikes = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + Hike.TABLE_NAME + " ORDER BY " +
                Hike.COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int parkingAvailableValue = cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_PARKING));
                boolean parkingAvailability = (parkingAvailableValue == 1);

                Hike hike = new Hike();
                hike.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_ID)));
                hike.setName(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_NAME)));
                hike.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LOCATION)));
                hike.setDate(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DATE)));
                hike.setParkingAvailable(cursor.getInt(cursor.getColumnIndexOrThrow(Hike.COLUMN_PARKING)));
                hike.setLength(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LENGTH)));
                hike.setLevel(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_LEVEL)));
                hike.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Hike.COLUMN_DESCRIPTION)));
                hikes.add(hike);


            } while (cursor.moveToNext());
        }

        db.close();

        return hikes;
    }

    // Insert Data into Database
    public long insertHike(String name, String location, String date, int parkingAvailable, String length, String level, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Hike.COLUMN_NAME, name);
        values.put(Hike.COLUMN_LOCATION, location);
        values.put(Hike.COLUMN_DATE, date);
        values.put(Hike.COLUMN_PARKING, parkingAvailable);
        values.put(Hike.COLUMN_LENGTH, length);
        values.put(Hike.COLUMN_LEVEL, level);
        values.put(Hike.COLUMN_DESCRIPTION, description);


        long id = db.insert(Hike.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public int updateHike(Hike contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Hike.COLUMN_NAME, contact.getName());
        values.put(Hike.COLUMN_LOCATION, contact.getLocation());
        values.put(Hike.COLUMN_DATE, contact.getDate());
        values.put(Hike.COLUMN_PARKING, contact.isParkingAvailable());
        values.put(Hike.COLUMN_LENGTH, contact.getLength());
        values.put(Hike.COLUMN_LEVEL, contact.getLevel());
        values.put(Hike.COLUMN_DESCRIPTION, contact.getDescription());


        int rowsUpdated = db.update(Hike.TABLE_NAME, values, Hike.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(contact.getId())});

        db.close(); // Close the database

        return rowsUpdated;

    }

    public void deleteHike(Hike contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Hike.TABLE_NAME, Hike.COLUMN_ID + " = ?",
                new String[]{String.valueOf(contact.getId())}
        );
        db.close();
    }


}
