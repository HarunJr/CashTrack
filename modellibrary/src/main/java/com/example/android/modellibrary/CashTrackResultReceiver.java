package com.example.android.modellibrary;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.Vehicle;

import static com.example.android.modellibrary.model.Transaction.TRANSACTION_KEY;
import static com.example.android.modellibrary.model.User.TOKEN_KEY;
import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;

public class CashTrackResultReceiver extends ResultReceiver {
    public static final String LOG_TAG = CashTrackResultReceiver.class.getSimpleName();
    public static final String TOKEN_RECEIVER = "token-received";
    public static final String VEHICLE_RECEIVER = "vehicle-received";
    private OnTokenReceived resultsReceived;
    private OnVehicleReceived vehicleReceived;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public CashTrackResultReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        String token = resultData.getString(TOKEN_KEY);
        Vehicle vehicle = resultData.getParcelable(VEHICLE_KEY);
        Transaction transaction = resultData.getParcelable(TRANSACTION_KEY);

        if (token != null) {
            resultsReceived.onTokenReceivedListener(token);
        } else if (vehicle != null) {
            vehicleReceived.onVehicleReceivedListener(vehicle);
        }
        Log.w(LOG_TAG, "onReceiveResult: " + resultCode + "..." + token);
//        Log.w(VEHICLE_RECEIVER, "onReceiveResult: " + resultCode +"..."+vehicle.getRegistration());
    }

    public void setTokenReceiver(OnTokenReceived result) {
        resultsReceived = result;
    }

    public void setVehicleReceiver(OnVehicleReceived result) {
        vehicleReceived = result;
    }

    public interface OnTokenReceived {
        void onTokenReceivedListener(String token);
    }

    public interface OnVehicleReceived {
        void onVehicleReceivedListener(Vehicle vehicle);
    }

    public interface OnTransactionReceived {
        void onTransactionReceivedListener(Transaction transaction);
    }
}