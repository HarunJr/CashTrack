package com.harun.android.loginpin;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by HARUN on 4/19/2018.
 */

public class Utilities {

    public static String getCountryZipCode(Context context){
        String plusSign = "+";
        String countryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        assert manager != null;
        String countryID= manager.getSimCountryIso().toUpperCase();
        String[] codes=context.getResources().getStringArray(R.array.CountryCodes);
        for (String aRl : codes) {
            String[] g = aRl.split(",");
            if (g[1].trim().equals(countryID.trim())) {
                countryZipCode = g[0];
                break;
            }
        }
        return plusSign+countryZipCode;
    }

    public static void showSoftKeyboard(View view, FragmentActivity activity) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
}
