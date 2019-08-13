package com.sp.beactive.Homepage;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import com.sp.beactive.Helpers.PhotoUpload;
import com.sp.beactive.R;
import com.sp.beactive.Helpers.UserDetails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;


public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    private static final String IMAGE_DIRECTORY = "/beactive";
    private int CAMERA=1, GALLERY = 2;
    public EditText mName2;
    Button mSave;
    Button mDiscard;
    private EditText mAge;
    private ImageView mProfile_Thumbnail;
    Button setProfile;
    SharedPreferences sharedPreferences;
    private ValueEventListener mDetailsListener;
    private ValueEventListener mPhotoListener;
    public static final String mypreferences = "mypref";
    public static final String file = "fileKey";
    private DatabaseReference profile_ref;
    private DatabaseReference photo_ref;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main);

        mAuth = FirebaseAuth.getInstance();
        String uid= Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        sharedPreferences=getSharedPreferences(mypreferences,Context.MODE_PRIVATE);
        mSave = findViewById(R.id.Save);
        mDiscard = findViewById(R.id.Discard);
        mName2 = findViewById(R.id.Name2);
        mAge = findViewById(R.id.Age);
        mSave.setOnClickListener(onSave);
        mDiscard.setOnClickListener(onDiscard);
        mProfile_Thumbnail= findViewById(R.id.Profile_Thumbnail);
        setProfile = findViewById(R.id.setProfile_Thumbnail);
        profile_ref = FirebaseDatabase.getInstance().getReference("users/"+uid+"/profile");
        photo_ref = FirebaseDatabase.getInstance().getReference("users/"+uid+"/photo");

        //GET NAME/AGE FROM FIREBASE
        mDetailsListener= profile_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                assert userDetails!=null;
                mName2 = findViewById(R.id.Name2);
                mName2.setText(userDetails.username);
                mAge.setText(userDetails.age);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(Profile.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        profile_ref.addValueEventListener(mDetailsListener);

        //GET FILEPATH FOR PHOTO FROM FIREBASE
        mPhotoListener= photo_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PhotoUpload photoUpload = dataSnapshot.getValue(PhotoUpload.class);
                assert photoUpload!=null;
                File imgFile = new File(photoUpload.filepath);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
                    mProfile_Thumbnail.setImageBitmap(myBitmap);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(Profile.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        photo_ref.addValueEventListener(mPhotoListener);
        setProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePicture();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if(data != null){
                Uri contentURI = data.getData();
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    saveToTemp(bitmap);
                    Toast.makeText(Profile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    mProfile_Thumbnail.setImageBitmap(bitmap);
                } catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(Profile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA){
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            mProfile_Thumbnail.setImageBitmap(imageBitmap);
            saveToTemp(imageBitmap);
            saveImage(imageBitmap);
            Toast.makeText(Profile.this, "Image Saved!", Toast.LENGTH_SHORT).show();

        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private String saveToTemp(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            String m = mypath.toString();
            sharedPreferences=getApplicationContext().getSharedPreferences(mypreferences,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(file, m);
            editor.apply();


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                assert fos != null;
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String imgFile = (sharedPreferences.getString(file, ""));
            final Intent intent = new Intent(Profile.this, Home.class);
            String profile_name;
            profile_name=mName2.getText().toString();
            String profile_age;
            profile_age=mAge.getText().toString();
            updatePhoto(imgFile);
            updateDetails(profile_name, profile_age);
            startActivity(intent);
            finish();
        }
    };

    public View.OnClickListener onDiscard = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Profile.this);
            alertDialog.setTitle("DISCARDING CHANGES");
            alertDialog.setMessage("You are discarding all changes, are you sure you want to leave?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(),Home.class);
                    startActivity(intent);
                    finish();
                }
            });
            alertDialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

    };

    private void updateDetails(String name,String age){
            UserDetails userDetails=new UserDetails(name,age);
            profile_ref.setValue(userDetails);
    }

    private void updatePhoto(String path){
        PhotoUpload photoUpload = new PhotoUpload(path);
        photo_ref.setValue(photoUpload);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        profile_ref.removeEventListener(mDetailsListener);
        photo_ref.removeEventListener(mPhotoListener);
    }
}
