package com.sp.beactive;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sp.beactive.Helpers.GPSHelper;
import com.sp.beactive.Helpers.PhotoUpload;
import com.sp.beactive.Helpers.UserDetails;
import com.sp.beactive.Homepage.Home;

import java.util.Arrays;
import java.util.List;


public class SignIn extends AppCompatActivity {
    private static final String TAG = "SignIn";
    private static final int RC_SIGN_IN = 123;
    private DatabaseReference ref;
    Button mSignIn;
    Button mSignOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_main);


        mSignIn= findViewById(R.id.sign_in_button);
        mSignOut= findViewById(R.id.sign_out_button);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSignInIntent();
            }
        });
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final Intent intent = new Intent(this, Home.class);
                assert user.getUid() !=null;
                ref = FirebaseDatabase.getInstance().getReference("users/"+user.getUid());
                final ValueEventListener mDetailsListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            Toast.makeText(SignIn.this,"Welcome Back!",Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            GPSHelper gpsHelper = new GPSHelper();
                            UserDetails userDetails = new UserDetails();
                            PhotoUpload photoUpload = new PhotoUpload();
                            ref.child("photo").setValue(photoUpload);
                            ref.child("profile").setValue(userDetails);
                            ref.child("location").setValue(gpsHelper);
                            Toast.makeText(SignIn.this,"Hello New User!",Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        Toast.makeText(SignIn.this, "Oops",
                                Toast.LENGTH_SHORT).show();
                    }
                };
                ref.addListenerForSingleValueEvent(mDetailsListener);
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

            }
        }
    }
    // [END auth_fui_result]

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        Toast.makeText(SignIn.this,"User has been signed out",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignIn.this, CloseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // [END auth_fui_signout]
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