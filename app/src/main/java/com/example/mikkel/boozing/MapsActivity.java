package com.example.mikkel.boozing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
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
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public final static String keyMessage = "Ved stadig ikke rigtigt hvad den her gør";
    private boolean movedAlready = true; //What for???
    private String friendName;
    private GoogleMap mMap;
    private Location lastLoc; //Users current or last known location.
    public LocationManager locationManager;
    private String name;
    private Marker myLocation;
    private WifiManager mWifiManager;
    private String thisSSID = "";
    private String phone;

    //EV -Firebase
    DatabaseReference mRootRef;
    DatabaseReference mMembersRef;

    ArrayList<String> list = new ArrayList<String>();

    ArrayList<Member> mMembersList;

    public void sendMessage(View view) {
        Intent intent = new Intent(this, Main2Activity.class);
        Bundle extras = new Bundle();
        extras.putString(keyMessage, phone);
        extras.putSerializable("myList", list);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //wifi stuff
        thisSSID = getSSIDInfo();
        mWifiManager  = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();

        //EV -Firebase
        mRootRef = FirebaseDatabase.getInstance().getReference(); //mDatabase.getReference("Child");
        mMembersRef = mRootRef.child("Members");
        mMembersList = new ArrayList<Member>();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        name = extras.getString(MainActivity.BONUS);
        phone = extras.getString(MainActivity.PHONE);
        if(name != null) {
            DatabaseReference mMe = mMembersRef.child(phone);
            Member memberMe = new Member(name, 0, 0, Integer.parseInt(phone),thisSSID);
            mMe.setValue(memberMe);
            mMe.child("Friends");
            mMembersList.add(memberMe);
        }
        else {
            friendName = extras.getString(Main2Activity.EXTRA_MESSAGE);
            phone = extras.getString(Main2Activity.KEY_MESSAGE);
            mMembersRef.child(phone).child("Friends").child(extras.getString(Main2Activity.FRIENDS_PHONE)).setValue(friendName);
        }

//        DatabaseReference newLat = newUser.push();
//        DatabaseReference newLng = newUser.push();
//        newUser.setValue(name);
//        newLat.setValue(0);
//        newLng.setValue(0);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
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

    public String getSSIDInfo(){
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo =wifiManager.getConnectionInfo();

        SupplicantState state = wifiInfo.getSupplicantState();
        String ssid ="<N/A>";
        if(state==SupplicantState.COMPLETED)
        {
            ssid = wifiInfo.getSSID();
            if(ssid.startsWith("\"")&& ssid.endsWith("\""))
                ssid=ssid.substring(1, ssid.length()-1);

        }
        return ssid;
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                ArrayList<String> wifis = new ArrayList<String>();
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                for(ScanResult result : mScanResults){
                     if(!wifis.contains(result.SSID)) {
                         wifis.add(result.SSID);
                     }
                }
                String allWifis="Nearby WiFi's: ";
                for(String m: wifis) {
                    allWifis += m + ".\n ";
                }
                Toast.makeText(getBaseContext(), allWifis, Toast.LENGTH_LONG).show();
            }
        }
    };

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLoc = location; //Last location becomes current locating... for a while...
            LatLng position = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
            if(name != null) {
                mMembersRef.child(phone).child("lat").setValue(lastLoc.getLatitude());
                mMembersRef.child(phone).child("lng").setValue(lastLoc.getLongitude());
            }
            String newWifiMaybe = getSSIDInfo();
            if(!newWifiMaybe.equals(thisSSID)) {
                mMembersRef.child(phone).child("wifi").setValue(newWifiMaybe);
                thisSSID = newWifiMaybe;
            }
            if(movedAlready) {
                myLocation = mMap.addMarker(new MarkerOptions().position(position).title(name));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                movedAlready = false;
            }
            myLocation.setPosition(position);
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

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMembersRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals(phone)) {
                    name = dataSnapshot.child("name").getValue().toString();
                    for (DataSnapshot d : dataSnapshot.child("Friends").getChildren()) {
                        if(!list.contains(d.getKey().toString())) {
                            list.add(d.getKey().toString());
                        }
                    }
                }
                else {
                    String wifi = dataSnapshot.child("wifi").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    double lat = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                    double lng = Double.parseDouble(dataSnapshot.child("lng").getValue().toString());
                    Member m = new Member(name, lat, lng, Integer.parseInt(dataSnapshot.getKey()), wifi);
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
                    if(dataSnapshot.getKey().equals(m.getNumber()) && !phone.equals(m.getNumber())){
                        m.setLat(Double.parseDouble(dataSnapshot.child("lat").getValue().toString()));
                        m.setLng(Double.parseDouble(dataSnapshot.child("lng").getValue().toString()));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(m.getLat(), m.getLng())).title(m.getName()));
                    }
                }
                for(String friend: list) {
                    System.out.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤ Your friend " + friend + " has changed network status");
                    if (dataSnapshot.child("wifi").getValue().toString().equals(thisSSID) && !dataSnapshot.getKey().equals(phone) && dataSnapshot.getKey().toString().equals(friend)) {
                        //print someone is nearby
                        //Den her sysout bliver aldrig printet, ved ikke hvorfor. Den burde blive printet når man tilføjer en ven med et
                        // telefon nummer som ligger i databasen, hvis det er sådan at vennen og en selv er på samme wifi.
                        System.out.println("################################################################################ ...and the networks are a match. WOHOOOO!!!!");
                        System.out.println("################################################################################ ...by the way, the nme of your friend is "
                                + dataSnapshot.child("name").getValue().toString());
                        showAlert(dataSnapshot.child("name").getValue().toString());
                    }
                }

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



    private void showAlert(String name) {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
        myAlert.setMessage("Wanna go Boozing with " + name + "?").setPositiveButton("Sure, why not!",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setTitle("Incomming notification about your opportunities to get really pissed...!").create();
        myAlert.show();
    }
}
