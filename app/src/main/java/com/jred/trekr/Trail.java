package com.jred.trekr;

public class Trail {

    private int _id;
    private String _trailName;
    private Location[] _pathValues;
    private POI[] _POIList;

    public Trail()
    {
    }

    public Trail(int id, String trailName, Location[] pathValues)
    {
        this._id = id;
        this._trailName = trailName;
        this._pathValues = pathValues;
    }

    public Trail(String trailName, Location[] pathValues)
    {
        this._trailName = trailName;
        this._pathValues = pathValues;
    }

    public void setID(int id)
    {
        this._id = id;
    }

    public int getID()
    {
        return this._id;
    }

    public void setTrailName(String trailName)
    {
        this._trailName = trailName;
    }

    public String getTrailName()
    {
        return this._trailName;
    }

    public void setPathValues(Location[] pathValues)
    {
        this._pathValues = pathValues;
    }

    public Location[] getPathValues()
    {
        return this._pathValues;
    }

    public void setPOIList(POI[] POIList)
    {
        this._POIList = POIList;
    }

    public POI[] getPOIList()
    {
        return this._POIList;
    }

}
