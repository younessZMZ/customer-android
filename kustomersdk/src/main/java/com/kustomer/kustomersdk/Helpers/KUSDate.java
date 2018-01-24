package com.kustomer.kustomersdk.Helpers;

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

    //region Static Methods
    public static String humanReadableTextFromDate(Date date){
        return null;
    }

    public static String messageTimeStampTextFromDate(Date date){
        return null;
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
