package com.sp.beactive;

import com.firebase.ui.auth.data.model.User;
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
