package com.example.projectfinal.db.entity;



public class Hike {

    public static final String TABLE_NAME = "hikes";

    public static final String COLUMN_ID = "hike_id";

    public static final String COLUMN_NAME = "hike_name";

    public static  final String COLUMN_LOCATION = "hike_location";

    public static final String COLUMN_DATE = "hike_date";

    public static final String COLUMN_PARKING = "hike_parking";

    public static final String COLUMN_LENGTH = "hike_length";

    public static final String COLUMN_LEVEL = "hike_level";

    public static final String COLUMN_DESCRIPTION = "hike_description";



    private String name;
    private String location;
    private String date;
    private boolean parkingAvailable;
    private float length;
    private String level;
    private String description;

    private int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hike(String name, String location, String date, boolean parkingAvailable, float length, String level, String description, int id) {
        this.name = name;
        this.location = location;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.level = level;
        this.description = description;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Hike() {
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_LOCATION + " TEXT,"
                    + COLUMN_DATE + " TEXT,"
                    + COLUMN_PARKING + " TEXT,"
                    + COLUMN_LENGTH + " TEXT,"
                    + COLUMN_LEVEL + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT"
                    + ")";
}
