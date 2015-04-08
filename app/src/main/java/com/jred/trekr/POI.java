package com.jred.trekr;

public class POI {

    public String _POIName;
    public Location _location;

    public POI()
    {
    }

    public POI(String POIName)
    {
        this._POIName = POIName;
    }

    public POI(String POIName, Location location)
    {
        this._location = location;
    }

    public void setPOIName(String POIName)
    {
        this._POIName = POIName;
    }

    public String getPOIName()
    {
        return this._POIName;
    }

    public void setLocation(Location location)
    {
        this._location = location;
    }

    public Location getLocation()
    {
        return this._location;
    }
}
