package com.example.android.modellibrary.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;

import static com.example.android.modellibrary.data.CashTrackContract.CONTENT_AUTHORITY;
import static com.example.android.modellibrary.data.CashTrackContract.PATH_TRANSACTIONS;
import static com.example.android.modellibrary.data.CashTrackContract.PATH_VEHICLES;
import static com.example.android.modellibrary.data.CashTrackContract.TransactionEntry;
import static com.example.android.modellibrary.data.CashTrackContract.VehicleEntry;

@SuppressWarnings("ConstantConditions")
public class CashTrackProvider extends ContentProvider {
    private static final String LOG_TAG = CashTrackProvider.class.getSimpleName();
    private DbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int VEHICLES = 100;
    private static final int VEHICLE_WITH_ID = 101;

    private static final int TRANSACTIONS = 200;
    private static final int TRANSACTION_WITH_VEHICLE_ID_AND_DATE = 201;

    private static final SQLiteQueryBuilder sTransactionByVehicleQueryBuilder;

    static {
        sTransactionByVehicleQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //transaction INNER JOIN vehicle ON transaction.vehicle_id = vehicle._id
        sTransactionByVehicleQueryBuilder.setTables(TransactionEntry.TABLE_NAME
                + " INNER JOIN " +
                VehicleEntry.TABLE_NAME
                + " ON "
                + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_VEHICLE_KEY +
                " = " + VehicleEntry.TABLE_NAME + "." + VehicleEntry.COLUMN_VEHICLE_ID);
    }

    private static final String sTransactionsWithVehicleIdAndStartDateSelection =
            TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_VEHICLE_KEY + " = ? AND "
                    + TransactionEntry.TABLE_NAME + "." + TransactionEntry.COLUMN_DATE_TIME + " BETWEEN ? AND ?";

    public static final String sVehicleWithIdSelection =
            VehicleEntry.TABLE_NAME +
                    "." + VehicleEntry.COLUMN_VEHICLE_ID + " = ?";

    public static final String sTransactionWithVehicleIdSelection =
            TransactionEntry.TABLE_NAME +
                    "." + TransactionEntry.COLUMN_VEHICLE_KEY + " = ?";

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // For each URI you want to add, create a corresponding code.
        matcher.addURI(CONTENT_AUTHORITY, PATH_VEHICLES, VEHICLES);
        matcher.addURI(CONTENT_AUTHORITY, PATH_VEHICLES + "/#", VEHICLE_WITH_ID);

        matcher.addURI(CONTENT_AUTHORITY, PATH_TRANSACTIONS, TRANSACTIONS);
        matcher.addURI(CONTENT_AUTHORITY, PATH_TRANSACTIONS + "/#/*/*", TRANSACTION_WITH_VEHICLE_ID_AND_DATE);

        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case VEHICLES: {
                Log.w(LOG_TAG, "called; VEHICLES");
                retCursor = getVehicles(projection, sortOrder);
                break;
            }
            case VEHICLE_WITH_ID: {
                Log.w(LOG_TAG, "called; VEHICLE_WITH_ID");
                retCursor = getVehicleWithId(uri, projection);
                break;
            }
            case TRANSACTIONS: {
                Log.w(LOG_TAG, "called; TRANSACTIONS");
                retCursor = getTransactions(projection);
                break;
            }
            case TRANSACTION_WITH_VEHICLE_ID_AND_DATE: {
                retCursor = getTransactionsByVehicleIdAndStartDate(uri, projection, sortOrder);

                Log.w(LOG_TAG, "called; TRANSACTION_WITH_VEHICLE_ID_AND_DATE " + uri + "\n" + Arrays.toString(projection));
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        assert retCursor != null;
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getTransactionsByVehicleIdAndStartDate(Uri uri, String[] projection, String sortOrder) {
        String vehicleKey = TransactionEntry.getVehicleIdFromUri(uri);
        String startDate = TransactionEntry.getStartDateFromUri(uri);
        String endDate = TransactionEntry.getEndDateFromUri(uri);

        Log.w(LOG_TAG, "getTransactionsByVehicleIdAndStartDate: " + vehicleKey + "\n" + startDate + " " + endDate);

        String[] selectionArgs = new String[]{vehicleKey, startDate, endDate};
        String selection = sTransactionsWithVehicleIdAndStartDateSelection;

        Log.w(LOG_TAG, "selectionArgs: " + Arrays.toString(selectionArgs) + "\n" + selection);
        Cursor cursor = sTransactionByVehicleQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        Log.d(LOG_TAG, "getTransactionsByVehicleIdAndStartDate: \n" + selection + "\n : "
                + Arrays.toString(selectionArgs) + "\n" + cursor.getCount());

        return cursor;
    }


    //Retrieve all Transaction items from the database
    private Cursor getTransactions(String[] projection) {

        return sTransactionByVehicleQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Cursor getVehicleWithId(Uri uri, String[] projection) {
        int id = VehicleEntry.getVehicleId(uri);
        String[] selectionArgs = {String.valueOf(id)};

        return mOpenHelper.getReadableDatabase().query(
                VehicleEntry.TABLE_NAME,
                projection,
                sVehicleWithIdSelection,
                selectionArgs,
                null,
                null,
                null
        );
    }


    private Cursor getVehicles(String[] projection, String sortOrder) {
        Cursor cursor = mOpenHelper.getReadableDatabase().query(
                VehicleEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        Log.w(LOG_TAG, "getLocalVehicles\n : " + Arrays.toString(projection) + "\n" + cursor.getCount());
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case VEHICLES:
                return VehicleEntry.CONTENT_TYPE;
            case TRANSACTIONS:
                return TransactionEntry.CONTENT_TYPE;
            case TRANSACTION_WITH_VEHICLE_ID_AND_DATE:
                return TransactionEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        Log.w(LOG_TAG, "called; delete");
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case VEHICLES:
                rowsDeleted = db.delete(
                        VehicleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRANSACTIONS:
                rowsDeleted = db.delete(
                        TransactionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (null == selection) selection = "1";
        switch (match) {
            case VEHICLES:
                rowsUpdated = db.update(
                        VehicleEntry.TABLE_NAME, values, selection, selectionArgs);
                Log.w(LOG_TAG, "called; update VEHICLES");
                break;
            case TRANSACTIONS:
                rowsUpdated = db.update(
                        TransactionEntry.TABLE_NAME, values, selection, selectionArgs);
                Log.w(LOG_TAG, "called; update TRANSACTIONS");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        // this makes delete all rows return the number of rows deleted
        switch (match) {
            case VEHICLES:
                int returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(VehicleEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        Log.w(LOG_TAG, "bulkInsert; VEHICLES: " + value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case TRANSACTIONS:
                returnCount = 0;
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(TransactionEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        Log.w(LOG_TAG, "bulkInsert; TRANSACTIONS: " + value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
