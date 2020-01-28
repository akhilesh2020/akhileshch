package com.example.apipratice1;

public class item {
    private String mimgurl;
    private  String mTitle;
    private String mdes;

    public item(String mimgurl, String mTitle, String mdes) {
        this.mimgurl = mimgurl;
        this.mTitle = mTitle;
        this.mdes = mdes;
    }

    public String getMimgurl() {
        return mimgurl;
    }


    public String getmTitle() {
        return mTitle;
    }

    public String getMdes() {
        return mdes;
    }

}
