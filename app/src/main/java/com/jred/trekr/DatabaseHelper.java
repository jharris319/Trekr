package com.jred.trekr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{


    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "TrekrDB.db";
    protected static final String TABLE_TRAILS = "Trails";
    protected static final String TABLE_POILIST = "POIList";
    protected static final String TABLE_PATHVALUES = "PathValues";
    public static final String COLUMN_ID = "_ID";
    public static final String TRAIL_ID_FK = "_ID";
    public static final String POI_ID = "_POI_ID";
    public static final String COLUMN_TRAILNAME = "TrailName";
    public static final String COLUMN_LOCATIONNAME = "LocationName";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";
    public static final String COLUMN_POINAME = "POIName";
    public static final String COLUMN_POILATITUDE = "POILatitude";
    public static final String COLUMN_POILONGITUDE = "POILongitude";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TRAILS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_TRAILS        + "("
                + COLUMN_ID           + " INTEGER PRIMARY KEY, "
                + COLUMN_TRAILNAME    + " TEXT,"
                + COLUMN_LOCATIONNAME + " TEXT);";

        String CREATE_POILIST_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_POILIST       + "("
                + TRAIL_ID_FK         + " INTEGER, "
                + POI_ID              + " INTEGER PRIMARY KEY, "
                + COLUMN_POINAME      + " STRING, "
                + COLUMN_POILATITUDE  + " REAL, "
                + COLUMN_POILONGITUDE + " REAL, "
                + " FOREIGN KEY ("+TRAIL_ID_FK+") REFERENCES "+TABLE_TRAILS+" ("+COLUMN_ID+"));";

        String CREATE_PATHVALUES_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_PATHVALUES + "("
                + TRAIL_ID_FK      + " INTEGER, "
                + COLUMN_LATITUDE  + " REAL, "
                + COLUMN_LONGITUDE + " REAL, "
                + " FOREIGN KEY ("+TRAIL_ID_FK+") REFERENCES "+TABLE_TRAILS+" ("+COLUMN_ID+"));";

        db.execSQL(CREATE_TRAILS_TABLE);
        db.execSQL(CREATE_POILIST_TABLE);
        db.execSQL(CREATE_PATHVALUES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POILIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATHVALUES);
        onCreate(db);
    }



}
