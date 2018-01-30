package com.kustomer.kustomersdk.API;


import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kustomer.kustomersdk.BuildConfig;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Junaid on 1/20/2018.
 */


public class KUSRequestManager implements Serializable{



    //region Properties
    private String baseUrlString;
    private KUSUserSession userSession;

    HashMap<String, String> genericHTTPHeaderValues = null;
    ArrayList<KUSTrackingTokenListener> pendingTrackingTokenListeners = null;
    //endregion

    //region LifeCycle
    public KUSRequestManager (KUSUserSession userSession){
        this.userSession = userSession;

        baseUrlString = String.format("https://%s.api.%s",userSession.getOrgName(), Kustomer.hostDomain());
        genericHTTPHeaderValues = new HashMap<String, String>(){
            {
                put("X-Kustomer","kustomer");
                put("Accept-Language",KUSAcceptLanguageHeaderValue());
                put("User_Agent",KUSUserAgentHeaderValue());
                put("x-kustomer-client","android");
                put("x-kustomer-version", Kustomer.sdkVersion());
            }
        };
    }
    //endregion

    //region URL Methods
    public URL urlForEndpoint(String endpoint){
        String endpointUrlString = String.format("%s%s", baseUrlString, endpoint);
        try {
            return new URL(endpointUrlString);
        } catch (MalformedURLException ignore) {

        }
        return null;
    }
    //endregion

    //region Request Methods
    public void getEndpoint(String endpoint, boolean authenticated, KUSRequestCompletionListener listener){
        performRequestType(KUSRequestType.KUS_REQUEST_TYPE_GET,
                endpoint,
                null,
                authenticated,
                listener);
    }

    public void performRequestType(KUSRequestType type, String endpoint,
                                   HashMap<String, Object> params, boolean authenticated,
                                   KUSRequestCompletionListener listener){

        performRequestType(type,
                urlForEndpoint(endpoint),
                params,
                authenticated,
                listener);
    }

    public void performRequestType(KUSRequestType type, URL url,
                                   HashMap<String, Object> params,
                                   boolean authenticated,
                                   KUSRequestCompletionListener listener){

        performRequestType(type,
                url,
                params,
                authenticated,
                null,
                listener);

    }

    private void performRequestType(KUSRequestType type, URL url,
                                    HashMap<String, Object> params,
                                    boolean authenticated,
                                    HashMap additionalHeaders,
                                    KUSRequestCompletionListener listener){
        performRequestType(type,
                url,
                params,
                null,
                authenticated,
                additionalHeaders,
                listener);

    }

    private void performRequestType(final KUSRequestType type,
                                    final URL url,
                                    final HashMap<String, Object> params,
                                    final byte[] bodyData,
                                    final boolean authenticated,
                                    final HashMap additionalHeaders,
                                    final KUSRequestCompletionListener completionListener){

        if(authenticated){
            dispenseTrackingToken(new KUSTrackingTokenListener() {
                @Override
                public void onCompletion(Error error, String trackingToken) {
                    if(error != null){
                        completionListener.onCompletion(error,null);
                    }else{
                        performRequestWithTrackingToken(type,trackingToken,url,params,bodyData,authenticated,additionalHeaders,completionListener);
                    }
                }
            });
        }else{
            performRequestWithTrackingToken(type,null,url,params,bodyData,authenticated,additionalHeaders,completionListener);
        }

    }
    //endregion

    //region Private Methods
    private void performRequestWithTrackingToken(KUSRequestType type,
                                                 String trackingToken,
                                                 URL url,
                                                 HashMap<String, Object> params,
                                                 byte[] bodyData,
                                                 boolean authenticated,
                                                 HashMap additionalHeaders,
                                                 final KUSRequestCompletionListener completionListener){

        //TODO: Incomplete for requests other than GET & POST

        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.parse(url.toString());
        HttpUrl.Builder httpBuilder = null;
        Request request = null;

        if(httpUrl != null) {
            httpBuilder = httpUrl.newBuilder();

            if(type == KUSRequestType.KUS_REQUEST_TYPE_GET && params != null) {
                    for (String key : params.keySet()) {
                        Object value = params.get(key);

                        String valueString = String.valueOf(value);
                        httpBuilder.addQueryParameter(key, valueString);
                    }
            }

            Request.Builder requestBuilder = requestBuilder = new Request.Builder()
                    .url(httpBuilder.build());

            //Adding headers
            for(Map.Entry<String, String> entry : genericHTTPHeaderValues.entrySet()){
                requestBuilder.addHeader(entry.getKey(),entry.getValue());
            }

            //Adding Additional Headers
            if(additionalHeaders != null) {
                for (Object key : additionalHeaders.keySet()) {
                    String keyString = String.valueOf(key);
                    requestBuilder.addHeader(keyString, String.valueOf(additionalHeaders.get(keyString)));
                }
            }

            if(type == KUSRequestType.KUS_REQUEST_TYPE_POST){
                if(bodyData != null){
                    //TODO: incomplete
                }else{
                    if(params != null){

                        //TODO: Need to enhance
                        JSONObject jsonObject = new JSONObject();
                        for (String key: params.keySet()) {
                            try {
                                jsonObject.put(key, params.get(key));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        byte []bytes = jsonObject.toString().getBytes();

                        requestBuilder.post(RequestBody.create(MediaType.parse("application/json"), bytes));
                        requestBuilder.addHeader("Content-Length", String.valueOf(bytes.length));
                        requestBuilder.addHeader("Content-Type", "application/json");
                    }
                }
            }

            if(authenticated && trackingToken != null)
                requestBuilder.addHeader(KUSConstants.Keys.K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY,trackingToken);


            request = requestBuilder.build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    completionListener.onCompletion(new Error(e.getMessage()),null);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.body() != null) {
                        String body = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            completionListener.onCompletion(null, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

    }


    private void dispenseTrackingToken(KUSTrackingTokenListener listener){
        //TODO:
        listener.onCompletion(null,KUSConstants.MockedData.TRACKING_TOKEN);
    }


    private static String KUSAcceptLanguageHeaderValue(){
        //TODO: To be changed Later
        return "en-us";
    }

    private static String KUSUserAgentHeaderValue(){

        //Screen Scale not passed
        return String.format(Locale.US,"%s/%s (%s; android %s; Scale/ 0.2f)",
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME,
                Build.MODEL,
                Build.VERSION.RELEASE);
    }
    //endregion

    //region Request Completion Interface
    public interface KUSTrackingTokenListener{
        void onCompletion(Error error, String trackingToken);
    }
    //endregion

}
