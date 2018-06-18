package com.example.android.background;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.background.infrastructure.CashTrackApplication;

public class BaseActivity extends AppCompatActivity {
    protected CashTrackApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (CashTrackApplication) getApplication();
    }
}
