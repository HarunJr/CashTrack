package com.example.android.modellibrary.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.modellibrary.DateHelper;
import com.example.android.modellibrary.data.CashTrackContract.VehicleEntry;
import com.example.android.modellibrary.model.Transaction;
import com.example.android.modellibrary.model.User;
import com.example.android.modellibrary.model.Vehicle;

import java.util.ArrayList;

import static com.example.android.modellibrary.SystemQuery.COL_AMOUNT;
import static com.example.android.modellibrary.SystemQuery.COL_DESCRIPTION;
import static com.example.android.modellibrary.SystemQuery.COL_TIME_STAMP;
import static com.example.android.modellibrary.SystemQuery.COL_TYPE;
import static com.example.android.modellibrary.SystemQuery.COL_UPDATE_TIME;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_COLLECTION;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_EXPENSE;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_ID;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_KEY;
import static com.example.android.modellibrary.SystemQuery.COL_VEHICLE_REGISTRATION;
import static com.example.android.modellibrary.SystemQuery.EXPENSE_TAG;
import static com.example.android.modellibrary.SystemQuery.INCOME_TAG;
import static com.example.android.modellibrary.SystemQuery.TRANSACTION_COLUMNS;
import static com.example.android.modellibrary.SystemQuery.VEHICLE_COLUMN;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.COLUMN_AMOUNT;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.COLUMN_DATE_TIME;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.COLUMN_DESCRIPTION;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.COLUMN_TIME_STAMP;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.COLUMN_TRANSACTION_ID;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.COLUMN_TYPE;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.COLUMN_VEHICLE_KEY;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry.SYNC;
import static com.example.android.modellibrary.data.CashTrackProvider.sTransactionWithVehicleIdSelection;
import static com.example.android.modellibrary.data.CashTrackProvider.sVehicleWithIdSelection;

public class LocalStore {
    private static final String LOG_TAG = LocalStore.class.getSimpleName();
    private SQLiteDatabase db;
    private final DbHelper dbHelper;
    private final Context mContext;
    private SharedPreferences userLocal_SP, token_SP;

    public LocalStore(Context context) {
        this.mContext = context;
        dbHelper = new DbHelper(mContext);
        userLocal_SP = context.getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
        token_SP = context.getSharedPreferences(LOG_TAG, Context.MODE_PRIVATE);
    }

    //Shared preference storage
    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.putString("name", user.getName());
        spEditor.putString("email", user.getEmail());
        spEditor.putString("phoneNo", user.getPhoneNo());
        spEditor.putString("company", user.getCompany());
        spEditor.putString("pin", user.getPin());
        spEditor.putString("role", user.getRole());
        spEditor.apply();
    }

    public User getStoredUser() {
        String name = userLocal_SP.getString("name", "");
        String email = userLocal_SP.getString("email", "");
        String phoneNo = userLocal_SP.getString("phoneNo", "");
        String company = userLocal_SP.getString("company", "");
        String pin = userLocal_SP.getString("pin", "");
        String role = userLocal_SP.getString("role", "");
        Log.w(LOG_TAG, name + " " + email + " " + role + ", " + phoneNo + ", " + pin);

        return new User(name, email, phoneNo, pin, company, role);
    }

    public void clearData() {
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.clear();
        spEditor.apply();
    }

