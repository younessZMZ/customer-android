package com.kustomer.kustomersdk.DataSources;

import android.graphics.Bitmap;

import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSChatMessageState;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSUpload;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSImageUploadListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatAttachment;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSForm;
import com.kustomer.kustomersdk.Models.KUSFormQuestion;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;
import com.kustomer.kustomersdk.Utils.KUSUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.kustomer.kustomersdk.Models.KUSChatMessage.KUSChatMessageSentByUser;

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

    private String firstOtherUserId;
    private ArrayList<String> otherUserIds;
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

    public URL getFirstUrl() {
        if (sessionId != null) {
            String endPoint = String.format(KUSConstants.URL.MESSAGES_LIST_ENDPOINT, sessionId);
            return getUserSession().getRequestManager().urlForEndpoint(endPoint);
        }
        return  null;
    }

    @Override
    public boolean isFetched() {
        return createdLocally || super.isFetched();
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


        JSONObject json = JsonHelper.stringToJson(jsonString);
        List<KUSModel> temporaryMessages = objectsFromJSON(json);

        fullySendMessage(temporaryMessages,attachments,text);
    }

    private void upsertNewMessages(List<KUSModel> chatMessages){
       if(chatMessages.size()>1)
            Collections.reverse(chatMessages);

       upsertAll(chatMessages);
    }
    //endregion

    //region Private Methods
    private void fullySendMessage(List<KUSModel> temporaryMessages,List<Bitmap> attachments, String text){
        //TODO: Incomplete
        insertMessagesWithState(KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENDING, temporaryMessages);
        sendMessage(attachments,temporaryMessages,text);
    }

    private void insertMessagesWithState(KUSChatMessageState state, List<KUSModel> temporaryMessages){
        removeAll(temporaryMessages);
        for(KUSModel message : temporaryMessages){
            if(message.getClass().equals(KUSChatMessage.class)){
                ((KUSChatMessage)message).setState(state);
            }
        }

        upsertNewMessages(temporaryMessages);
    }

    private void sendMessage(List<Bitmap> attachments, final List<KUSModel> temporaryMessages , final String text){

        //TODO: send attachmentIds
        KUSUpload.uploadImages(attachments, getUserSession(), new KUSImageUploadListener() {
            @Override
            public void onCompletion(Error error, List<KUSChatAttachment> attachments) {
                getUserSession().getRequestManager().performRequestType(
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
                        new KUSRequestCompletionListener() {
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

    @Override
    public List<KUSModel> objectsFromJSON(JSONObject jsonObject)
    {
        return JsonHelper.kusChatModelsFromJSON(jsonObject);
    }

    private void handleMessageSent(JSONObject response, List<KUSModel> temporaryMessages){
        List<KUSModel> finalMessages = objectsFromJSON(JsonHelper.jsonObjectFromKeyPath(response,"data"));
        //TODO: Incomplete
        if(finalMessages != null) {
            removeAll(temporaryMessages);
            upsertNewMessages(finalMessages);
        }
    }
    //endregion


    //region Callbacks
    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId) {
        //TODO: Not implemented
    }

    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {
        //TODO: Not implemented
    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {
        //TODO: Not implemented
    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {
        //TODO: Not implemented
    }
    //endregion

    //region Accessors

    public String getFirstOtherUserId() {

        for(KUSModel message : getList()){
            KUSChatMessage chatMessage = (KUSChatMessage) message;
            if(!KUSChatMessageSentByUser(chatMessage))
                return chatMessage.getSentById();
        }
        return firstOtherUserId;
    }

    public List<String> getOtherUserIds(){
        HashSet<String> userIdsSet = new HashSet<>();
        List<String> otherUserIds = new ArrayList<>();

        for(KUSModel message : getList()){
            KUSChatMessage kusChatMessage = (KUSChatMessage) message;
            if(!KUSChatMessageSentByUser(kusChatMessage)){
                String sentById = kusChatMessage.getSentById();
                if(sentById != null && !userIdsSet.contains(sentById)){
                    userIdsSet.add(sentById);
                    otherUserIds.add(sentById);
                }
            }
        }

        return otherUserIds;
    }

    public int unreadCountAfterDate(Date date){
        int count = 0;

        for(KUSModel model : getList()){
            KUSChatMessage message = (KUSChatMessage) model;

            if(KUSChatMessageSentByUser(message)){
                return count;
            }

            if(message.getCreatedAt() != null){
                if(message.getCreatedAt().before(date)){
                    return count;
                }

                count ++;
            }

        }

        return count;
    }

    //endregion
}
