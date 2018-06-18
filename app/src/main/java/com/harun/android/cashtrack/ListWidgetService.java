package com.harun.android.cashtrack;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.Vehicle;

import static com.example.android.modellibrary.SystemQuery.COL_AMOUNT;
import static com.example.android.modellibrary.SystemQuery.COL_DESCRIPTION;
import static com.example.android.modellibrary.SystemQuery.COL_TYPE;
import static com.example.android.modellibrary.SystemQuery.COL_UPDATE_TIME;
import static com.example.android.modellibrary.model.Transaction.TRANSACTION_KEY;
import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;

/**
 * Created by HARUN on 11/15/2017.
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String LOG_TAG = ListRemoteViewsFactory.class.getSimpleName();
    private final Context mContext;
    private Cursor mCursor;
    private static Vehicle vehicle;

    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        LocalStore localStore = new LocalStore(mContext);
        vehicle = localStore.getPreferenceVehicle();
        int transactionId = vehicle.get_id();
        Log.w(LOG_TAG, "onDataSetChanged: " + vehicle.get_id() + " Vehicle: " + vehicle.getRegistration());

        try {
            mCursor = localStore.getTransactionsCursor(transactionId);
        } finally {
            Binder.restoreCallingIdentity(Binder.clearCallingIdentity());
        }
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) return null;
        mCursor.moveToPosition(position);

        String description = mCursor.getString(COL_DESCRIPTION);
        double amount = mCursor.getDouble(COL_AMOUNT);
        String type = mCursor.getString(COL_TYPE);
        String dateTime = mCursor.getString(COL_UPDATE_TIME);

        Transaction transaction = new Transaction(amount,type , description, dateTime);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_widget_transactions);
        description = transaction.getDescription();
        String amountText = String.valueOf(transaction.getAmount());
        dateTime = transaction.getCreated_at();
        views.setTextViewText(R.id.item_widget_description, description);
        views.setTextViewText(R.id.item_widget_amount, amountText);
        views.setTextViewText(R.id.item_widget_date_time, dateTime);
        Log.w(LOG_TAG, "getViewAt: " + getCount()+" "+ description+" "+ amountText);

        Bundle extras = new Bundle();
        extras.putParcelable(TRANSACTION_KEY, transaction);
        extras.putParcelable(VEHICLE_KEY, vehicle);
        Intent fillIntent = new Intent();
        fillIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.row_widget_transaction, fillIntent);
        Log.w(LOG_TAG, "getViewAtClick: " + getCount() + " Vehicle: " + vehicle.getRegistration());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
