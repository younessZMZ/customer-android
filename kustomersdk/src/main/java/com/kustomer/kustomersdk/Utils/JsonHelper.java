package com.kustomer.kustomersdk.Utils;

import com.google.gson.Gson;
import com.kustomer.kustomersdk.Helpers.KUSDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Junaid on 1/23/2018.
 */

public class JsonHelper {

    public static URL urlFromKeyPath(JSONObject jsonObject, String keyPath) {
        String value = stringFromKeyPath(jsonObject, keyPath);

        if (value != null)
            try {
                return new URL(value);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        return null;
    }

    public static String stringFromKeyPath(JSONObject jsonObject, String keyPath){
        try {
            String[] keys = keyPath.split("[.]");
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            return keys.length > 0 ? jsonObject.getString(keys[keys.length - 1]) : jsonObject.getString(keyPath);
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONArray arrayFromKeyPath(JSONObject jsonObject, String keyPath) {
        try {
            String[] keys = keyPath.split("[.]");
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            return keys.length > 0 ? jsonObject.getJSONArray(keys[keys.length - 1]) : jsonObject.getJSONArray(keyPath);
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList arrayListFromKeyPath(JSONObject jsonObject, String keyPath) {
        try {
            Gson googleJson = new Gson();
            String[] keys = keyPath.split("[.]");
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            JSONArray jsonArray = keys.length > 0 ? jsonObject.getJSONArray(keys[keys.length - 1]) : jsonObject.getJSONArray(keyPath);
            return googleJson.fromJson(jsonArray.toString(), ArrayList.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean boolFromKeyPath(JSONObject jsonObject, String keyPath) {
        try {
            String[] keys = keyPath.split("[.]");
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            return keys.length > 0 ? jsonObject.getBoolean(keys[keys.length - 1]) : jsonObject.getBoolean(keyPath);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer integerFromKeyPath(JSONObject jsonObject, String keyPath) {
        try {
            String[] keys = keyPath.split("[.]");
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            return keys.length > 0 ? jsonObject.getInt(keys[keys.length - 1]) : jsonObject.getInt(keyPath);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date dateFromKeyPath(JSONObject jsonObject, String keyPath) {

        try {
            String[] keys = keyPath.split("[.]");
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            String value = keys.length > 0 ? jsonObject.getString(keys[keys.length - 1]) : jsonObject.getString(keyPath);
            if (value != null)
                return KUSDate.dateFromString(value);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject jsonObjectFromString(JSONObject json, String key){

        try {
            return json.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject jsonFromString(String jsonString){
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