//    public void clearToken() {
//        SharedPreferences.Editor spEditor = token_SP.edit();
//        spEditor.clear();
//        spEditor.apply();
//    }

    public void storeToken(String token) {
        Log.d(LOG_TAG, "storeToken: " + token);
        SharedPreferences.Editor spEditor = token_SP.edit();
        spEditor.putString("token", token);
        spEditor.apply();
    }

    public String getToken() {
        String token = token_SP.getString("token", "");
        Log.w(LOG_TAG, token);
        return token;
    }

    private void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    private void close() {
        db.close();
    }

    public boolean storeVehicleData(Vehicle vehicle) {
        open();
        ArrayList<ContentValues> vehicleList = new ArrayList<>();
        ContentValues vehicleValues = new ContentValues();

        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_ID, vehicle.get_id());
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_REGISTRATION, vehicle.getRegistration());
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION, vehicle.getCollection());
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE, vehicle.getExpense());
        vehicleValues.put(VehicleEntry.COLUMN_UPDATE_TIME, vehicle.getUpdated_at());

        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_MAKE, vehicle.getVehicle_make());
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_MODEL, vehicle.getVehicle_model());
        vehicleValues.put(VehicleEntry.COLUMN_MANUFACTURE_YEAR, vehicle.getManufacture_year());
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_CAPACITY, vehicle.getSeating_capacity());
        vehicleValues.put(VehicleEntry.COLUMN_VEHICLE_CREATION_DATE, vehicle.getCreated_at());

        vehicleList.add(vehicleValues);

        if (vehicleList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[vehicleList.size()];
            vehicleList.toArray(cvArray);
            Log.w(LOG_TAG, "storeVehicleData: " + vehicle.get_id() + " " + vehicle.getRegistration() + ">>");

            //TODO: bulkInsert With Handler
            DatabaseHandler handler = new DatabaseHandler(mContext.getContentResolver());
            handler.startBulkInsert(1, null, VehicleEntry.CONTENT_URI, cvArray);
            close();
            return true;
        } else {
            close();
            return false;
        }
    }

    public void storeVehiclePreference(Vehicle vehicle) {
        Log.w(LOG_TAG, "storeRecipeData: " + vehicle.getRegistration());
        SharedPreferences.Editor spEditor = userLocal_SP.edit();
        spEditor.putInt("id", vehicle.get_id());
        spEditor.putString("registration", vehicle.getRegistration());
        spEditor.apply();
    }

    public Vehicle getPreferenceVehicle() {
        int id = userLocal_SP.getInt("id", 0);
        String registration = userLocal_SP.getString("registration", "");

        Log.w(LOG_TAG, id + ", " + registration);

        return new Vehicle(id, registration);
    }


    public boolean storeTransactionData(Transaction transaction) {
        open();
        ArrayList<ContentValues> transactionList = new ArrayList<>();
        ContentValues transactionValues = new ContentValues();

        transactionValues.put(COLUMN_TRANSACTION_ID, transaction.getId());
        transactionValues.put(COLUMN_VEHICLE_KEY, transaction.getVehicle_id());
        transactionValues.put(COLUMN_AMOUNT, transaction.getAmount());
        transactionValues.put(COLUMN_TYPE, transaction.getType());
        transactionValues.put(COLUMN_DESCRIPTION, transaction.getDescription());
        transactionValues.put(COLUMN_TIME_STAMP, transaction.getTimestamp());
        transactionValues.put(COLUMN_DATE_TIME, transaction.getCreated_at());
        transactionValues.put(SYNC, transaction.getCreated_at());
        transactionList.add(transactionValues);

        if (transactionList.size() > 0) {
            ContentValues[] cvArray = new ContentValues[transactionList.size()];
            transactionList.toArray(cvArray);
            Log.w(LOG_TAG, "storeTransactionData " + transaction.getId() + " >>" + transaction.getVehicle_id() + " Synced: " + transaction.getSync());

            // bulkInsert With Handler
            DatabaseHandler handler = new DatabaseHandler(mContext.getContentResolver());
            handler.startBulkInsert(1, null, TransactionEntry.CONTENT_URI, cvArray);
            close();

            return true;
        } else {
            close();
            return false;
        }
    }

