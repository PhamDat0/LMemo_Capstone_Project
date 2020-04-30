package com.example.lmemo_capstone_project.controller.internet_checking_controller;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class InternetCheckingController {
    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     * この関数はインタネットとモバイルデータをチェックします
     */
    public static boolean isOnline (Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        if (wifi != null) {
//            if (wifi.isConnected()) {
//                return true;
//            }
//        }
//        if (mobile != null) {
//            if (mobile.isConnected()) {
//                return true;
//            }
//        }

        final String command = "ping -c 1 google.com";
        try {
            boolean result;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Process process = Runtime.getRuntime().exec(command);
                result = process.waitFor(2, TimeUnit.SECONDS);
                if (!result) {
                    process.destroy();
                } else {
                    result = Runtime.getRuntime().exec(command).waitFor() == 0;
                }
            } else {
                result = Runtime.getRuntime().exec(command).waitFor() == 0;
            }
            Log.i("Test", "Connected");
            return result;
        } catch (InterruptedException e) {
            Log.i("Test", e.getMessage() == null ? "Null1" : e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("Test", e.getMessage() == null ? "Null2" : e.getMessage());
            e.printStackTrace();
        }
        return false;

//        ConnectivityManager cm = (ConnectivityManager)context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (activeNetwork != null && activeNetwork.isConnected()) {
//            try {
//                URL url = new URL("https://www.google.com/");
//                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
//                urlc.setRequestProperty("User-Agent", "test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1000); // mTimeout is in seconds
//                urlc.connect();
//                if (urlc.getResponseCode() == 200) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } catch (IOException e) {
//                Log.i("warning", "Error checking internet connection", e);
//                return false;
//            }
//        }
//
//        return false;
    }
}
