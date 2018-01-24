package com.kustomer.kustomersdk.Models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.kustomer.kustomersdk.Utils.JsonHelper.boolFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTrackingToken extends KUSModel {

    public String trackingId;
    public String token;
    public Boolean verified;


    public boolean initWithJSON(JSONObject json)  {
        boolean val = super.initWithJSON(json);

        if(!val)
            return false;

        trackingId = stringFromKeyPath(json, "attributes.trackingId");
        token = stringFromKeyPath(json, "attributes.token");
        verified = boolFromKeyPath(json, "attributes.verified");

        return true;
    }


    public String modelType() {
        return "tracking_token";
    }
}
