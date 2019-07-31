package com.sp.beactive;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;



public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    public EditText mName2;
    private Button mSave;
    private EditText mAge;
    private ImageView mProfile_Thumbnail;
    SharedPreferences sharedPreferences;
    public static final String mypreferences = "mypref";
    public static final String file = "fileKey";
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);


        ref=FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences=getSharedPreferences(mypreferences,Context.MODE_PRIVATE);
        mSave = findViewById(R.id.Save);
        mName2 = findViewById(R.id.Name2);
        mAge = findViewById(R.id.Age);
        mSave.setOnClickListener(onSave);
        mProfile_Thumbnail= findViewById(R.id.Profile_Thumbnail);

        ref = FirebaseDatabase.getInstance().getReference("users/"+ mAuth.getCurrentUser().getUid()+"/profile");
        ValueEventListener mDetailsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                assert userDetails!=null;
                mName2 = findViewById(R.id.Name2);
                mName2.setText(userDetails.username);
                mAge.setText(userDetails.age);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(Profile.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addValueEventListener(mDetailsListener);

        File imgFile = new File(this.sharedPreferences.getString(file,""));
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
            mProfile_Thumbnail.setImageBitmap(myBitmap);
        }
    }

    public View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           final Intent intent = new Intent(Profile.this,Home.class);
            String profile_name;
            profile_name=mName2.getText().toString();
            String profile_age;
            profile_age=mAge.getText().toString();

            updateDetails(profile_name, profile_age);
            startActivity(intent);
            finish();
        }
    };

    private void updateDetails(String name,String age){
            UserDetails userDetails=new UserDetails(name,age);
            ref.setValue(userDetails);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
        finish();
    }
}
