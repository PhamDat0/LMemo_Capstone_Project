package com.example.lmemo_capstone_project.controller.internet_checking_controller;

import android.content.Context;

import java.io.IOException;

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

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
