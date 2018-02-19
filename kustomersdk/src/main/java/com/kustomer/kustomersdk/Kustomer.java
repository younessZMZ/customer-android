package com.kustomer.kustomersdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Activities.KUSSessionsActivity;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSCustomerDescription;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Junaid on 1/20/2018.
 */

public class Kustomer {

    //region Properties
    private static Context mContext;
    private static Kustomer sharedInstance = null;

    private KUSUserSession userSession;

    private String apiKey;
    private String orgId;
    private String orgName;

    private static String hostDomainOverride = null;
    //endregion

    //region LifeCycle
    public static Kustomer getSharedInstance(){
        if(sharedInstance == null)
            sharedInstance = new Kustomer();

        return sharedInstance;
    }

    public static void init(Context context, String apiKey) {
        mContext = context.getApplicationContext();
        getSharedInstance().setApiKey(apiKey);
    }
    //endregion

    //region Class Methods
    public static void describeConversation(HashMap<String,Object> customAttributes){
        getSharedInstance().mDescribeConversation(customAttributes);
    }

    public static void describeCustomer(KUSCustomerDescription customerDescription){
        getSharedInstance().mDescribeCustomer(customerDescription);
    }

    public static void identify(String externalToken){
        getSharedInstance().mIdentify(externalToken);
    }

    public static void resetToken(){
        getSharedInstance().mResetTracking();
    }

    public static void showSupport(Activity activity){

        if(activity != null) {
            Intent intent = new Intent(activity, KUSSessionsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.kus_slide_up, R.anim.stay);
        }
    }

    public static void presentKnowledgeBase(){
        //TODO:
    }
    //endregion

    //region Private Methods
    private void mDescribeConversation(HashMap<String,Object> customAttributes){
        if (customAttributes==null)
            throw new AssertionError("Attempted to describe a conversation with no attributes set");

        if(customAttributes.keySet().size() == 0)
            return;

        userSession.getChatSessionsDataSource().describeActiveConversation(customAttributes);
    }

    private void mDescribeCustomer(KUSCustomerDescription customerDescription){
        userSession.describeCustomer(customerDescription,null);
    }

    private void mIdentify(final String externalToken){
        if(externalToken == null)
            return;

        HashMap<String , Object> params = new HashMap<String , Object>(){{
            put("externalToken",externalToken);
        }};

        final KUSUserSession instance = this.userSession;
        userSession.getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                KUSConstants.URL.IDENTITY_ENDPOINT,
                params,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        instance.getTrackingTokenDataSource().fetch();
                    }
                }
        );
    }

    private void mResetTracking(){
        userSession = new KUSUserSession(orgName,orgId,true);
    }

    private void setApiKey(String apiKey){
        if(apiKey.length()==0){
            return;
        }

        String []apiKeyParts = apiKey.split("[.]");
        if(apiKeyParts.length<=2)
            return;

        JSONObject tokenPayload = null;
        try {
            tokenPayload = jsonFromBase64EncodedJsonString(apiKeyParts[1]);
            this.apiKey = apiKey;
            orgId = tokenPayload.getString(KUSConstants.Keys.K_KUSTOMER_ORG_ID_KEY);
            orgName = tokenPayload.getString(KUSConstants.Keys.K_KUSTOMER_ORG_NAME_KEY);

            if(orgName.length()==0)
                return;

            userSession = new KUSUserSession(orgName,orgId);
        } catch (JSONException ignore) {}

    }

    private JSONObject jsonFromBase64EncodedJsonString(String base64EncodedJson )throws JSONException{
        byte[] array = Base64.decode(base64EncodedJson,Base64.NO_PADDING);
        return new JSONObject(new String(array));
    }
    //endregion

    //region Public Methods
    public static String sdkVersion(){
        return BuildConfig.VERSION_NAME;
    }

    public static String hostDomain(){
        return hostDomainOverride != null ? hostDomainOverride : KUSConstants.URL.HOST_NAME;
    }

    public static void setHostDomain(String hostDomain){
        hostDomainOverride = hostDomain;
    }

    public static Context getContext() {
        return mContext;
    }

    public KUSUserSession getUserSession() {
        return userSession;
    }
    //endregion

}
