package com.example.android.modellibrary;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    public static String getFormattedDate(long dateTimeMillis) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.getDefault());
        return formatter.format(new Date(dateTimeMillis));
    }

    public static String getFormattedTimeString(long dateTimeMillis) {
        DateFormat formatter = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        return formatter.format(new Date(dateTimeMillis));
    }

    public static String getFormattedDateHyphenString(long dateTimeMillis) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(new Date(dateTimeMillis));
    }

    public static long getMilliDateTimeFromString(String stringDate){
        DateTimeFormatter dayFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dateTime = DateTime.parse(stringDate, dayFormat);
        return dateTime.getMillis();
    }
}
