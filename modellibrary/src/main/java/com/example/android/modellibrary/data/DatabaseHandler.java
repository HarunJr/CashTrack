package com.example.android.modellibrary.data;

import android.content.ContentResolver;
import android.util.Log;

import com.madrapps.asyncquery.AsyncQueryHandler;

/**
 * Created by HARUN on 8/10/2017.
 */

class DatabaseHandler extends AsyncQueryHandler {

    DatabaseHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onBulkInsertComplete(int token, Object cookie, int result) {
        super.onBulkInsertComplete(token, cookie, result);
        Log.d("DatabaseHandler", "Bulk Insert Done " + result);
    }
}
