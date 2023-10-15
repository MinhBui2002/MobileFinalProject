package com.example.projectfinal.db.entity;

public class Observation {

    public static final String TABLE_NAME = "observations";
    public static final String HIKE_TABLE = "hikes";

    public static final String COL_ID = "observe_id";
    public static final String COL_NAME = "observe_name";
    public static final String COL_TIME = "observe_time";
    public static final String COL_COMMENT = "observe_comment";
    public static final String COL_HIKE_CONSTRAINT = "observe_hike_id";

    private String name;
    private String time;
    private String comment;
    private long hikeId;

    private long id;

    public Observation() {

    }

    public Observation(String name, String time, String comment, long hikeId, long id) {
        this.name = name;
        this.time = time;
        this.comment = comment;
        this.hikeId = hikeId;
        this.id = id;
    }

    //get - set

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getHikeId() {
        return hikeId;
    }

    public void setHikeId(long hikeId) {
        this.hikeId = hikeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_NAME + " TEXT,"
                    + COL_TIME + " TEXT,"
                    + COL_COMMENT + " TEXT,"
                    + COL_HIKE_CONSTRAINT + " INTEGER,"
                    + "FOREIGN KEY (" + COL_HIKE_CONSTRAINT + ") REFERENCES " + HIKE_TABLE + " (hike_id) ON DELETE CASCADE" +
                    ")";
}
