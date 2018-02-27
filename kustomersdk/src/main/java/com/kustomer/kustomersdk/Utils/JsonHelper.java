package com.kustomer.kustomersdk.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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

            String value = keys.length > 0 ? jsonObject.getString(keys[keys.length - 1]) : jsonObject.getString(keyPath);
            return value.equals("null") ? null : value;
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

    public static ArrayList<String> arrayListFromKeyPath(JSONObject jsonObject, String keyPath) {
        try {
            Gson googleJson = new Gson();
            String[] keys = keyPath.split("[.]");
            for (int i = 0; i < keys.length - 1; i++) {
                jsonObject = jsonObject.getJSONObject(keys[i]);
            }
            JSONArray jsonArray = keys.length > 0 ? jsonObject.getJSONArray(keys[keys.length - 1]) : jsonObject.getJSONArray(keyPath);

            Type listType = new TypeToken<ArrayList<String>>(){}.getType();
            return googleJson.fromJson(jsonArray.toString(), listType);
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

    public static JSONObject jsonObjectFromKeyPath(JSONObject json, String keyPath){

        try {
            return json.getJSONObject(keyPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject stringToJson(String jsonString){
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<KUSModel> kusChatModelsFromJSON(JSONObject jsonObject) {
        if(jsonObject == null)
            return null;

        KUSChatMessage standardChatMessage = null;

        try {
            standardChatMessage = new KUSChatMessage(jsonObject);
        } catch (KUSInvalidJsonException e) {
            e.printStackTrace();
        }

        if(standardChatMessage == null)
            return new ArrayList<>();

        String body = KUSUtils.KUSUnescapeBackslashesFromString(standardChatMessage.getBody());
        standardChatMessage.setBody(body);

        //The markdown url pattern we want to detect
        String imagePattern = "!\\[.*\\]\\(.*\\)";
        List<KUSModel> chatMessages = new ArrayList<>();

        Pattern regex = Pattern.compile(imagePattern);


        // TODO: Incomplete

        chatMessages.add(standardChatMessage);

        return chatMessages;
    }

    public static List<KUSModel> kusSessionModelsFromJSON(JSONObject jsonObject) {

        return null;
    }

}
