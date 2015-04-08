package com.jred.trekr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteProgram;

public class DatabaseHelper {

    public class MyDBHandler extends SQLiteOpenHelper
    {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "TrekrDB.db";

        private static final String TABLE_TRAILS = "Trails";
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_TRAILNAME = "Trail Name";

        private static final String TABLE_PATHVALUES = "Path Values";
        public static final String COLUMN_TRAILID = "_TrailID";
        public static final String COLUMN_LATITUDE = "Latitude";
        public static final String COLUMN_LONGITUDE = "Longitude";

        private static final String TABLE_POILIST = "POI List";
        // Trail ID will be used again as primary/foreign key
        public static final String COLUMN_POINAME = "POI Name";
        public static final String COLUMN_POILATITUDE = "POI Latitude";
        public static final String COLUMN_POILONGITUDE = "POI Longitude";

        public MyDBHandler(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String CREATE_TRAILS_TABLE = "CREATE TABLE " +
                    TABLE_TRAILS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TRAILNAME + " TEXT,"
                    + ")";

            String CREATE_PATHVALUES_TABLE = "CREATE TABLE " +
                    TABLE_PATHVALUES + "("
                    + COLUMN_TRAILID + " INTEGER PRIMARY KEY,"
                    + COLUMN_LATITUDE + " REAL,"
                    + COLUMN_LONGITUDE + " REAL"
                    + "FOREIGN KEY (" + COLUMN_TRAILID
                    + ") REFERENCES " + COLUMN_ID
                    + "(" + TABLE_TRAILS + ") "
                    + ")";

            String CREATE_POILIST_TABLE = "CREATE TABLE " +
                    TABLE_POILIST + "("
                    + COLUMN_TRAILID + " INTEGER PRIMARY KEY,"
                    + COLUMN_POINAME + " TEXT,"
                    + COLUMN_POILATITUDE + " REAL,"
                    + COLUMN_POILONGITUDE + " REAL,"
                    + "FOREIGN KEY (" + COLUMN_TRAILID
                    + ") REFERENCES " + COLUMN_ID
                    + "(" + TABLE_TRAILS + ") "
                    + ")";

            db.execSQL(CREATE_TRAILS_TABLE);
            db.execSQL(CREATE_PATHVALUES_TABLE);
            db.execSQL(CREATE_POILIST_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAILS);
            onCreate(db);
        }

        public void addTrail(Trail trail)
        {
            ContentValues valuesForTrailTable = new ContentValues();
            ContentValues valuesForPathValuesTable = new ContentValues();
            ContentValues valuesForPOITable = new ContentValues();
            Location[] pathValues = trail.getPathValues();
            POI[] POIList = trail.getPOIList();

            valuesForTrailTable.put(COLUMN_TRAILNAME, trail.getTrailName());


            for(int i = 0; i < pathValues.length; i++)
            {
                // Need to figure out how to deal with foreign keys
                valuesForPathValuesTable.put(COLUMN_LATITUDE, pathValues[i].getLatitude());
                valuesForPathValuesTable.put(COLUMN_LONGITUDE, pathValues[i].getLongitude());
            }

            for(int i = 0; i < POIList.length; i++)
            {
                Location location = POIList[i].getLocation();
                // Need to figure out how to deal with foreign keys
                valuesForPOITable.put(COLUMN_POINAME, POIList[i].getPOIName());
                valuesForPOITable.put(COLUMN_POILATITUDE, location.getLatitude());
                valuesForPOITable.put(COLUMN_POILONGITUDE, location.getLongitude());
            }

            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(TABLE_TRAILS, null, valuesForTrailTable);
            db.insert(TABLE_PATHVALUES, null, valuesForPathValuesTable);
            db.insert(TABLE_POILIST, null, valuesForPOITable);
            db.close();
        }

        public Trail findTrail(String trailName)
        {
            String query = "Select * FROM " + TABLE_TRAILS + " WHERE " +
                    COLUMN_TRAILNAME + " = \"" + trailName + "\";";

            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery(query, null);

            Trail trail = new Trail();

            if (cursor.moveToFirst())
            {
                cursor.moveToFirst();
                trail.setID(Integer.parseInt(cursor.getString(0)));
                trail.setTrailName(cursor.getString(1));
                //trail.setPathValues(cursor.getString(2));
                cursor.close();
            }
            else
            {
                trail = null;
            }
            db.close();
            return trail;
        }

        public boolean deleteTrail(String trailName)
        {
            boolean result = false;
            String query = "Select * FROM " + TABLE_TRAILS + " WHERE " +
                    COLUMN_TRAILNAME + " = \"" + trailName + "\";";

            SQLiteDatabase db = this.getWritableDatabase();

            Cursor cursor = db.rawQuery(query, null);

            Trail trail = new Trail();

            if (cursor.moveToFirst())
            {
                trail.setID(Integer.parseInt(cursor.getString(0)));
                db.delete(TABLE_TRAILS, COLUMN_ID + " = ?",
                        new String[] { String.valueOf(trail.getID()) });
                cursor.close();
                result = true;
            }
            db.close();
            return result;
        }
    }

}
