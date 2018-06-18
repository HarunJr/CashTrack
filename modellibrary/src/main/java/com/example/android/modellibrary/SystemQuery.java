package com.example.android.modellibrary;

import com.example.android.modellibrary.data.CashTrackContract;

public class SystemQuery {
    public static final String INCOME_TAG = "vehicle_collection";
    public static final String EXPENSE_TAG = "vehicle_expense";

    public static final String[] VEHICLE_COLUMN = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the vehicle & transactions tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            CashTrackContract.VehicleEntry.TABLE_NAME + "." + CashTrackContract.VehicleEntry.COLUMN_VEHICLE_ID,
            CashTrackContract.VehicleEntry.COLUMN_VEHICLE_REGISTRATION,
            CashTrackContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION,
            CashTrackContract.VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE,
            CashTrackContract.VehicleEntry.COLUMN_UPDATE_TIME,

            CashTrackContract.VehicleEntry.COLUMN_VEHICLE_MAKE,
            CashTrackContract.VehicleEntry.COLUMN_VEHICLE_MODEL,
            CashTrackContract.VehicleEntry.COLUMN_VEHICLE_CAPACITY,
            CashTrackContract.VehicleEntry.COLUMN_MANUFACTURE_YEAR,
            CashTrackContract.VehicleEntry.COLUMN_VEHICLE_CREATION_DATE,

    };

    // These indices are tied to VEHICLE_COLUMN.  If VEHICLE_COLUMN changes, these
    // must change.
    public static final int COL_VEHICLE_ID = 0;
    public static final int COL_VEHICLE_REGISTRATION = 1;
    public static final int COL_VEHICLE_COLLECTION = 2;
    public static final int COL_VEHICLE_EXPENSE = 3;
    public static final int COL_UPDATE_TIME = 4;

    public static final int COL_VEHICLE_MAKE = 5;
    public static final int COL_VEHICLE_MODEL = 6;
    public static final int COL_VEHICLE_CAPACITY = 7;
    public static final int COL_MANUFACTURE_YEAR = 8;
    public static final int COL_VEHICLE_CREATION_DATE = 9;

    public static final String[] TRANSACTION_COLUMNS = {
            CashTrackContract.TransactionEntry.TABLE_NAME + "." + CashTrackContract.TransactionEntry.COLUMN_TRANSACTION_ID,
            CashTrackContract.TransactionEntry.COLUMN_VEHICLE_KEY,
            CashTrackContract.TransactionEntry.COLUMN_AMOUNT,
            CashTrackContract.TransactionEntry.COLUMN_TYPE,
            CashTrackContract.TransactionEntry.COLUMN_DESCRIPTION,
            CashTrackContract.TransactionEntry.COLUMN_TIME_STAMP,
    };

    public static final int COL_TRANSACTION_ID = 0;
    public static final int COL_VEHICLE_KEY = 1;
    public static final int COL_AMOUNT = 2;
    public static final int COL_TYPE = 3;
    public static final int COL_DESCRIPTION = 4;
    public static final int COL_TIME_STAMP = 5;


}
