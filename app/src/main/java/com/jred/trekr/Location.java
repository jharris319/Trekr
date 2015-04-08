package com.jred.trekr;

public class Location {

    public int _latitude;
    public int _longitude;

    public Location()
    {
    }

    public Location(int latitude, int longitude)
    {
        this._latitude = latitude;
        this._longitude = longitude;
    }

    public void setLatitude(int latitude)
    {
        this._latitude = latitude;
    }

    public int getLatitude()
    {
        return this._latitude;
    }

    public void setLongitude(int longitude)
    {
        this._longitude = longitude;
    }

    public int getLongitude()
    {
        return this._longitude;
    }
}
