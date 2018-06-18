package com.example.android.background.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.background.LiveCashTrackServices;
import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.User;
import com.example.android.modellibrary.model.Vehicle;

import static com.example.android.background.sync.CashTrackSyncUtils.SYNC_TRANSACTIONS;
import static com.example.android.background.sync.CashTrackSyncUtils.SYNC_USER;
import static com.example.android.background.sync.CashTrackSyncUtils.SYNC_VEHICLES;
import static com.example.android.modellibrary.model.Transaction.TRANSACTION_KEY;
import static com.example.android.modellibrary.model.User.USER_KEY;
import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;

public class CashTrackSyncIntentService extends IntentService {
    private static final String LOG_TAG = CashTrackSyncIntentService.class.getSimpleName();
//    public final static String ACTION_SYNC = "ACTION_SYNC";

    public CashTrackSyncIntentService() {
        super("CashTrackSyncIntentService");
        Log.w(LOG_TAG, "CashTrackSyncIntentService ");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.w(LOG_TAG, "onHandleIntent: ");
        assert intent != null;
        String action = intent.getAction();

        assert action != null;
        switch (action) {
            case SYNC_USER:
                User user = intent.getParcelableExtra(USER_KEY);
                LiveCashTrackServices.postUserMessage(getBaseContext(), intent, user);

                Log.w(LOG_TAG, "SYNC_USER:");
                break;
            case SYNC_VEHICLES:
                Vehicle vehicle = intent.getParcelableExtra(VEHICLE_KEY);
                LiveCashTrackServices.postVehicleMessage(getBaseContext(),intent, vehicle);
                Log.w(LOG_TAG, "SYNC_VEHICLES:");
                break;
            case SYNC_TRANSACTIONS:
                Transaction transaction = intent.getParcelableExtra(TRANSACTION_KEY);
                LiveCashTrackServices.postTransactionMessage(getBaseContext(), transaction);
                Log.w(LOG_TAG, "SYNC_TRANSACTIONS:");
                break;
        }
    }
}
