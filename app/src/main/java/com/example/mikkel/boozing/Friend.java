package com.example.mikkel.boozing;

/**
 * Created by Mikkel on 06/10/2016.
 */

public class Friend {
    private String name;
    private int phone;

    public Friend(String name, int phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public int getPhone(){
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setphone(int phone) {
        this.phone = phone;
    }
}

