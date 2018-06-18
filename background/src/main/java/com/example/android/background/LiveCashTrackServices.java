package com.example.android.background;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.example.android.background.infrastructure.CashTrackApplication;
import com.example.android.modellibrary.data.CashTrackContract;
import com.example.android.modellibrary.data.LocalStore;
import com.example.android.modellibrary.model.ParentModel;
import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.User;
import com.example.android.modellibrary.model.Vehicle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.modellibrary.CashTrackResultReceiver.TOKEN_RECEIVER;
import static com.example.android.modellibrary.CashTrackResultReceiver.VEHICLE_RECEIVER;
import static com.example.android.modellibrary.model.User.TOKEN_KEY;
import static com.example.android.modellibrary.model.Vehicle.VEHICLE_KEY;

/**
 * Created by HARUN on 12/18/2016.
 */

public class LiveCashTrackServices extends BaseLiveService {
    public static final String LOG_TAG = LiveCashTrackServices.class.getSimpleName();
    private static String token = null;

    LiveCashTrackServices(CashTrackApplication application, CashTrackWebServices api) {
        super(application, api);
    }

    synchronized public static void postTransactionMessage(final Context baseContext, final Transaction transaction) {
        final LocalStore localStore = new LocalStore(baseContext);
        token = localStore.getToken();
        Call<ParentModel> call;

        if (transaction != null) {
            Log.w(LOG_TAG, "postTransactionMessage: " + transaction.getVehicle_id() + "," + transaction.getAmount() + ","
                    + transaction.getType() + "," + transaction.getCreated_at() + "\n" + token);
            call = api.postTransaction(token, transaction.getVehicle_id(), transaction.getAmount(), transaction.getDescription());
        } else {
            Log.w(LOG_TAG, "getTransactions: ");
            call = api.getTransactions(token);
        }

        assert call != null;
        call.enqueue(new Callback<ParentModel>() {

            @Override
            public void onResponse(Call<ParentModel> call, Response<ParentModel> response) {
                Log.w(LOG_TAG, "onResponse: " + response.code());
                int code = response.code();
                switch (code) {
                    case 200:

                        if (transaction != null) {
                            getTransactionFromServer(response, localStore);

                        } else {
                            Log.w(LOG_TAG, "TRANSACTION IS NULL: ");
                            baseContext.getContentResolver().delete(CashTrackContract.TransactionEntry.CONTENT_URI, null, null);
                            getTransactionsFromServer(response, localStore);

                        }
                }
            }

            @Override
            public void onFailure(Call<ParentModel> call, Throwable t) {
                call.cancel();
                Log.w(LOG_TAG, "onFailure: " + t.getMessage() + "\n" + call.toString());
            }
        });
    }

    private static void getTransactionsFromServer(Response<ParentModel> response, LocalStore localStore) {
        Log.w(LOG_TAG, "getTransactionInfoAfterPost: " + response.body().getStatus());
        if (response.body().getStatus() == response.code()) {
            for (Transaction transaction : response.body().getTransactionList()) {
                String sync = "1";
                transaction = new Transaction(transaction.getId(), transaction.getVehicle_id(), transaction.getAmount(), transaction.getType(), transaction.getDescription(), transaction.getTimestamp(), transaction.getCreated_at(), transaction.getUpdated_at(), sync);
                Log.w(LOG_TAG, "getTransactionInfoAfterPost: " + transaction.getVehicle_id() + " " + transaction.getAmount() + " " + transaction.getCreated_at());
                localStore.storeTransactionData(transaction);
                localStore.getVehicleDataFromTransaction(transaction.getVehicle_id());
            }
//            updateVehiclesTable(vehicle);

        }
    }

    private static void getTransactionFromServer(Response<ParentModel> response, LocalStore localStore) {
        Log.w(LOG_TAG, "getTransactionFromServer: " + response.body().getTransactionObject().getAmount());
        Transaction transaction = response.body().getTransactionObject();
        String sync = "1";
        transaction = new Transaction(transaction.getId(), transaction.getVehicle_id(), transaction.getAmount(), transaction.getType(), transaction.getDescription(), transaction.getTimestamp(), transaction.getCreated_at(), transaction.getUpdated_at(), sync);
        localStore.updateTransactionsTable(transaction);
    }


