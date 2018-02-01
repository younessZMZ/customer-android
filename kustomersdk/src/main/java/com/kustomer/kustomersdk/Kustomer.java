package com.kustomer.kustomersdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;

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
        getSharedInstance().setApiKey(apiKey);
        mContext = context.getApplicationContext();
    }
    //endregion

    //region Class Methods
    public static void describeConversation(HashMap<String,Object> customAttributes){
        //TODO:
    }

    public static void describeCustomer(KUSCustomerDescription customerDescription){
        //TODO:
    }

    public static void identify(String externalToken){
        //TODO:
    }

    public static void resetToken(){
        //TODO:
    }

    public static void showSupport(Activity activity){

        //TODO: Mocking Session Object for POC
        KUSChatSession chatSession =  new KUSChatSession();
        chatSession.setId(KUSConstants.MockedData.CHAT_SESSION_OID);
        chatSession.setOrgId(KUSConstants.MockedData.CHAT_SESSION_ORG_ID);
        chatSession.setCustomerId(KUSConstants.MockedData.CHAT_SESSION_CUSTOMER_ID);
        chatSession.setPreview(KUSConstants.MockedData.CHAT_SESSION_PREVIEW);
        chatSession.setTrackingId(KUSConstants.MockedData.CHAT_SESSION_TRACKING_ID);
        chatSession.setSessionId(KUSConstants.MockedData.CHAT_SESSION_SESSION_ID);



        Intent intent = new Intent(activity, KUSChatActivity.class);
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
