package com.example.mikkel.boozing;

/**
 * Created by emil on 9/29/16.
 */

public class Member {

    private String name;
    private double lat;
    private double lng;

    public Member(String name, double lat, double lng){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName(){
        return name;
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }
}
