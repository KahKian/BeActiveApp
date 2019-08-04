package com.sp.beactive.Helpers;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserDetails {

    public String username;
    public String age;

    public UserDetails(){
        username="";
        age="";
    }

    public UserDetails(String username, String age){
        this.username=username;
        this.age=age;
    }
}
