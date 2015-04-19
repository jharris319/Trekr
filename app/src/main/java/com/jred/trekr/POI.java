package com.jred.trekr;

public class POI {

    public long _POIID;
    public long _trailID;
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
        this._POIName = POIName;
        this._location = location;
    }

    public void setPOIID(long ID)
    {
        this._POIID = ID;
    }

    public long getPOIID()
    {
        return this._POIID;
    }

    public void setTrailID(long ID)
    {
        this._trailID = ID;
    }

    public long getTrailID()
    {
        return this._trailID;
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
