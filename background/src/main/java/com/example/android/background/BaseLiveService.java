package com.example.android.background;

import android.util.Log;

import com.example.android.background.infrastructure.CashTrackApplication;

/**
 * Created by HARUN on 12/18/2016.
 */

public class BaseLiveService {
    private static final String LOG_TAG = BaseLiveService.class.getSimpleName();
    private CashTrackApplication application;
    static CashTrackWebServices api;

    public BaseLiveService(CashTrackApplication application, CashTrackWebServices api){
        this.application = application;
        BaseLiveService.api = api;
        Log.w(LOG_TAG, "BaseLiveService " );
    }
}
