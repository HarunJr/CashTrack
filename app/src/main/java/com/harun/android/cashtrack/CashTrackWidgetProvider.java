package com.harun.android.cashtrack;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.example.android.cashtracklibrary.CashTrackActivity;
import com.example.android.modellibrary.model.Vehicle;

/**
 * Implementation of App Widget functionality.
 */
public class CashTrackWidgetProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, Vehicle vehicle, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = getTransactionListRemoteView(context, vehicle);

        // Set the DetailsActivity intent to launch when clicked
        Intent intent = new Intent(context, CashTrackActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_view, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static RemoteViews getTransactionListRemoteView(Context context, Vehicle vehicle) {
//        Recipe recipe = new LocalStore(context).getPreferenceVehicle();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        views.setTextViewText(R.id.widget_text_view, vehicle.getRegistration());

        // Set the ListWidgetService intent to act as the adapter for the ListView
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        // Handle empty steps
        views.setEmptyView(R.id.widget_list_view, R.id.empty_widget_view);
        return views;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        CashTrackService.startActionUpdateTransactionWidget(context);
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    public static void updateTransactionsWidgets(Context context, AppWidgetManager appWidgetManager,
                                                 Vehicle vehicle, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, vehicle, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

