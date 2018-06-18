package com.example.android.background.infrastructure;

import android.app.Application;

import com.example.android.background.Module;

import java.lang.ref.WeakReference;

/**
 * Created by HARUN on 12/18/2016.
 */

public class CashTrackApplication extends Application {
    public static final String ONLINE_URL = "http://transit.gemilab.com/";
//    public static final String BASE_URL = "http://192.168.245.2/";
//    public static final String BASE_WIFI_URL = "http://192.168.0.17/"; //Ipconfig LAN
    private static WeakReference<CashTrackApplication> applicationWeakReference;


    public CashTrackApplication(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationWeakReference = new WeakReference<>(this);
        Module.Register(this);
    }

    public CashTrackApplication getAppContext(){
        return applicationWeakReference.get();
    }
}
