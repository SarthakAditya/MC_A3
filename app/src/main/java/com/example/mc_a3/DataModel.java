package com.example.mc_a3;

public class DataModel {
    String X,Y,Z,Lat,Long,APnam,APstrength,Location,Time;

    public DataModel(String x, String y, String z, String lat, String aLong, String APnam, String APstrength, String Location, String Time) {
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.Lat = lat;
        this.Long = aLong;
        this.APnam = APnam;
        this.APstrength = APstrength;
        this.Location = Location;
        this.Time = Time;
    }

    public DataModel()
    {
        this.X = "";
        this.Y = "";
        this.Z = "";
        this.Lat = "";
        this.Long = "";
        this.APnam = "";
        this.APstrength = "";
        this.Location = "";
        this.Time = "";
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getZ() {
        return Z;
    }

    public void setZ(String z) {
        Z = z;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getAPnam() {
        return APnam;
    }

    public void setAPnam(String APnam) {
        this.APnam = APnam;
    }

    public String getAPstrength() {
        return APstrength;
    }

    public void setAPstrength(String APstrength) {
        this.APstrength = APstrength;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
