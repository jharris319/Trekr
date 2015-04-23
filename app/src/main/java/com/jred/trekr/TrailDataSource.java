package com.jred.trekr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This Class contains all functions that manipulate the Database
 */
public class TrailDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    //private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TRAILNAME};

    public TrailDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /* Modify Trail Functions */

    public void addTrail(Trail trail)
    {
        ContentValues valuesForTrailTable = new ContentValues();
        String trailName = trail.getTrailName();
        String locationName = trail.getLocationName();
        ArrayList<LatLng> pathValues = trail.getPathValues();
        ArrayList<POI> POIList = trail.getPOIList();

        valuesForTrailTable.put(DatabaseHelper.COLUMN_TRAILNAME, trailName);
        valuesForTrailTable.put(DatabaseHelper.COLUMN_LOCATIONNAME, locationName);
        long trailID = database.insert(DatabaseHelper.TABLE_TRAILS, null, valuesForTrailTable);

        addPathValues(trailID, pathValues);
        addPOIList(trailID, POIList);
    }

    public Trail findTrail(String trailName, String locationName)
    {
        String trailQuery = "Select * FROM " + DatabaseHelper.TABLE_TRAILS + " WHERE " +
                DatabaseHelper.COLUMN_TRAILNAME + " = \"" + trailName + "\" AND " +
                DatabaseHelper.COLUMN_LOCATIONNAME + " = \"" + locationName + "\";";

        Cursor cursor = database.rawQuery(trailQuery, null);

        Trail trail = new Trail();

        if (cursor.moveToFirst())
        {
            trail.setID(Integer.parseInt(cursor.getString(0)));
            trail.setTrailName(cursor.getString(1));
            trail.setLocationName(cursor.getString(2));
            trail.setPathValues(findPathValues(trail.getID()));
            trail.setPOIList(findPOIList(trail.getID()));
            cursor.close();
        }
        else
        {
            trail = null;
        }

        return trail;
    }

    public Trail findTrail(long trailID)
    {
        String trailQuery = "Select * FROM " + DatabaseHelper.TABLE_TRAILS + " WHERE " +
                DatabaseHelper.COLUMN_ID + " = \"" + trailID + "\";";

        Cursor cursor = database.rawQuery(trailQuery, null);

        Trail trail = new Trail();

        if (cursor.moveToFirst())
        {
            trail.setID(Integer.parseInt(cursor.getString(0)));
            trail.setTrailName(cursor.getString(1));
            trail.setLocationName(cursor.getString(2));
            trail.setPathValues(findPathValues(trail.getID()));
            trail.setPOIList(findPOIList(trail.getID()));
            cursor.close();
        }
        else
        {
            trail = null;
        }

        return trail;
    }

    public boolean deleteTrail(String trailName)
    {
        boolean result = false;
        String query = "Select * FROM " + DatabaseHelper.TABLE_TRAILS + " WHERE " +
                DatabaseHelper.COLUMN_TRAILNAME + " = \"" + trailName + "\";";

        Cursor cursor = database.rawQuery(query, null);

        Trail trail = new Trail();

        if (cursor.moveToFirst())
        {
            trail.setID(Integer.parseInt(cursor.getString(0)));
            database.delete(DatabaseHelper.TABLE_TRAILS, DatabaseHelper.COLUMN_ID + " = ?",
                    new String[] { String.valueOf(trail.getID()) });
            result = true;
        }
        return result;
    }

    /* Modify PathValues Functions */

    public void addPathValues(long trailID, ArrayList<LatLng> pathValues)
    {
        if (pathValues == null) { return; }

        ContentValues valuesForPathValuesTable = new ContentValues();

        for (LatLng pathValue : pathValues) {
            valuesForPathValuesTable.put(DatabaseHelper.TRAIL_ID_FK, trailID);
            valuesForPathValuesTable.put(DatabaseHelper.COLUMN_LATITUDE, pathValue.latitude);
            valuesForPathValuesTable.put(DatabaseHelper.COLUMN_LONGITUDE, pathValue.longitude);
            database.insert(DatabaseHelper.TABLE_PATHVALUES, null, valuesForPathValuesTable);
            valuesForPathValuesTable.clear();
        }
    }

    public ArrayList<LatLng> findPathValues(long trailID)
    {
        String query = "Select * FROM " + DatabaseHelper.TABLE_PATHVALUES + " WHERE " +
                DatabaseHelper.COLUMN_ID + " = \"" + trailID + "\";";

        Cursor cursor = database.rawQuery(query, null);

        ArrayList<LatLng> pathValues = new ArrayList<LatLng>();

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                pathValues.add(new LatLng(Double.parseDouble(cursor.getString(1)),
                        Double.parseDouble(cursor.getString(2))));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return pathValues;
    }

    public boolean deletePathValues(long trailID)
    {
        boolean result = false;
        String query = "Select * FROM " + DatabaseHelper.TABLE_PATHVALUES + " WHERE " +
                DatabaseHelper.COLUMN_ID + " = \"" + trailID + "\";";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            database.delete(DatabaseHelper.TABLE_PATHVALUES, DatabaseHelper.COLUMN_ID + " = ?",
                    new String[] { String.valueOf(trailID) });
            result = true;
        }
        return result;
    }

    /* POIList Functions */

    public void addPOI(POI poi)
    {
        ContentValues valuesForPOITable = new ContentValues();

        valuesForPOITable.put(DatabaseHelper.TRAIL_ID_FK, poi.getTrailID());
        valuesForPOITable.put(DatabaseHelper.COLUMN_POILATITUDE, poi.getLocation().latitude);
        valuesForPOITable.put(DatabaseHelper.COLUMN_POILONGITUDE, poi.getLocation().longitude);

        database.insert(DatabaseHelper.TABLE_POILIST, null, valuesForPOITable);
    }

    public void addPOI(POI poi, String trailName)
    {
        ContentValues valuesForPOITable = new ContentValues();

        String query = "Select * FROM " + DatabaseHelper.TABLE_TRAILS + " WHERE " +
                DatabaseHelper.COLUMN_TRAILNAME + " = \"" + trailName + "\";";

        Cursor cursor = database.rawQuery(query, null);

        long id = 0;

        if (cursor.moveToFirst())
        {
            id = Long.parseLong(cursor.getString(0));
        }

        LatLng location = poi.getLocation();

        valuesForPOITable.put(DatabaseHelper.TRAIL_ID_FK, id);
        valuesForPOITable.put(DatabaseHelper.COLUMN_POINAME, poi.getPOIName());
        valuesForPOITable.put(DatabaseHelper.COLUMN_DESCRIPTION, poi.getDescription());
        valuesForPOITable.put(DatabaseHelper.COLUMN_POILATITUDE, location.latitude);
        valuesForPOITable.put(DatabaseHelper.COLUMN_POILONGITUDE, location.longitude);

        database.insert(DatabaseHelper.TABLE_POILIST, null, valuesForPOITable);
    }

    public void addPOIList(long trailID, ArrayList<POI> POIList)
    {
        if (POIList == null) { return; }

        ContentValues valuesForPOITable = new ContentValues();

        for (POI aPOIList : POIList) {
            valuesForPOITable.put(DatabaseHelper.TRAIL_ID_FK, trailID);
            valuesForPOITable.put(DatabaseHelper.COLUMN_POILATITUDE, aPOIList.getLocation().latitude);
            valuesForPOITable.put(DatabaseHelper.COLUMN_POILONGITUDE, aPOIList.getLocation().longitude);

            database.insert(DatabaseHelper.TABLE_POILIST, null, valuesForPOITable);

            valuesForPOITable.clear();
        }
    }

    public POI findPOI(long trailID, long POIID)
    {
        String query = "Select * FROM " + DatabaseHelper.TABLE_POILIST + " WHERE "
                + DatabaseHelper.COLUMN_ID + " = \"" + trailID + "\" AND "
                + DatabaseHelper.POI_ID + " = \"" + POIID + "\";";

        Cursor cursor = database.rawQuery(query, null);

        POI POIFound = new POI();
        LatLng POILocation;

        if (cursor.moveToFirst())
        {
            POIFound.setPOIID(Long.parseLong(cursor.getString(0)));
            POIFound.setTrailID(Long.parseLong(cursor.getString(1)));
            POIFound.setPOIName(cursor.getString(2));
            POILocation = new LatLng(Double.parseDouble(cursor.getString(3)),
                    Double.parseDouble(cursor.getString(4)));
            POIFound.setLocation(POILocation);
        }
        else
        {
            POIFound = null;
        }

        return POIFound;
    }

    public POI findPOI(long POIID)
    {
        String query = "Select * FROM " + DatabaseHelper.TABLE_POILIST + " WHERE "
                + DatabaseHelper.POI_ID + " = \"" + POIID + "\";";

        Cursor cursor = database.rawQuery(query, null);

        POI POIFound = new POI();
        LatLng POILocation;

        if (cursor.moveToFirst())
        {
            POIFound.setTrailID(Long.parseLong(cursor.getString(0)));
            POIFound.setPOIID(Long.parseLong(cursor.getString(1)));
            POIFound.setPOIName(cursor.getString(2));
            POIFound.setDescription(cursor.getString(3));
            POILocation = new LatLng(Double.parseDouble(cursor.getString(4)),
                    Double.parseDouble(cursor.getString(5)));
            POIFound.setLocation(POILocation);
        }
        else
        {
            POIFound = null;
        }

        return POIFound;
    }

    public ArrayList<POI> findPOIList(long trailID)
    {
        String query = "Select * FROM " + DatabaseHelper.TABLE_POILIST + " WHERE "
                + DatabaseHelper.COLUMN_ID + " = \"" + trailID + "\";";

        Cursor cursor = database.rawQuery(query, null);

        ArrayList<POI> POIList = new ArrayList<>();
        LatLng POILocation;

        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                POI newPOI = new POI();
                newPOI.setPOIID(Long.parseLong(cursor.getString(0)));
                newPOI.setTrailID(Long.parseLong(cursor.getString(1)));
                newPOI.setPOIName(cursor.getString(2));
                newPOI.setDescription(cursor.getString(3));
                POILocation = new LatLng(Double.parseDouble(cursor.getString(4)),
                        Double.parseDouble(cursor.getString(5)));
                newPOI.setLocation(POILocation);
                POIList.add(newPOI);
                cursor.moveToNext();
            }
        }
        else
        {
            POIList = null;
        }

        return POIList;
    }

    public boolean deletePOI(long trailID, long POIID)
    {
        boolean result = false;
        String query = "Select * FROM " + DatabaseHelper.TABLE_POILIST + " WHERE "
                + DatabaseHelper.COLUMN_ID + " = \"" + trailID + "\" AND "
                + DatabaseHelper.POI_ID + " = \"" + POIID + "\";";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            database.delete(DatabaseHelper.TABLE_POILIST, DatabaseHelper.POI_ID + " = ?",
                    new String[] { String.valueOf(POIID) });
            result = true;
        }
        return result;
    }

    public ArrayList<String> getAllTrails() {
        ArrayList<String> values = new ArrayList<String>();
        String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TRAILNAME, DatabaseHelper.COLUMN_LOCATIONNAME};

        Cursor cursor = database.query(DatabaseHelper.TABLE_TRAILS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Trail trail = cursorToTrail(cursor);
            values.add(trail.getTrailName());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return values;
    }

    private Trail cursorToTrail(Cursor cursor) {
        Trail trail = new Trail();
        trail.setID(cursor.getLong(0));
        trail.setTrailName(cursor.getString(1));
        return trail;
    }

    public ArrayList<String> getAllPOIs() {
        ArrayList<String> values = new ArrayList<>();
        String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.POI_ID,
                DatabaseHelper.COLUMN_POINAME, DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_POILATITUDE, DatabaseHelper.COLUMN_POILONGITUDE};

        Cursor cursor = database.query(DatabaseHelper.TABLE_POILIST,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            POI poi = cursorToPOI(cursor);
            values.add(poi.getPOIName());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return values;
    }

    private POI cursorToPOI(Cursor cursor) {
        POI poi = new POI();
        poi.setTrailID(cursor.getLong(0));
        poi.setPOIID(cursor.getLong(1));
        poi.setPOIName(cursor.getString(2));
        return poi;
    }
}
