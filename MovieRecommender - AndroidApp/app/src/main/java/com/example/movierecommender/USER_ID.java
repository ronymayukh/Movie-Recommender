package com.example.movierecommender;

public class USER_ID {
    public static String localIP = "http://192.168.0.10:3000";
    public static String userID = "1";

    public static void setLocalIP(String localIP) {
        USER_ID.localIP = "http://"+localIP+":3000";
    }
}
