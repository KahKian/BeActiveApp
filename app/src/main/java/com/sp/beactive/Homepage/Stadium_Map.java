package com.sp.beactive.Homepage;


import androidx.fragment.app.FragmentActivity;


import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sp.beactive.R;
import com.sp.beactive.Services.GPSTracker;

public class Stadium_Map extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Stadium_Map";
    private double sLat;
    private double sLon;
    private String sName;
    private LatLng ME;
    private LatLng STADIUM;

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stadium_map);
        sLat= getIntent().getDoubleExtra("lat",0);
        sLon= getIntent().getDoubleExtra("lng",0);
        sName=getIntent().getExtras().getString("name","");

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


    }





    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        gpsTracker = new GPSTracker(Stadium_Map.this);
        ME = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        Marker me = mMap.addMarker(new MarkerOptions().position(ME).title("ME")
                .snippet("My location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me))
                .flat(true));
        STADIUM = new LatLng(sLat, sLon);
        Marker stadium = mMap.addMarker(new MarkerOptions().position(STADIUM).title(sName)
                .snippet("Stadium")
                .flat(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ME, 15));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }


}