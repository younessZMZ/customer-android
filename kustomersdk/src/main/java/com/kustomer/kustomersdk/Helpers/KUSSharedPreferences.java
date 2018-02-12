package com.kustomer.kustomersdk.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSSharedPreferences {
    //TODO: Incomplete
    //region Properties
    private static String PREFERENCE_FILE_KEY = "kustomer_app_preferences";
    private static String TRACKING_TOKEN_PREFERENCE = "tracking_token_pref";
    //endregion

    //region Basic Methods
    private static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(key, value);
        editor.apply();
    }

    private static int getInt(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, MODE_PRIVATE);

        return sharedPref.getInt(key, -1);
    }

    private static void saveBoolean(Context context, String key, boolean check) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, check);
        editor.apply();
    }

    private static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, MODE_PRIVATE);
        return sharedPref.getBoolean(key, true);
    }

    private static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getString(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_FILE_KEY, MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    //endregion

    //region Public Methods
    public void setTrackingToken(Context context, String trackingToken) {
        saveString(context,TRACKING_TOKEN_PREFERENCE,trackingToken);
    }

    public String getTrackingToken(Context context) {
        return getString(context,TRACKING_TOKEN_PREFERENCE);
    }
    //endregion

}