    //Send vehicle data to server
    synchronized public static void postVehicleMessage(final Context baseContext, final Intent intent, final Vehicle vehicle) {
        final LocalStore localStore = new LocalStore(baseContext);
        token = localStore.getToken();
        Call<ParentModel> call;
        if (vehicle != null) {
            Log.w(LOG_TAG, "postVehicleMessage: " + vehicle.get_id() + "," + vehicle.getRegistration() + ","
                    + vehicle.getVehicle_model() + "," + vehicle.getManufacture_year() + "\n" + token);
//        User Two user2@mail.com offloader, +1445566778, 1234
            call = api.createVehicle(token, vehicle.getRegistration(), vehicle.getVehicle_model(), vehicle.getManufacture_year()
                    , vehicle.getSeating_capacity());

        } else {
            call = api.getVehicles(token);
        }

        assert call != null;
        call.enqueue(new Callback<ParentModel>() {

            @Override
            public void onResponse(Call<ParentModel> call, Response<ParentModel> response) {
                Log.w(LOG_TAG, "onResponse: " + response.code());
                int code = response.code();
                switch (code) {
                    case 200:

                        final ResultReceiver receiver = intent.getParcelableExtra(VEHICLE_RECEIVER);
                        if (receiver != null) {
                            getVehicleFromServer(response, receiver);

                        } else {
                            Log.w(LOG_TAG, "RECEIVER IS NULL: ");
                            getVehiclesFromServer(response, localStore);
                        }

                    case 422:
                        Log.w(LOG_TAG, "REGISTRATION TAKEN ");

                }
            }

            @Override
            public void onFailure(Call<ParentModel> call, Throwable t) {
                call.cancel();
                Log.w(LOG_TAG, "onFailure: " + t.getMessage() + "\n" + call.toString());
            }
        });
    }

    private static void getVehiclesFromServer(Response<ParentModel> response, LocalStore localStore) {
        for (Vehicle vehiclesModel : response.body().getVehicles()) {
            Log.w(LOG_TAG, "onResponse vehicleReg: " + vehiclesModel.get_id() + "\n" + vehiclesModel.getRegistration()
                    + "\n signUpDate: " + vehiclesModel.getUpdated_at() + "\nCollection: " + vehiclesModel.getCollection()
                    + "\nExpense: " + vehiclesModel.getExpense() + "\nExpense: " + vehiclesModel.getUpdated_at());

            localStore.storeVehicleData(vehiclesModel);
        }
    }

    private static void getVehicleFromServer(Response<ParentModel> response, ResultReceiver receiver) {
        Log.w(LOG_TAG, "onResponse: " + response.body().getVehicleRe().getRegistration());
        Vehicle vehicle = response.body().getVehicleRe();
        int status = response.body().getStatus();
        switch (status) {
            case 422:
                //TODO: Display message to user
                //email or phone number has already been taken
                Log.w(LOG_TAG, "REGISTRATION HAS BEEN TAKEN ");

            default:
                if (vehicle != null) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(VEHICLE_KEY, vehicle);
                    receiver.send(response.code(), bundle);
                } else {
                    Log.w(LOG_TAG, "VEHICLE IS NULL: ");
                }
        }
    }

    //Send user data to server
    synchronized public static void postUserMessage(final Context baseContext, final Intent intent, final User user) {
        Log.w(LOG_TAG, "postUserMessage: " + user.getAuth_token() + "," + user.getRole() + ","
                + user.getPhoneNo() + "," + user.getPin() + ",");

        Call<ParentModel> call;
        token = user.getAuth_token();
        switch (token) {
            case "signup":
                Log.w(LOG_TAG, "SIGN UP >>>> ");
                call = api.registerUser(token, user.getName(), user.getEmail(),
                        user.getPhoneNo(), user.getCompany(), user.getPin());
                break;
            case "login":
                Log.w(LOG_TAG, "LOGIN >>>> ");
                call = api.loginUser(user.getPhoneNo(), user.getPin());
                break;
            default:
                Log.w(LOG_TAG, "DEFAULT >>>> ");
                call = api.loginUser(user.getPhoneNo(), user.getPin());
        }

        assert call != null;
        call.enqueue(new Callback<ParentModel>() {

            @Override
            public void onResponse(Call<ParentModel> call, Response<ParentModel> response) {
                Log.w(LOG_TAG, "onResponse: " + response.code());
                int code = response.code();
                switch (code) {
                    case 200:
                        getTokenFromServer(baseContext, response, intent);
                }
            }

            @Override
            public void onFailure(Call<ParentModel> call, Throwable t) {
                call.cancel();
                Log.w(LOG_TAG, "onFailure: " + t.getMessage() + "\n" + call.toString());
            }
        });
    }

    private static void getTokenFromServer(Context baseContext, Response<ParentModel> response, Intent intent) {
        Log.w(LOG_TAG, "getUserDataFromServer " + response.body().getToken());
        String token = response.body().getToken();
        int status = response.body().getStatus();

        switch (status) {
            case 422:
                //TODO: Set Firebase phone/email authentication before login
                //email or phone number has already been taken
                Log.w(LOG_TAG, "status " + response.body().getStatus());
                LocalStore localStore = new LocalStore(baseContext);
                User user = localStore.getStoredUser();
                user.setAuth_token("login");
                postUserMessage(baseContext, intent, user);

            default:
                if (token != null) {
                    ResultReceiver receiver = intent.getParcelableExtra(TOKEN_RECEIVER);
                    Bundle bundle = new Bundle();
                    bundle.putString(TOKEN_KEY, token);
                    receiver.send(response.code(), bundle);
                } else {
                    Log.w(LOG_TAG, "TOKEN IS NULL: ");

                }
        }
    }
}
