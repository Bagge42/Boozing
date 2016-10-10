package com.example.mikkel.boozing;

/**
 * Created by Mikkel on 06/10/2016.
 */

public class Friend {
    private String name;
    private String wifi;

    public Friend(String name, String wifi) {
        this.name = name;
        this.wifi = wifi;
    }

    public String getName() {
        return name;
    }

    public String getWifi(){
        return wifi;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }
}

