package com.example.mikkel.boozing;

import java.util.ArrayList;

/**
 * Created by emil on 9/29/16.
 */

public class Member {

    private String wifi;
    private String name;
    private int number;
    private double lat;
    private double lng;

    public Member(String name, double lat, double lng, int phone, String wifi){
        this.wifi = wifi;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.number = phone;
    }

    public String getWifi(){return wifi;}

    public String getName(){
        return name;
    }

    public int getNumber(){
        return number;
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWifi(String wifi) {this.wifi = wifi;}
}
