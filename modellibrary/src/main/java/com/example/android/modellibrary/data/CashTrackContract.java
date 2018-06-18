package com.example.android.modellibrary.data;

import android.content.ContentResolver;
import android.net.Uri;

public class CashTrackContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    static final String CONTENT_AUTHORITY = "com.harun.android.cashtrack";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    static final String PATH_VEHICLES = "vehicles";

    public static final class VehicleEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VEHICLES).build();

        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLES;
//        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "vehicle";

        public static final String COLUMN_VEHICLE_ID = "vehicle_id";
        public static final String COLUMN_VEHICLE_REGISTRATION = "vehicle_registration";
        public static final String COLUMN_VEHICLE_TOTAL_COLLECTION = "vehicle_collection";
        public static final String COLUMN_VEHICLE_TOTAL_EXPENSE = "vehicle_expense";

        public static final String COLUMN_VEHICLE_MAKE = "vehicle_make";
        public static final String COLUMN_VEHICLE_MODEL = "vehicle_model";
        public static final String COLUMN_VEHICLE_CAPACITY = "passenger_capacity";
        public static final String COLUMN_MANUFACTURE_YEAR = "manufacture_year";
        public static final String COLUMN_VEHICLE_CREATION_DATE = "date_time_created";
        public static final String COLUMN_UPDATE_TIME = "update_time";

        public static Uri buildVehicleDataWithId(int vehicleId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(vehicleId))
                    .build();
        }

        public static int getVehicleId(Uri mUri) {
            return Integer.parseInt(mUri.getPathSegments().get(1));
        }
    }

    // Possible paths (appended to base content URI for possible URI's)
    static final String PATH_TRANSACTIONS = "transactions";

    public static final class TransactionEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;

        public static final String TABLE_NAME = "transactions";

        public static final String COLUMN_TRANSACTION_ID = "_id";
        public static final String COLUMN_VEHICLE_KEY = "vehicle_Key";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TIME_STAMP = "time_stamp";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String SYNC = "sync";

        public static Uri buildKeyTransactionWithDateUri(int vehicleId, String startDate, String endDate) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(vehicleId))
                    .appendPath(startDate)
                    .appendPath(endDate)
                    .build();
        }

        // Get the Transaction id from Uri
//        public static String getVehicleIdFromTransactionUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }

        public static String getVehicleIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getEndDateFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }

    }
}
