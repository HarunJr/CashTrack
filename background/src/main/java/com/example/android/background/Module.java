package com.example.android.background;

import android.util.Log;

import com.example.android.background.infrastructure.CashTrackApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HARUN on 12/18/2016.
 */

public class Module {
    public static final String LOG_TAG = Module.class.getSimpleName();

    public static void Register(CashTrackApplication application){
        new LiveCashTrackServices(application, createCashTrackService());
    }

    private static CashTrackWebServices createCashTrackService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Log.w(LOG_TAG, "OkHttpClient ");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Log.w(LOG_TAG, "Gson ");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CashTrackApplication.ONLINE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Log.w(LOG_TAG, "CashTrackWebServices " + retrofit.create(CashTrackWebServices.class));

        return retrofit.create(CashTrackWebServices.class);
    }
}
