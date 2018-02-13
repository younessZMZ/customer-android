package com.kustomer.kustomersdk.API;

import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.DataSources.KUSChatSessionsDataSource;
import com.kustomer.kustomersdk.DataSources.KUSChatSettingsDataSource;
import com.kustomer.kustomersdk.DataSources.KUSDelegateProxy;
import com.kustomer.kustomersdk.DataSources.KUSFormDataSource;
import com.kustomer.kustomersdk.DataSources.KUSTrackingTokenDataSource;
import com.kustomer.kustomersdk.DataSources.KUSUserDataSource;
import com.kustomer.kustomersdk.Helpers.KUSSharedPreferences;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSCustomerDescription;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSUserSession implements Serializable {

    //region Properties
    private String orgId;
    private String orgName;
    private String organizationName; //UserFacing


    private KUSChatSessionsDataSource chatSessionsDataSource;
    private KUSChatSettingsDataSource chatSettingsDataSource;
    private KUSTrackingTokenDataSource trackingTokenDataSource;
    private KUSFormDataSource formDataSource;

    private HashMap<String, KUSUserDataSource> userDataSources;
    private HashMap<String, KUSChatMessagesDataSource> chatMessagesDataSources;

    private KUSRequestManager requestManager;
    private KUSPushClient pushClient;
    private KUSDelegateProxy delegateProxy;

    private KUSSharedPreferences sharedPreferences;

    boolean shouldCaptureEmail;
    //endregion

    //region LifeCycle
    public KUSUserSession(String orgName, String orgId, boolean reset){
        this.orgName = orgName;
        this.orgId = orgId;

        if(this.orgName != null && this.orgName.length() > 0) {
            String firstLetter = this.orgName.substring(0,1).toUpperCase();
            this.organizationName = firstLetter.concat(this.orgName.substring(1));
        }

        if(reset){
            trackingTokenDataSource.reset();
            getSharedPreferences().reset();
        }

        getChatSettingsDataSource().fetch();
        getPushClient();
    }

    public KUSUserSession(String orgName, String orgId){
        this(orgName,orgId,false);
    }

    //endregion


    //region public methods
    public KUSChatMessagesDataSource chatMessageDataSourceForSessionId(String sessionId){
        if(sessionId.length() == 0)
            return null;

        KUSChatMessagesDataSource chatMessagesDataSource = getChatMessagesDataSources().get(sessionId);
        if (chatMessagesDataSource == null) {
            chatMessagesDataSource = new KUSChatMessagesDataSource(this, sessionId);
            chatMessagesDataSources.put(sessionId, chatMessagesDataSource);
        }
        return chatMessagesDataSource;
    }

    public KUSUserDataSource userDataSourceForUserId(String userId) {
        if(userId == null || userId.length() == 0 || userId.equals("__team"))
            return null;

        KUSUserDataSource userDataSource = getUserDataSources().get(userId);
        if(userDataSource == null){
            userDataSource = new KUSUserDataSource(this,userId);
            getUserDataSources().put(userId,userDataSource);
        }

        return userDataSource;
    }

    public void submitEmail(String emailAddress){
        //TODO:
    }

    public void describeCustomer(KUSCustomerDescription customerDescription, KUSRequestCompletionListener listener){
        //TODO:
    }


    //endregion

    //region Getters

    public KUSChatSessionsDataSource getChatSessionsDataSource() {
        if (chatSessionsDataSource == null)
            chatSessionsDataSource = new KUSChatSessionsDataSource(this);
        return chatSessionsDataSource;
    }

    public KUSChatSettingsDataSource getChatSettingsDataSource() {
        if (chatSettingsDataSource == null)
            chatSettingsDataSource = new KUSChatSettingsDataSource(this);
        return chatSettingsDataSource;
    }

    public KUSTrackingTokenDataSource getTrackingTokenDataSource() {
        if (trackingTokenDataSource == null)
            trackingTokenDataSource = new KUSTrackingTokenDataSource(this);
        return trackingTokenDataSource;
    }

    public KUSFormDataSource getFormDataSource() {
        if (formDataSource == null)
            formDataSource = new KUSFormDataSource();
        return formDataSource;
    }

    public KUSRequestManager getRequestManager() {
        if (requestManager == null)
            requestManager = new KUSRequestManager(this);
        return requestManager;
    }

    public KUSPushClient getPushClient() {
        if (pushClient == null)
            pushClient = new KUSPushClient(this);
        return pushClient;
    }


    public KUSSharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = new KUSSharedPreferences(Kustomer.getContext(),this);
        return sharedPreferences;
    }

    public HashMap<String, KUSUserDataSource> getUserDataSources() {
        if (userDataSources == null)
            userDataSources = new HashMap<>();
        return userDataSources;
    }

    public HashMap<String, KUSChatMessagesDataSource> getChatMessagesDataSources() {
        if (chatMessagesDataSources == null)
            chatMessagesDataSources = new HashMap<>();
        return chatMessagesDataSources;
    }
    //endregion

    //region Accessors

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    //endregion
}
