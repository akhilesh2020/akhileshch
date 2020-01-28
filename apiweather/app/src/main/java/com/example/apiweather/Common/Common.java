package com.example.apiweather.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String API_KEY="9c81bacda9eb2dc9b8c6661d93128ba6";
    public static Location current_Location=null;

    public static String convertUnixToDate(long dt) {
        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm dd EEE mm yyyy");
        String formatted=sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long sunrise) {

        Date date=new Date(sunrise*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        String formatted=sdf.format(date);
        return formatted;
    }
}
