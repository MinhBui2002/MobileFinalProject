package com.example.projectfinal.db.entity;



public class Hike {

    public static final String TABLE_NAME = "hikes";

    public static final String COL_ID = "hike_id";

    public static final String COL_NAME = "hike_name";

    public static  final String COL_LOCATION = "hike_location";

    public static final String COL_DATE = "hike_date";

    public static final String COL_PARKING = "hike_parking";

    public static final String COL_LENGTH = "hike_length";

    public static final String COL_LEVEL = "hike_level";

    public static final String COL_DESCRIPTION = "hike_description";



    private String name;
    private String location;
    private String date;
    private int parkingAvailable;
    private String length;
    private String level;
    private String description;

    private long id;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Hike(String name, String location, String date, int parkingAvailable, String length, String level, String description, long id) {
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

    public int isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(int parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
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
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_NAME + " TEXT,"
                    + COL_LOCATION + " TEXT,"
                    + COL_DATE + " TEXT,"
                    + COL_PARKING + " TEXT,"
                    + COL_LENGTH + " TEXT,"
                    + COL_LEVEL + " TEXT,"
                    + COL_DESCRIPTION + " TEXT"
                    + ")";
}
