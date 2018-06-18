package com.example.android.cashtracklibrary;

import android.content.Context;

public class TouchableEditText extends android.support.v7.widget.AppCompatEditText{

    public TouchableEditText(Context context) {
        super(context);
    }
    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}