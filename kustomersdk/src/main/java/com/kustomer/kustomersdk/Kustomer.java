package com.kustomer.kustomersdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Activities.KUSChatActivity;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSCustomerDescription;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.kustomer.kustomersdk.Utils.KUSConstants.BundleName.USER_SESSION_BUNDLE__KEY;

/**
 * Created by Junaid on 1/20/2018.
 */

public class Kustomer {

    //region Properties
    private static Kustomer sharedInstance = null;
    private static String kKustomerOrgIdKey = "org";
    private static String kKustomerOrgNameKey = "orgName";

    KUSUserSession userSession;

    String apiKey;
    String orgId;
    String orgName;

    private static String hostDomainOverride = null;
    //endregion

    //region LifeCycle
    public static Kustomer getSharedInstance(){
        if(sharedInstance == null)
            sharedInstance = new Kustomer();

        return sharedInstance;
    }
    //endregion

    //region Class Methods
    public static void initializeWithAPIKey(String apiKey){
        getSharedInstance().setApiKey(apiKey);
    }

    public static void describeConversation(HashMap<String,Object> customAttributes){

    }

    public static void describeCustomer(KUSCustomerDescription customerDescription){

    }

    public static void identify(String externalToken){

    }

    public static void resetToken(){

    }

    public static void showSupport(Activity activity){

        //TODO: need to mock this
        KUSChatSession chatSession =  new KUSChatSession();
        chatSession.oid = "5a62e560f3fbb800014f7a27";
        chatSession.orgId = "5a5f6ca3b573fd0001af73dd";
        chatSession.customerId = "5a62e55cf3fbb800014f7a11";
        chatSession.preview = "Yt";
        chatSession.trackingId = "5a62e559a64a1d0010b2ced5";
        chatSession.sessionId = "5a6083aff105640001c8f96c";



        Intent intent = new Intent(activity, KUSChatActivity.class);
        intent.putExtra(USER_SESSION_BUNDLE__KEY,Kustomer.sharedInstance.userSession);
        intent.putExtra(KUSConstants.BundleName.CHAT_SESSION_BUNDLE__KEY,chatSession);
        activity.startActivity(intent);
    }
    //endregion

    //region Private Methods
    private void setApiKey(String apiKey){
        if(apiKey.length()==0){
            return;
        }

        String []apiKeyParts = apiKey.split("[.]");
        if(apiKeyParts.length<=2)
            return;

        //String base64EncodedTokenJson = paddedBase64String(apiKeyParts[1]);
        JSONObject tokenPayload = null;
        try {
            tokenPayload = jsonFromBase64EncodedJsonString(apiKeyParts[1]);
            this.apiKey = apiKey;
            orgId = tokenPayload.getString(kKustomerOrgIdKey);
            orgName = tokenPayload.getString(kKustomerOrgNameKey);

            if(orgName.length()==0)
                return;

            Log.v("KUSTOMER","kustomer initialized for organization" + orgName);
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
        return hostDomainOverride != null ? hostDomainOverride : "kustomerapp.com";
    }

    public static void setHostDomain(String hostDomain){
        hostDomainOverride = hostDomain;
    }
    //endregion

}
