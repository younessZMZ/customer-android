package com.kustomer.kustomersdk.Interfaces;

import org.json.JSONObject;

/**
 * Created by Junaid on 1/29/2018.
 */

public interface KUSRequestCompletionListener{
    void onCompletion(Error error, JSONObject response);
}
