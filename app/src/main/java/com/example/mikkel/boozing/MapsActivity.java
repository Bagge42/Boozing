package com.example.mikkel.boozing;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private boolean movedAlready = true; //What for???
    private GoogleMap mMap;
    private Location lastLoc; //Users current or last known location.
    public LocationManager locationManager;
    private String name;
    private Marker myLocation;
    private String thisKey = "";

    //EV -Firebase
    DatabaseReference mRootRef;
    DatabaseReference mMembersRef;

    ArrayList<Member> mMembersList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        name = intent.getStringExtra(MainActivity.BONUS);

        //EV -Firebase
        mRootRef = FirebaseDatabase.getInstance().getReference(); //mDatabase.getReference("Child");
        mMembersList = new ArrayList<Member>();
        mMembersRef = mRootRef.child("Members");

        DatabaseReference my_ref = mMembersRef.push();
        thisKey = my_ref.getKey();
        Member memberMe = new Member(name, 0, 0, thisKey);
        my_ref.setValue(memberMe);
        mMembersList.add(memberMe);
//        DatabaseReference newLat = newUser.push();
//        DatabaseReference newLng = newUser.push();
//        newUser.setValue(name);
//        newLat.setValue(0);
//        newLng.setValue(0);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        lastLoc = new Location("");

        /*Scanner sc = new Scanner(msg);
        double lat = sc.nextDouble(), lng = sc.nextDouble();*/
        //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLoc = location; //Last location becomes current locating... for a while...
            LatLng position = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
            myLocation.setPosition(position);
            mMembersRef.child(thisKey).child("lat").setValue(lastLoc.getLatitude());
            mMembersRef.child(thisKey).child("lng").setValue(lastLoc.getLongitude());
            if(movedAlready) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                movedAlready = false;
            }
//            Toast.makeText(getBaseContext(), ""+location.getLongitude(), Toast.LENGTH_LONG).show();
            //Run a method for pushing users location to server. aprox. 1min. interval
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {


        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(0, 0);
        myLocation = mMap.addMarker(new MarkerOptions().position(sydney).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMembersRef.addChildEventListener(new ChildEventListener() {


//            ArrayList<String> list = mMembersList.get(i);

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(thisKey)) {

                }
                else {
                    String name = dataSnapshot.child("name").getValue().toString();
                    double lat = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                    double lng = Double.parseDouble(dataSnapshot.child("lng").getValue().toString());
                    Member m = new Member(name, lat, lng, dataSnapshot.getKey());
                    mMembersList.add(m);
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                for(Member m: mMembersList) {
                    if(dataSnapshot.getKey().equals(m.getKey()) & !thisKey.equals(m.getKey())){
                        m.setLat(Double.parseDouble(dataSnapshot.child("lat").getValue().toString()));
                        m.setLng(Double.parseDouble(dataSnapshot.child("lng").getValue().toString()));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(m.getLat(), m.getLng())).title(m.getName()));
                    }
                }
//                mMembersList.add(s);

//                memberName = dataSnapshot.getValue(String.class);
//                System.out.println("The member is..." + mMembersList.get(1));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
        });




//        mMembersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String memberName;
//                String content;
//
//                content = dataSnapshot.getValue(String.class);
//                System.out.println("The member is..." + content);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });




    }
}
