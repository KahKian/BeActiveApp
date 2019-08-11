package com.sp.beactive.Homepage;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sp.beactive.Helpers.GPSHelper;
import com.sp.beactive.Helpers.UserDetails;
import com.sp.beactive.R;
import com.sp.beactive.Services.GPSTracker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private double myLat;
    private double myLon;
    private LatLng ME;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private ValueEventListener mDetailsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_maps);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("users/"+ mAuth.getCurrentUser().getUid()+"/location");
            mDetailsListener = ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GPSHelper gpsHelper = dataSnapshot.getValue(GPSHelper.class);
                    assert gpsHelper!=null;
                    myLat=gpsHelper.lat;
                    myLon=gpsHelper.lon;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    Toast.makeText(MapsActivity.this, "Failed to load post.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        ref.addValueEventListener(mDetailsListener);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void updateCamera(float bearing) {
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(new LatLng(myLat, myLon))
                .bearing(bearing).tilt(65.5f).zoom(18f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        ME = new LatLng(myLon,myLat);
        Marker me = mMap.addMarker(new MarkerOptions().position(ME).title("ME")
        .snippet("My location")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, 4));
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ref.removeEventListener(mDetailsListener);
    }
}
