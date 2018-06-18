package com.harun.android.cashtrack;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Vehicle;

/**
 * Created by HARUN on 11/22/2017.
 */

public class CashTrackService extends IntentService {
    private static final String LOG_TAG = CashTrackService.class.getSimpleName();
    private static final String ACTION_UPDATE_CASH_TRACK_WIDGETS = "com.harun.android.cashtrack.action.update_baking_widgets";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     **/
    public CashTrackService() {
        super("CashTrackService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            final String action = intent.getAction();
            if (ACTION_UPDATE_CASH_TRACK_WIDGETS.equals(action)){
                LocalStore localStore = new LocalStore(getApplicationContext());
                Vehicle vehicle = localStore.getPreferenceVehicle();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CashTrackWidgetProvider.class));
                //Trigger data update to handle the GridView widgets and force a data refresh
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
                //Now update all widgets
                CashTrackWidgetProvider.updateTransactionsWidgets(this, appWidgetManager, vehicle,appWidgetIds);
            }
        }
    }

    public static void startActionUpdateTransactionWidget(Context context) {
        Log.w(LOG_TAG, "startActionUpdateTransactionWidget: " );

        Intent intent = new Intent(context, CashTrackService.class);
        intent.setAction(ACTION_UPDATE_CASH_TRACK_WIDGETS);
        context.startService(intent);
    }
}
