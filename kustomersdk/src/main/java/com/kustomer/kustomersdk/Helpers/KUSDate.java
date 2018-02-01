package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSDate {

    //region properties
    private static int TWO_DAYS_MILLIS = 48 * 60 * 60 *1000;
    private static DateFormat shortDateFormat;
    private static DateFormat shortTimeFormat;
    //endregion

    //region Static Methods
    public static String humanReadableTextFromDate(Date date){
        return null;
    }

    public static String messageTimeStampTextFromDate(Date date){
        long now = System.currentTimeMillis();

        //2days
        if(now-date.getTime() <= TWO_DAYS_MILLIS){
            return DateUtils.getRelativeTimeSpanString(date.getTime(),
                    now,
                    DateUtils.DAY_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE).toString() + shortTimeFormatter().format(date);
        }else
            return shortRelativeDateFormatter().format(date);

    }

    public static Date dateFromString (String string){
        if(string != null && string.length() > 0)
            try {
                return ISO8601DateFormatterFromString().parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        return null;
    }

    public static String stringFromDate(Date date){
        if(date != null){
            return ISO8601DateFormatterFromDate().format(date);
        }else
            return null;
    }
    //endregion

    //region Private Methods
    private static DateFormat shortRelativeDateFormatter(){
        if(shortDateFormat == null) {
            shortDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a",new Locale("en_US_POSIX"));
        }

        return shortDateFormat;
    }

    private static DateFormat shortTimeFormatter(){
        if(shortTimeFormat == null) {
            shortTimeFormat = new SimpleDateFormat(", h:mm a",new Locale("en_US_POSIX"));
        }

        return shortTimeFormat;
    }

    private static DateFormat ISO8601DateFormatterFromString(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", new Locale("en_US_POSIX"));
    }

    private static DateFormat ISO8601DateFormatterFromDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("en_US_POSIX"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat;
    }
    //endregion

}
