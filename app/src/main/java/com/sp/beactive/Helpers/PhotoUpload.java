package com.sp.beactive.Helpers;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PhotoUpload {

    public String filepath;

    public PhotoUpload(){
            filepath="";
    }

    public PhotoUpload(String filepath){

        this.filepath=filepath;
    }
}
