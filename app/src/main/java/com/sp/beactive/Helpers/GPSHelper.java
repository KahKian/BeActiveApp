package com.sp.beactive.Helpers;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class GPSHelper {

    public Double lat;
    public Double lon;

    public GPSHelper(){
        lat= 0.0d;
        lon= 0.0d;
    }

    public GPSHelper(Double lat, Double lon){
        this.lat=lat;
        this.lon=lon;
    }
}
