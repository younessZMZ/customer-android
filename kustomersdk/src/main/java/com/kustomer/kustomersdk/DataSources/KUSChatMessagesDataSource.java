package com.kustomer.kustomersdk.DataSources;

import android.graphics.Bitmap;

import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSChatMessageState;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSUpload;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSImageUploadListener;
import com.kustomer.kustomersdk.Models.KUSChatAttachment;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSForm;
import com.kustomer.kustomersdk.Models.KUSFormQuestion;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatMessagesDataSource extends KUSPaginatedDataSource implements KUSChatMessagesDataSourceListener {

    //region Properties
    private String sessionId;
    private boolean createdLocally;
    private KUSForm form;
    private Set<String> delayedChatMessageIds;
    private int questionIndex;
    private KUSFormQuestion questions;
    private boolean submittingForm;
    private boolean creatingSession;
    //endregion

    //region Initializer
    public KUSChatMessagesDataSource(KUSUserSession userSession) {
        super(userSession);
        delayedChatMessageIds = new HashSet<>();


        addListener(this);
    }
    //endregion

    //region Public Methods
    public KUSChatMessagesDataSource(KUSUserSession userSession, String sessionId){
        this(userSession);

        if(sessionId.length() > 0)
            this.sessionId = sessionId;
    }

    public void addListener(KUSChatMessagesDataSourceListener listener) {
        super.addListener(listener);
    }

    public URL firstUrl() {
        if (sessionId != null) {
            String endPoint = String.format(KUSConstants.URL.MESSAGES_LIST_ENDPOINT, sessionId);
            return userSession.getRequestManager().urlForEndpoint(endPoint);
        }
        return  null;
    }

    public KUSModel modelClass() {
        return new KUSChatMessage();
    }

    @Override
    public boolean isFetched() {
        return createdLocally || super.fetched;
    }

    @Override
    public boolean isFetchedAll(){
        return fetchedAll;
    }

    @Override
    public boolean isFetching(){
        return fetching;
    }


    public void sendMessageWithText(String text, List<Bitmap> attachments){
        sendMessageWithText(text,attachments,null);
    }

    public void sendMessageWithText(String text, List<Bitmap> attachments, String value){
        //TODO: Incomplete

        actuallySendMessage(text,attachments);
    }

    public void actuallySendMessage(String text, List<Bitmap> attachments){
        //TODO: incomplete

        String jsonString = "{" +
                "\"type\":\"chat_message\"," +
                "\"id\":\"\"," +
                "\"attributes\":{" +
                            "\"body\":\"" + text + "\"," +
                            "\"direction\":\"in\"," +
                            "\"createdAt\":\"" + KUSDate.stringFromDate(Calendar.getInstance().getTime()) + "\""+
                            "}," +
                "\"relationships\":{" +
                                "\"attachments\":{" +
                                                "\"data\": null" +
                                                "}" +
                                "}" +
                "}";


        JSONObject json = JsonHelper.jsonFromString(jsonString);
        List<KUSModel> temporaryMessages = new KUSChatMessage().objectsWithJSON(json);

        fullySendMessage(temporaryMessages,attachments,text);
    }

    private void upsertNewMessages(List<KUSModel> chatMessages){
       if(chatMessages.size()>1)
            Collections.reverse(chatMessages);

       upsertObjects(chatMessages);
    }
    //endregion

    //region Private Methods
    private void fullySendMessage(List<KUSModel> temporaryMessages,List<Bitmap> attachments, String text){

        //TODO: Incomplete
        insertMessagesWithState(KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENDING, temporaryMessages);
        sendMessage(attachments,temporaryMessages,text);
    }

    private void insertMessagesWithState(KUSChatMessageState state, List<KUSModel> temporaryMessages){
        removeObjects(temporaryMessages);
        for(KUSModel message : temporaryMessages){
            if(message.getClass().equals(KUSChatMessage.class)){
                ((KUSChatMessage)message).state = state;
            }
        }

        upsertNewMessages(temporaryMessages);
    }

    private void sendMessage(List<Bitmap> attachments, final List<KUSModel> temporaryMessages , final String text){

        //TODO: send attachmentIds
        KUSUpload.uploadImages(attachments, userSession, new KUSImageUploadListener() {
            @Override
            public void onCompletion(Error error, List<KUSChatAttachment> attachments) {
                userSession.getRequestManager().performRequestType(
                        KUSRequestType.KUS_REQUEST_TYPE_POST,
                        KUSConstants.URL.SEND_MESSAGE_ENDPOINT,
                        new HashMap<String, Object>() {
                            {
                                put("body", text);
                                put("session",sessionId);
                                put("attachments", null);
                            }
                        },
                        true,
                        new KUSRequestManager.KUSRequestCompletionListener() {
                            @Override
                            public void onCompletion(Error error, JSONObject response) {
                                if(error != null){
                                    insertMessagesWithState(KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_FAILED,temporaryMessages);
                                }

                                handleMessageSent(response, temporaryMessages);
                            }
                        });
            }
        });
    }

    private void handleMessageSent(JSONObject response, List<KUSModel> temporaryMessages){
        List<KUSModel> finalMessages = new KUSChatMessage().objectsWithJSON(JsonHelper.jsonObjectFromString(response,"data"));

        //TODO: Incomplete

        if(finalMessages != null) {
            removeObjects(temporaryMessages);
            upsertNewMessages(finalMessages);
        }
    }
    //endregion


    //region Callbacks
    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId) {

    }

    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {

    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {

    }
    //endregion
}
