package com.example.android.cashtracklibrary;

import android.support.v4.app.FragmentActivity;

public class BaseBackPressedListener implements CashTrackFragment.OnBackPressedListener {
    private final FragmentActivity activity;
    private final NumPad numPad;

    public BaseBackPressedListener(FragmentActivity activity, NumPad numPad) {
        this.activity = activity;
        this.numPad = numPad;
    }

    @Override
    public void onBackPressed() {
        if( numPad.isCustomKeyboardVisible() ) numPad.hideCustomKeyboard(); else activity.finish();

    }
}
