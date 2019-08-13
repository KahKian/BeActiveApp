package com.sp.beactive.Homepage;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sp.beactive.Helpers.PhotoUpload;
import com.sp.beactive.OneTimeAlertDialog;
import com.sp.beactive.R;
import com.sp.beactive.Services.GPSTracker;
import com.sp.beactive.SignIn;
import com.sp.beactive.Helpers.UserDetails;

import java.io.File;
import java.util.List;
import java.util.Objects;


public class Home extends AppCompatActivity {




    public TextView mName;
    public static final String TAG = "Home";
    private DatabaseReference profile_ref;
    private DatabaseReference photo_ref;

    private FirebaseAuth mAuth;
    private ValueEventListener mDetailsListener;
    private ValueEventListener mPhotoListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_main);


        Button stadiums;
        stadiums = findViewById(R.id.Stadiums);
        stadiums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jsonintent = new Intent(getApplicationContext(), Select_Stadium.class);
                startActivity(jsonintent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        profile_ref = FirebaseDatabase.getInstance().getReference("users/"+ uid+"/profile");
        photo_ref = FirebaseDatabase.getInstance().getReference("users/"+ uid+"/photo");





        mName = findViewById(R.id.Name);
        Button buttonProfile = findViewById(R.id.buttonProfile);
        Button buttonInfo = findViewById(R.id.buttonInfo);
        Button buttonReminders = findViewById(R.id.buttonReminders);
        Button buttonCommunity = findViewById(R.id.buttonCommunity);
        Button buttonMap = findViewById(R.id.buttonMap);
        ImageButton signout_btn = findViewById(R.id.popup_menu);

        buttonProfile.setOnClickListener(onProfile);
        buttonInfo.setOnClickListener(onLearning);
        buttonReminders.setOnClickListener(onReminders);
        buttonCommunity.setOnClickListener(onCommunity);
        buttonMap.setOnClickListener(onMap);

        requestMultiplePermissions();

        signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Home.this, v);
                popup.getMenuInflater().inflate(R.menu.menu1, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getApplicationContext(), "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
                        switch (item.getItemId()){
                            case R.id.sign_out:
                                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                startActivity(intent);
                                return true;

                            case  R.id.reset_acc:
                                String name = "";
                                String age = "";
                                String photopath = "";
                                UserDetails resetDetails = new UserDetails(name,age);
                                PhotoUpload resetPhoto = new PhotoUpload(photopath);
                                profile_ref.setValue(resetDetails);
                                photo_ref.setValue(resetPhoto);
                                Intent reset_intent = new Intent(getApplicationContext(), SignIn.class);
                                startActivity(reset_intent);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
        new OneTimeAlertDialog.Builder(Home.this, "homepage_dialog")
                .setTitle("Welcome!")
                .setMessage("Please head towards the Profile to set up your particulars")
                .show();

    }

    @Override
    protected void onStart(){
        super.onStart();
        mDetailsListener=profile_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                assert userDetails!=null;
                mName = findViewById(R.id.Name);
                if(userDetails.username.equals(""))
                {
                    mName.setText("Hello first time user,\n please go to Profile!");

                }
                else
                {
                    mName.setText("Hello, " +userDetails.username+"!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(Home.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        profile_ref.addValueEventListener(mDetailsListener);

        mPhotoListener= photo_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PhotoUpload photoUpload = dataSnapshot.getValue(PhotoUpload.class);
                assert photoUpload!=null;
                File imgFile = new File(photoUpload.filepath);
                if(imgFile.exists()){
                    ImageView mThumbnail;
                    mThumbnail = findViewById(R.id.homeThumbnail);
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
                    mThumbnail.setImageBitmap(myBitmap);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(Home.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        photo_ref.addValueEventListener(mPhotoListener);
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }


                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(getApplicationContext(), "Some permissions are denied by user!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    private View.OnClickListener onProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
            finish();

        }
    };
    private View.OnClickListener onLearning = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), More_Info.class);
            startActivity(intent);
            finish();

        }
    };

    private View.OnClickListener onReminders = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Reminders.class);
            startActivity(intent);
            finish();

        }
    };
    private View.OnClickListener onCommunity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Community.class);
            startActivity(intent);
            finish();

        }
    };
    private View.OnClickListener onMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Stadium_Map.class);
            startActivity(intent);
            finish();

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        profile_ref.removeEventListener(mDetailsListener);
        photo_ref.removeEventListener(mPhotoListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        minimizeApp();
    }
    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
