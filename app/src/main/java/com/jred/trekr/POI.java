package com.jred.trekr;

import com.google.android.gms.maps.model.LatLng;

public class POI {

    public long _POIID;
    public long _trailID;
    public String _POIName;
    public String _description;
    public LatLng _location;

    public POI()
    {
    }

    public POI(String POIName)
    {
        this._POIName = POIName;
    }

    public POI(String POIName, LatLng location)
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

    public void setDescription(String description) {
        this._description = description;
    }

    public String getDescription() {
        return this._description;
    }

    public void setLocation(LatLng location)
    {
        this._location = location;
    }

    public LatLng getLocation()
    {
        return this._location;
    }
}
