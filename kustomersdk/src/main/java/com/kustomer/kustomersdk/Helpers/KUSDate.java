package com.kustomer.kustomersdk.Helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        return null;
    }
    //endregion

    //region Private Methods
    private static DateFormat ISO8601DateFormatterFromString(){
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new Locale("en_US_POSIX"));
    }
    //endregion

}
