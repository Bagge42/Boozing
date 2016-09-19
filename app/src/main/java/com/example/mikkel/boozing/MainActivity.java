package com.example.mikkel.boozing;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    public final static String BONUS = "hvad er det her?";

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        name = editText.getText().toString();
        //String send_message = "" + lastLoc.getLatitude() + "," + lastLoc.getLongitude();
        intent.putExtra(BONUS, name);
        startActivity(intent);
    }
}