//    private Transaction getTransactionsWithKey(Vehicle vehicle){
//        cursor = getLocalCursor(TransactionEntry.CONTENT_URI, VEHICLE_COLUMN);
//        assert cursor != null;
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            do {
//                int vehicleId = cursor.getInt(COL_VEHICLE_ID);
//                String vehicleReg = cursor.getString(COL_VEHICLE_REGISTRATION);
//                double collection = cursor.getDouble(COL_VEHICLE_COLLECTION);
//                double expense = cursor.getDouble(COL_VEHICLE_EXPENSE);
//                String updatedDate = cursor.getString(COL_UPDATE_TIME);
//                Log.w(LOG_TAG, "getLocalVehicles: " + vehicleId + " " + vehicleReg + ">>" + collection + ">>" + expense + ">>" + updatedDate);
//
//                vehicle = new Vehicle(vehicleId, vehicleReg, collection, expense, updatedDate);
//            } while (cursor.moveToNext());
//            cursor.close();
//        } else {
//            Log.w(LOG_TAG, "cursor is null: " + cursor.getCount() + " \ntransactionWithId: ");
//            cursor.close();
//        }
//        return ;
//
//    }

    public boolean updateTransactionsTable(Transaction transaction) {
        open();
        ArrayList<ContentValues> transactionList = new ArrayList<>();
        ContentValues contentValues = new ContentValues();

//        contentValues.put(TransactionEntry.COLUMN_TRANSACTION_ID, transaction.getId());
        contentValues.put(TransactionEntry.SYNC, transaction.getSync());
        transactionList.add(contentValues);

        if (transactionList.size() > 0) {
            String[] selectionArgs = new String[]{String.valueOf(transaction.getVehicle_id())};
            DatabaseHandler handler = new DatabaseHandler(mContext.getContentResolver());
            handler.startUpdate(1, null, TransactionEntry.CONTENT_URI, contentValues, sTransactionWithVehicleIdSelection, selectionArgs);

            return true;
        }
        close();
        return false;
    }

    public void updateVehiclesTable(Vehicle vehicle) {
        open();
        ArrayList<ContentValues> vehicleList = new ArrayList<>();
        ContentValues contentValues = new ContentValues();

        contentValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION, vehicle.getCollection());
        contentValues.put(VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE, vehicle.getExpense());
        contentValues.put(VehicleEntry.COLUMN_UPDATE_TIME, vehicle.getUpdated_at());
        vehicleList.add(contentValues);

        if (vehicleList.size() > 0) {
            String[] selectionArgs = new String[]{String.valueOf(vehicle.get_id())};
            DatabaseHandler handler = new DatabaseHandler(mContext.getContentResolver());
            handler.startUpdate(1, null, VehicleEntry.CONTENT_URI, contentValues, sVehicleWithIdSelection, selectionArgs);
        }
        close();
    }

    public Cursor getLocalCursor(Uri uri, String[] column) {
        return mContext.getContentResolver().query(
                uri, column, null, null, null);
    }

    public Cursor getTransactionsCursor(int vehicleId) {
        Log.w(LOG_TAG, "getLocalSteps: ");

        Uri uri = getTransactionsWithDateAndIdUri(vehicleId);
        Cursor cursor = mContext.getContentResolver().query(
                uri, TRANSACTION_COLUMNS, null, null, null);

        assert cursor != null;
        return cursor;
    }


    public Vehicle getLocalVehicles() {
        Vehicle vehicle = null;
        Cursor cursor = getLocalCursor(VehicleEntry.CONTENT_URI, VEHICLE_COLUMN);
        assert cursor != null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                int vehicleId = cursor.getInt(COL_VEHICLE_ID);
                String vehicleReg = cursor.getString(COL_VEHICLE_REGISTRATION);
                double collection = cursor.getDouble(COL_VEHICLE_COLLECTION);
                double expense = cursor.getDouble(COL_VEHICLE_EXPENSE);
                String updatedDate = cursor.getString(COL_UPDATE_TIME);
                Log.w(LOG_TAG, "getLocalVehicles: " + vehicleId + " " + vehicleReg + ">>" + collection + ">>" + expense + ">>" + updatedDate);

                vehicle = new Vehicle(vehicleId, vehicleReg, collection, expense, updatedDate);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.w(LOG_TAG, "cursor is null: " + cursor.getCount() + " \ntransactionWithId: ");
            cursor.close();
        }
        return vehicle;
    }

