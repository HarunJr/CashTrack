package com.example.android.modellibrary.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import static com.example.android.modellibrary.data.CashTrackContract.*;
import static com.example.android.modellibrary.data.CashTrackContract.VehicleEntry;

class DbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cash_track.db";
    private static final int DATABASE_VERSION = 1;
    private final Context mContext;

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String SQL_CREATE_VEHICLE_TABLE = "CREATE TABLE "
                    + VehicleEntry.TABLE_NAME + " ("
                    + VehicleEntry.COLUMN_VEHICLE_ID + " INTEGER NOT NULL, "
                    + VehicleEntry.COLUMN_VEHICLE_REGISTRATION + " TEXT PRIMARY KEY, "
                    + VehicleEntry.COLUMN_VEHICLE_TOTAL_COLLECTION + " REAL, "
                    + VehicleEntry.COLUMN_VEHICLE_TOTAL_EXPENSE + " REAL, "
                    + VehicleEntry.COLUMN_VEHICLE_MAKE + " TEXT, "
                    + VehicleEntry.COLUMN_VEHICLE_MODEL + " TEXT, "
                    + VehicleEntry.COLUMN_VEHICLE_CAPACITY + " INTEGER, "
                    + VehicleEntry.COLUMN_MANUFACTURE_YEAR + " TEXT, "
                    + VehicleEntry.COLUMN_VEHICLE_CREATION_DATE + " INTEGER, "
                    + VehicleEntry.COLUMN_UPDATE_TIME + " TEXT NOT NULL, "

                    + "UNIQUE (" + VehicleEntry.COLUMN_VEHICLE_REGISTRATION + ") ON CONFLICT REPLACE );";

            String SQL_CREATE_TRANSACTION_TABLE = "CREATE TABLE "
                    + TransactionEntry.TABLE_NAME + " ("
                    + TransactionEntry.COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TransactionEntry.COLUMN_VEHICLE_KEY + " TEXT NOT NULL, "
                    + TransactionEntry.COLUMN_AMOUNT + " REAL NOT NULL, "
                    + TransactionEntry.COLUMN_TYPE + " TEXT NOT NULL, "
                    + TransactionEntry.COLUMN_DESCRIPTION + " VARCHAR(255), "
                    + TransactionEntry.COLUMN_TIME_STAMP + " INTEGER NOT NULL, "
                    + TransactionEntry.COLUMN_DATE_TIME + " TEXT NOT NULL, "
                    + TransactionEntry.SYNC + " TEXT NOT NULL, "
                    + "FOREIGN KEY (" + TransactionEntry.COLUMN_VEHICLE_KEY + ") REFERENCES "
                    + VehicleEntry.TABLE_NAME + "(" + VehicleEntry.COLUMN_VEHICLE_ID +
                    ") " + ");";

            db.execSQL(SQL_CREATE_VEHICLE_TABLE);
            db.execSQL(SQL_CREATE_TRANSACTION_TABLE);

        } catch (SQLException e) {
            Toast.makeText(mContext, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String SQL_DROP_VEHICLE_TABLE = "DROP TABLE IF EXISTS " + VehicleEntry.TABLE_NAME;
            String SQL_DROP_TRANSACTION_TABLE = "DROP TABLE IF EXISTS " + TransactionEntry.TABLE_NAME;

            db.execSQL(SQL_DROP_VEHICLE_TABLE);
            db.execSQL(SQL_DROP_TRANSACTION_TABLE);
            onCreate(db);
        } catch (SQLException e) {
            Toast.makeText(mContext, "" + e, Toast.LENGTH_LONG).show();
        }
    }
}
