package com.kustomer.kustomersdk.API;


import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.os.ConfigurationCompat;
import android.util.Log;

import com.kustomer.kustomersdk.BuildConfig;
import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Junaid on 1/20/2018.
 */


public class KUSRequestManager implements Serializable, KUSObjectDataSourceListener {



    //region Properties
    private String baseUrlString;
    private KUSUserSession userSession;

    HashMap<String, String> genericHTTPHeaderValues = null;
    private ArrayList<KUSTrackingTokenListener> pendingTrackingTokenListeners  = null;
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

        userSession.getTrackingTokenDataSource().addListener(this);
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

    public void performRequestType(KUSRequestType type, URL url,
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

    public void performRequestType(final KUSRequestType type,
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
                        safeComplete(completionListener,error,null);
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


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

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

            Request.Builder requestBuilder = new Request.Builder()
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

            if(type != KUSRequestType.KUS_REQUEST_TYPE_GET){
                byte []bytes = null;

                if(bodyData != null){
                    bytes = bodyData;
                }else{
                    if(params != null){
                        JSONObject jsonObject = new JSONObject();
                        for (String key: params.keySet()) {
                            try {
                                jsonObject.put(key, params.get(key));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        bytes = jsonObject.toString().getBytes();
                    }
                }

                RequestBody reqbody = null;
                requestBuilder.addHeader("Content-Type", "application/json");
                //Tracking token can be null but we need to define the request type
                if(bytes != null) {
                    requestBuilder.addHeader("Content-Length", String.valueOf(bytes.length));
                    reqbody = RequestBody.create(MediaType.parse("application/json"), bytes);
                }else{
                    reqbody = RequestBody.create(MediaType.parse("application/json"), new byte[0]);
                }

                if (type == KUSRequestType.KUS_REQUEST_TYPE_POST)
                    requestBuilder.post(reqbody);
                else if (type == KUSRequestType.KUS_REQUEST_TYPE_PUT)
                    requestBuilder.put(reqbody);
                else if (type == KUSRequestType.KUS_REQUEST_TYPE_PATCH)
                    requestBuilder.patch(reqbody);

            }

            if(authenticated && trackingToken != null)
                requestBuilder.addHeader(KUSConstants.Keys.K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY,trackingToken);


            request = requestBuilder.build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    safeComplete(completionListener,new Error(e.getMessage()), null);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.body() != null) {
                        String body = response.body().string();

                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            safeComplete(completionListener,null, jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

    }

private void safeComplete(final KUSRequestCompletionListener completionListener, final Error error, final JSONObject jsonObject){
        completionListener.onCompletion(error, jsonObject);
    }


    private void dispenseTrackingToken(final KUSTrackingTokenListener listener){
        String trackingToken = userSession.getTrackingTokenDataSource().getCurrentTrackingToken();
        if(trackingToken != null){
            listener.onCompletion(null,trackingToken);
        }else{
            getPendingTrackingTokenListeners().add(listener);
            userSession.getTrackingTokenDataSource().fetch();
        }
    }

    private void firePendingTokenCompletionsWithToken(final String token, final Error error){
        final ArrayList<KUSTrackingTokenListener> listeners = new ArrayList<>(getPendingTrackingTokenListeners());
        pendingTrackingTokenListeners = null;

        if(listeners.size() > 0){
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    for(KUSTrackingTokenListener trackingTokenListener : listeners){
                        trackingTokenListener.onCompletion(error,token);
                    }
                }
            };
            handler.post(runnable);
        }
    }


    private static String KUSAcceptLanguageHeaderValue(){
        return ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).toString();
    }

    private static String KUSUserAgentHeaderValue(){

        return String.format(Locale.getDefault(),"%s/%s (%s; android %s;)",
                BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_NAME,
                Build.MODEL,
                Build.VERSION.RELEASE);
    }

    private ArrayList<KUSTrackingTokenListener> getPendingTrackingTokenListeners(){
        if(pendingTrackingTokenListeners == null)
            pendingTrackingTokenListeners = new ArrayList<>();

        return pendingTrackingTokenListeners;
    }
    //endregion

    //region Callbacks
    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        if(dataSource == userSession.getTrackingTokenDataSource()){
            String trackingToken = userSession.getTrackingTokenDataSource().getCurrentTrackingToken();
            firePendingTokenCompletionsWithToken(trackingToken,null);
        }
    }

    @Override
    public void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error) {
        firePendingTokenCompletionsWithToken(null,error);
    }
    //endregion

    //region Request Completion Interface
    public interface KUSTrackingTokenListener{
        void onCompletion(Error error, String trackingToken);
    }
    //endregion

}