//    public Vehicle getVehicleById(Vehicle vehicle) {
//        Log.w(LOG_TAG, "getVehicleById: " + vehicle.get_id() + " " + vehicle.getRegistration());
//        Uri vehicleDataWithId = VehicleEntry.buildVehicleDataWithId(vehicle.get_id());
//        cursor = getLocalCursor(vehicleDataWithId, VEHICLE_COLUMN);
//        assert cursor != null;
//        if (cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            int vehicleId = cursor.getInt(COL_VEHICLE_ID);
//            String vehicleReg = cursor.getString(COL_VEHICLE_REGISTRATION);
//            String vehicleModel = cursor.getString(COL_VEHICLE_MODEL);
//            String yOM = cursor.getString(COL_MANUFACTURE_YEAR);
//            String seatingCap = cursor.getString(COL_VEHICLE_CAPACITY);
//
//            double collection = cursor.getDouble(COL_VEHICLE_COLLECTION);
//            double expense = cursor.getDouble(COL_VEHICLE_EXPENSE);
//            String updatedDate = cursor.getString(COL_UPDATE_TIME);
//
//            Log.w(LOG_TAG, "getLocalVehicles: " + vehicleId + " " + vehicleReg + ">>" + collection + ">>" + expense + ">>" + updatedDate);
//            Log.w(LOG_TAG, "getVehicleById: " + vehicleId + " " + vehicleReg + ">>" + vehicleModel + ">>" + yOM + ">>" + seatingCap);
//
//            vehicle = new Vehicle(vehicleId, vehicleReg, vehicleModel, yOM, seatingCap);
//        }
//        cursor.close();
//
//        return vehicle;
//    }

    private Uri getTransactionsWithDateAndIdUri(int vehicleKey) {
        String startDate = null;
        String endDate = null;

        //24 hours in a day
        //minutes in a day = hrs * 60
        //seconds in a day = minutes * 60
        //milliseconds in a day = seconds * 1000
        long dateTimeMillis = System.currentTimeMillis();
        long dayInMilli = (24 * 60 * 60) * 1000;
        long yesterday = dateTimeMillis - dayInMilli;
        long tomorrow = dateTimeMillis + dayInMilli;
        String todayDate = DateHelper.getFormattedDateHyphenString(dateTimeMillis);

        //TODO: Run app at 3:01 am;
        final String yesterdayStartDate = DateHelper.getFormattedDateHyphenString(yesterday) + " 03:00:00";
        final String todayStartDate = todayDate + " 03:00:00";
        final String tomorrowEndDate = DateHelper.getFormattedDateHyphenString(tomorrow) + " 03:00:00";

        long todayStartDateMilli = DateHelper.getMilliDateTimeFromString(todayStartDate);
        long todayDateMilli = DateHelper.getMilliDateTimeFromString(todayDate + " 00:00:00");
        long tomorrowEndDateMilli = DateHelper.getMilliDateTimeFromString(tomorrowEndDate);

        if (dateTimeMillis > todayDateMilli && dateTimeMillis < todayStartDateMilli) {
            startDate = yesterdayStartDate;
            endDate = todayStartDate;
        } else if (dateTimeMillis > todayStartDateMilli && dateTimeMillis < tomorrowEndDateMilli) {
            startDate = todayStartDate;
            endDate = tomorrowEndDate;
        }
        Log.w(LOG_TAG, "getTransactionsWithDateAndIdUri " + vehicleKey + " " + todayDate + " " + endDate);

        return TransactionEntry.buildKeyTransactionWithDateUri(vehicleKey, startDate, endDate);
    }

    public Vehicle getVehicleDataFromTransaction(int vehicleKey) {
        Log.w(LOG_TAG, "getVehicleDataFromTransactions " + vehicleKey);
        open();
        Uri transactionWithId = getTransactionsWithDateAndIdUri(vehicleKey);
        String sortOrder = VehicleEntry.COLUMN_UPDATE_TIME + " DESC";
        Log.w(LOG_TAG, "transactionWithId " + transactionWithId);

        Cursor cursor = mContext.getContentResolver().query(transactionWithId, TRANSACTION_COLUMNS, null, null, sortOrder);
        //declare variables before loop
        double collection = 0.0;
        double expense = 0.0;

        assert cursor != null;
        if (cursor.getCount() > 0) {
            Log.w(LOG_TAG, "cursor: " + cursor.getCount() + "\ntransactionWithId: " + transactionWithId);
            Vehicle vehicle = null;
            cursor.moveToFirst();
            do {
                vehicleKey = cursor.getInt(COL_VEHICLE_KEY);
                double amount = cursor.getDouble(COL_AMOUNT);
                String type = cursor.getString(COL_TYPE);
                String description = cursor.getString(COL_DESCRIPTION);
                long timeStamp = cursor.getLong(COL_TIME_STAMP);

                String dateTime = DateHelper.getFormattedDate(timeStamp);
                Log.w(LOG_TAG, "getAmountType: " + vehicleKey + ">>" + amount + ">>" + type + ">>" + dateTime);

                switch (type) {
                    case INCOME_TAG:
                        collection += amount;
                        vehicle = new Vehicle(vehicleKey, collection, expense, dateTime);

                        Log.w(LOG_TAG, "getVehicleCollection: " + vehicleKey + ">>" + collection + ">>" + expense + ">>" + type + ">>" + dateTime);
                        break;
                    case EXPENSE_TAG:
                        expense += amount;
                        vehicle = new Vehicle(vehicleKey, collection, expense, dateTime);
                        Log.w(LOG_TAG, "getVehicleExpense: " + vehicleKey + ">>" + collection + ">>" + expense + ">>" + type + ">>" + dateTime);
                        break;
                    default:
                        Log.w(LOG_TAG, "getVehicleTransaction: " + vehicle.get_id() + ">>" + vehicle.getRegistration() + ">>" + vehicle.getCollection() + ">>" + vehicle.getExpense() + ">>" + vehicle.getUpdated_at());
                }
            } while (cursor.moveToNext());

            Log.w(LOG_TAG, "getVehicleTransaction>>>: " + vehicle.get_id() + ">>" + vehicle.getRegistration() + ">>" + vehicle.getCollection() + ">>" + vehicle.getExpense() + ">>" + vehicle.getUpdated_at());
            return vehicle;
        } else {
            Log.w(LOG_TAG, "cursor is null: " + cursor.getCount() + " \ntransactionWithId: " + transactionWithId);
            cursor.close();
            close();
            return null;
        }
    }
}
