package com.sp.beactive;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class Home extends AppCompatActivity {



    private int CAMERA=1, GALLERY = 2;
    private ImageView mThumbnail;
    public TextView mName;
    public static final String TAG = "Home";
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    public static final String mypreferences = "mypref";
    public static final String file = "fileKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_main);


        Button buttonProfile;
        Button buttonLearning;
        Button buttonHealth;
        Button buttonGoals;
        Button buttonCommunity;
        Button buttonMap;
        Button mSetThumbnail;
        ImageButton btn;

        ref=FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        sharedPreferences = getSharedPreferences(mypreferences, Context.MODE_PRIVATE);
        mThumbnail = findViewById(R.id.Thumbnail);
        File imgFile = new File(this.sharedPreferences.getString(file,""));
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
            mThumbnail.setImageBitmap(myBitmap);
        }

        mName = findViewById(R.id.Name);
        buttonProfile = findViewById(R.id.buttonProfile);
        buttonLearning = findViewById(R.id.buttonLearning);
        buttonHealth = findViewById(R.id.buttonHealth);
        buttonGoals = findViewById(R.id.buttonGoals);
        buttonCommunity = findViewById(R.id.buttonCommunity);
        buttonMap = findViewById(R.id.buttonMap);
        btn = findViewById(R.id.popup_menu);
        mSetThumbnail = findViewById(R.id.setThumbnail);

        buttonProfile.setOnClickListener(onProfile);
        buttonLearning.setOnClickListener(onLearning);
        buttonHealth.setOnClickListener(onHealth);
        buttonGoals.setOnClickListener(onGoals);
        buttonCommunity.setOnClickListener(onCommunity);
        buttonMap.setOnClickListener(onMap);

        requestMultiplePermissions();

        mSetThumbnail.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
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
                                Intent intent = new Intent(getApplicationContext(),SignIn.class);
                                startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        ref = FirebaseDatabase.getInstance().getReference("users/"+ mAuth.getCurrentUser().getUid());
        ValueEventListener mDetailsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
            public void onCancelled(DatabaseError databaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(Home.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addValueEventListener(mDetailsListener);
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
                    saveToInternalStorage(bitmap);
                    Toast.makeText(Home.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    mThumbnail.setImageBitmap(bitmap);
                } catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(Home.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA){
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            mThumbnail.setImageBitmap(imageBitmap);
            saveToInternalStorage(imageBitmap);
            Toast.makeText(Home.this, "Image Saved!", Toast.LENGTH_SHORT).show();

        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");



        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            String m = mypath.toString();
            sharedPreferences=getApplicationContext().getSharedPreferences(mypreferences,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(file, m);
            editor.apply();


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(getApplicationContext(), Learning.class);
            startActivity(intent);
            finish();

        }
    };
    private View.OnClickListener onHealth = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Health.class);
            startActivity(intent);
            finish();

        }
    };
    private View.OnClickListener onGoals = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), Goals.class);
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
            Intent intent = new Intent(getApplicationContext(), Map.class);
            startActivity(intent);
            finish();

        }
    };
}