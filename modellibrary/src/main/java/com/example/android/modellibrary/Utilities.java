package com.example.android.modellibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utilities {
    public static final String LOG_TAG = Utilities.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Log.w(LOG_TAG, "isNetworkAvailable " + networkInfo);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
