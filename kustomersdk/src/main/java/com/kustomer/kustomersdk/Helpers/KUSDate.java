package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.text.format.DateUtils;

import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSDate {

    //region properties
    private static int TWO_DAYS_MILLIS = 48 * 60 * 60 *1000;
    private static int SECONDS_PER_MINUTE = 60;
    private static int MINUTES_PER_HOUR = 60;
    private static int HOURS_PER_DAY = 24;
    private static int DAYS_PER_WEEK = 7;

    private static DateFormat shortDateFormat;
    private static DateFormat shortTimeFormat;
    //endregion

    //region Static Methods
    public static String humanReadableTextFromDate(Date date){
        if(date == null)
            return null;

        long timeAgo = (Calendar.getInstance().getTimeInMillis() - date.getTime()) / 1000;
        if(timeAgo >= SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY * DAYS_PER_WEEK){
            long count = timeAgo /(SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY * DAYS_PER_WEEK);
            return agoWithTextCountAndUnit(count, Kustomer.getContext().getString(R.string.week));
        } else if(timeAgo >= SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY){
            long count = timeAgo /(SECONDS_PER_MINUTE * MINUTES_PER_HOUR * HOURS_PER_DAY);
            return agoWithTextCountAndUnit(count,Kustomer.getContext().getString(R.string.day));
        } else  if(timeAgo >= SECONDS_PER_MINUTE * MINUTES_PER_HOUR){
            long count = timeAgo /(SECONDS_PER_MINUTE * MINUTES_PER_HOUR);
            return agoWithTextCountAndUnit(count,Kustomer.getContext().getString(R.string.hour));
        } else if(timeAgo >= SECONDS_PER_MINUTE){
            long count = timeAgo /(SECONDS_PER_MINUTE);
            return agoWithTextCountAndUnit(count,Kustomer.getContext().getString(R.string.minute));
        } else {
            return Kustomer.getContext().getString(R.string.just_now);
        }


    }

    private static String agoWithTextCountAndUnit(long count, String unit){
        int mCount = (int) count;
        if(mCount > 1)
            return String.format(Locale.getDefault(),"%d %ss ago",mCount,unit);
        else
            return String.format(Locale.getDefault(),"%d %s ago",mCount,unit);
    }

    public static String messageTimeStampTextFromDate(Date date){

        if(date != null){
            long now = System.currentTimeMillis();

            //2days
            if (now - date.getTime() <= TWO_DAYS_MILLIS) {
                return DateUtils.getRelativeTimeSpanString(date.getTime(),
                        now,
                        DateUtils.DAY_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE).toString() + shortTimeFormatter().format(date);
            } else
                return shortRelativeDateFormatter().format(date);
        }else {
            return "";
        }

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
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("en_US_POSIX"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat;
    }

    private static DateFormat ISO8601DateFormatterFromDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("en_US_POSIX"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat;
    }
    //endregion

}
