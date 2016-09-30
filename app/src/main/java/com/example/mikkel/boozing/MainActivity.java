package com.example.mikkel.boozing;

import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;  //hører til getSSIDInfo() metoden
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

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
    /*TODO: (JLG) metoden nedenfor -burde- returnere et string objekt,
      med navnet på det tilsluttede wifi-netværk.
      indsæt det i klassen hvor det skal bruges, husk at importer nødvendige pakker,
      og kald metoden hvor nødvendigt.
    */


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
    /*TODO: (JLG) kom i tanke om at alle uni-SSID herfra og til grækenland hedder Eduroam
       så dette stykke henter istedet den tilknyttede Routers MAC adresse.
       Test gerne inden brug, da den muligvis returnerer en tom streng
       eller "02:00:00:00:00:00" som er en falsk adresse (sikkerheds-ting).
       hvis dette er tilfældet, så smid mig en sms, og så prøver jeg istedet at
       implementere et indviklet workaround jeg har fundet.
    */
    public String getNetworkMAC(){
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo =wifiManager.getConnectionInfo();

        SupplicantState state = wifiInfo.getSupplicantState();
        String MACAdr ="<N/A>";
        if(state==SupplicantState.COMPLETED)
        {
            MACAdr = wifiInfo.getMacAddress();
            if(MACAdr.startsWith("\"")&& MACAdr.endsWith("\""))
                MACAdr=MACAdr.substring(1, MACAdr.length()-1);

        }
        return MACAdr;
    }

}
