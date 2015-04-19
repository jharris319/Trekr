package com.jred.trekr;

public class Location {

    private double _latitude;
    private double _longitude;

    public Location()
    {
    }

    public Location(double latitude, double longitude)
    {
        this._latitude = latitude;
        this._longitude = longitude;
    }

    public void setLatitude(double latitude)
    {
        this._latitude = latitude;
    }

    public double getLatitude()
    {
        return this._latitude;
    }

    public void setLongitude(double longitude)
    {
        this._longitude = longitude;
    }

    public double getLongitude()
    {
        return this._longitude;
    }
}
