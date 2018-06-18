package com.example.android.background.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.android.modellibrary.CashTrackResultReceiver;
import com.example.android.modellibrary.Utilities;
import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.User;
import com.example.android.modellibrary.model.Vehicle;

import static com.example.android.modellibrary.CashTrackResultReceiver.TOKEN_RECEIVER;
import static com.example.android.modellibrary.CashTrackResultReceiver.VEHICLE_RECEIVER;
import static com.example.android.modellibrary.model.Transaction.TRANSACTION_KEY;
import static com.example.android.modellibrary.model.User.USER_KEY;
import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;

public class CashTrackSyncUtils {
    private static final String LOG_TAG = CashTrackSyncUtils.class.getSimpleName();
    public static final String SYNC_VEHICLES = "sync-vehicles";
    public static final String SYNC_TRANSACTIONS = "sync-transactions";
    public static final String SYNC_USER = "sync-user";

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context       Context that will be passed to other methods and used to access the
     *                      ContentResolver
     * @param tokenReceiver ResultReceiver that will be passed to other methods and used to access server results
     */
    synchronized public static void initialize(@NonNull final Context context, final CashTrackResultReceiver tokenReceiver) {
        Log.w(LOG_TAG, "initialize ");
        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
//        if (sInitialized) return;
//
//        sInitialized = true;

//      TODO (13) Call the method you created to schedule a periodic weather sync
        /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        if (Utilities.isNetworkAvailable(context)) {
            Thread checkForToken = new Thread(new Runnable() {
                @Override
                public void run() {
                    LocalStore localStore = new LocalStore(context);
                    User user = null;
                    Log.w(LOG_TAG, "checkForEmpty " + localStore.getToken());

                    /* If the localStore.getToken() is null OR if it was empty, we need to sync immediately to
                     * be able to retrieve the API Token.
                     */
                    if (!localStore.getToken().isEmpty()) {
                        Log.w(LOG_TAG, "Login " + localStore.getToken());
                        user = localStore.getStoredUser();
                        user.setAuth_token(localStore.getToken());
                    } else {
                        user = localStore.getStoredUser();
                        user.setAuth_token("signup");
                        Log.w(LOG_TAG, "signup " + user.getAuth_token());
                    }
                    startImmediateSync(context, user, tokenReceiver);
                }
            });

            /* Finally, once the thread is prepared, fire it off to perform our checks. */
            checkForToken.start();

        } else {
            Toast.makeText(context, com.example.android.background.R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    synchronized public static void syncVehicles(@NonNull final Context context, final CashTrackResultReceiver vehicleReceiver, final Vehicle vehicle) {
        Log.w(LOG_TAG, "syncVehicles ");

//      TODO (13) Call the method you created to schedule a periodic weather sync
        /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        if (Utilities.isNetworkAvailable(context)) {
            if (vehicle != null) {
                Thread vehiclesNotSynced = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (vehicle.getRegistration() != null && vehicle.get_id() == 0) {
                            Log.w(LOG_TAG, "Data to SYNC: " + vehicle.get_id() + "," + vehicle.getRegistration() + ","
                                    + vehicle.getVehicle_model() + "," + vehicle.getManufacture_year() + "\n");
                            startVehicleSync(context, vehicle, vehicleReceiver);

                        } else {
                            Log.w(LOG_TAG, "DO NOT SYNC !!!!!!!!!!!!!!!!");
                        }
                    }
                });

                /* Finally, once the thread is prepared, fire it off to perform our checks. */
                vehiclesNotSynced.start();

            } else {
                //TODO: Use only for re-install after signup
                Vehicle localVehicles = new LocalStore(context).getLocalVehicles();
                if (localVehicles == null){
                    startVehicleSync(context, null, vehicleReceiver);
                }else {
                    Log.w(LOG_TAG, "localVehicles !!!!!!!!!!!!!!!!" + localVehicles.getRegistration());
                }
            }
        } else {
            Toast.makeText(context, com.example.android.background.R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    synchronized public static void syncTransactions(@NonNull final Context context, final Transaction transaction) {
        Log.w(LOG_TAG, "syncTransactions ");

//      TODO (13) Call the method you created to schedule a periodic weather sync
        /*
         * We need to check to see if our ContentProvider has data to display in our forecast
         * list. However, performing a query on the main thread is a bad idea as this may
         * cause our UI to lag. Therefore, we create a thread in which we will run the query
         * to check the contents of our ContentProvider.
         */
        if (Utilities.isNetworkAvailable(context)) {
            if (transaction != null) {
                Thread transactionsNotSynced = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (transaction.getSync().equals("0")) {
                            Log.w(LOG_TAG, "Data to SYNC: " + transaction.getId() + "," + transaction.getType() + ","
                                    + transaction.getAmount() + "," + transaction.getVehicle_id() + "\n");
                            startTransactionSync(context, transaction);
                        } else {
                            Log.w(LOG_TAG, "DO NOT SYNC !!!!!!!!!!!!!!!!");
                        }
                    }
                });
                /* Finally, once the thread is prepared, fire it off to perform our checks. */
                transactionsNotSynced.start();
            } else {
                startTransactionSync(context, null);
            }
        } else {
            Toast.makeText(context, com.example.android.background.R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context       The Context used to start the IntentService for the sync.
     * @param user
     * @param tokenReceiver
     */
    private static void startImmediateSync(@NonNull final Context context, User user, CashTrackResultReceiver tokenReceiver) {
//        Log.w(LOG_TAG, "startImmediateSync "+ user.getAuth_token());
        Intent intentToSyncImmediately = new Intent(context, CashTrackSyncIntentService.class);
        intentToSyncImmediately.putExtra(TOKEN_RECEIVER, tokenReceiver);
        intentToSyncImmediately.putExtra(USER_KEY, user);
        intentToSyncImmediately.setAction(SYNC_USER);
        context.startService(intentToSyncImmediately);
    }

    private static void startVehicleSync(@NonNull final Context context, Vehicle vehicle, CashTrackResultReceiver vehicleReceiver) {
//        Log.w(LOG_TAG, "startImmediateSync "+ user.getAuth_token());
        Intent intentToSyncImmediately = new Intent(context, CashTrackSyncIntentService.class);
        intentToSyncImmediately.putExtra(VEHICLE_RECEIVER, vehicleReceiver);
        intentToSyncImmediately.putExtra(VEHICLE_KEY, vehicle);
        intentToSyncImmediately.setAction(SYNC_VEHICLES);
        context.startService(intentToSyncImmediately);
    }

    private static void startTransactionSync(@NonNull final Context context, Transaction transaction) {
//        Log.w(LOG_TAG, "startImmediateSync "+ user.getAuth_token());
        Intent intentToSyncImmediately = new Intent(context, CashTrackSyncIntentService.class);
        intentToSyncImmediately.putExtra(TRANSACTION_KEY, transaction);
        intentToSyncImmediately.setAction(SYNC_TRANSACTIONS);
        context.startService(intentToSyncImmediately);
    }
}
