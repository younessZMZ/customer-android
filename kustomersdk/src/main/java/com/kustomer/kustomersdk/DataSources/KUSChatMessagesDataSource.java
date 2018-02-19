package com.kustomer.kustomersdk.DataSources;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSChatMessageState;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSAudio;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSUpload;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSChatSessionCompletionListener;
import com.kustomer.kustomersdk.Interfaces.KUSImageUploadListener;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatAttachment;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
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

public class KUSChatMessagesDataSource extends KUSPaginatedDataSource implements KUSChatMessagesDataSourceListener, KUSObjectDataSourceListener {

    //region Properties
    static final int KUS_CHAT_AUTO_REPLY = 2 * 1000;

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
    private ArrayList<onCreateSessionListener> onCreateSessionListeners;
    //endregion

    //region Initializer
    public KUSChatMessagesDataSource(KUSUserSession userSession) {
        super(userSession);
        delayedChatMessageIds = new HashSet<>();


        userSession.getChatSettingsDataSource().addListener(this);
        addListener(this);
    }

    public KUSChatMessagesDataSource(KUSUserSession userSession, boolean startNewConversation) {
        this(userSession);

        if(startNewConversation) {
            createdLocally = true;
            //TODO formDatasource

        }
    }

    public KUSChatMessagesDataSource(KUSUserSession userSession, String sessionId){
        this(userSession);

        if(sessionId.length() > 0)
            this.sessionId = sessionId;
    }


    //endregion

    //region Public Methods
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

    public void sendMessageWithText(String text, List<Bitmap> attachments){
        sendMessageWithText(text,attachments,null);
    }

    public void sendMessageWithText(String text, List<Bitmap> attachments, String value){
        //TODO: Incomplete

        actuallySendMessage(text,attachments);
    }

    public void createSessionIfNecessaryWithTitle(String title, final onCreateSessionListener listener){
        if(sessionId != null){
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                   listener.onComplete(true,null);
                }
            };
            mainHandler.post(myRunnable);
        }else{
            if(onCreateSessionListeners != null){
                if(listener != null)
                    onCreateSessionListeners.add(listener);
            }else{
                onCreateSessionListeners = new ArrayList<onCreateSessionListener>(){{add(listener);}};
                creatingSession = true;
                getUserSession().getChatSessionsDataSource().createSessionWithTitle(title, new KUSChatSessionCompletionListener() {
                    @Override
                    public void onComplete(Error error, KUSChatSession session) {
                        ArrayList<onCreateSessionListener> callbacks = new ArrayList<>(onCreateSessionListeners);
                        onCreateSessionListeners = null;

                        if(error != null || session == null){
                            //TODO: logError
                            for(onCreateSessionListener listener1 : callbacks)
                                listener1.onComplete(false,error);

                            return;
                        }

                        //Grab the sessionId
                        sessionId = session.getId();
                        creatingSession = false;

                        //Insert the current messages data source into the userSession's lookup table
                        getUserSession().getChatMessagesDataSources().put(session.getId(),KUSChatMessagesDataSource.this);

                        //Notify Listeners
                        for(KUSPaginatedDataSourceListener listener1: new ArrayList<>(listeners)){
                            KUSChatMessagesDataSourceListener chatListener = (KUSChatMessagesDataSourceListener) listener1;
                            chatListener.onCreateSessionId(KUSChatMessagesDataSource.this,session.getId());
                        }

                        for(onCreateSessionListener listener1 : callbacks)
                            listener1.onComplete(true,null);

                    }
                });
            }

        }
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
    private void fullySendMessage(final List<KUSModel> temporaryMessages, final List<Bitmap> attachments, final String text){
        insertMessagesWithState(KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENDING, temporaryMessages);

        createSessionIfNecessaryWithTitle(text, new onCreateSessionListener() {
            @Override
            public void onComplete(boolean success, Error error) {

                if(success)
                    sendMessage(attachments,temporaryMessages,text);
                else
                    insertMessagesWithState(KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_FAILED,temporaryMessages);

            }
        });
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

    private boolean shouldShowAutoReply(){

        KUSChatMessage firstMessage = null;
        if(getSize() > 0)
            firstMessage = (KUSChatMessage) getList().get(getSize()-1);

        KUSChatMessage secondMessage = getSize() >= 2 ? (KUSChatMessage) get(getSize() - 2) : null;
        KUSChatSettings chatSettings = (KUSChatSettings) getUserSession().getChatSettingsDataSource().getObject();

        if(firstMessage != null && chatSettings != null)
            return ((chatSettings.getActiveFormId() == null || chatSettings.getActiveFormId().length()==0 ||
                    (firstMessage.getImportedAt() == null && (secondMessage==null || secondMessage.getImportedAt() == null)))
                    && chatSettings.getAutoReply().length() > 0
                    && getSize() > 0
                    && isFetchedAll()
                    && (sessionId == null || sessionId.length() > 0)
                    && (firstMessage.getState() == null || firstMessage.getState() == KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENT)
            );
        else
            return true;
    }

    private void insertAutoReplyIfNecessary(){
        if(shouldShowAutoReply()){
            String autoreplyId = String.format("autoreply_%s",sessionId);
            // Early escape if we already have an autoreply
            if(findById(autoreplyId) != null)
                return;

            KUSChatMessage firstMessage = null;
            if(getSize() > 0)
                firstMessage = (KUSChatMessage) getList().get(getSize()-1);

            KUSChatSettings chatSettings = (KUSChatSettings) getUserSession().getChatSettingsDataSource().getObject();

            if(firstMessage != null && chatSettings != null) {
                Date createdAt = new Date(firstMessage.getCreatedAt().getTime() + KUS_CHAT_AUTO_REPLY);
                String jsonString = "{" +
                        "\"type\":\"chat_message\"," +
                        "\"id\":\"" + autoreplyId + "\"," +
                        "\"attributes\":{" +
                        "\"body\":\"" + chatSettings.getAutoReply() + "\"," +
                        "\"direction\":\"out\"," +
                        "\"createdAt\":\"" + KUSDate.stringFromDate(createdAt) + "\"" +
                        "}" +
                        "}";

                JSONObject json = JsonHelper.stringToJson(jsonString);
                try {
                    KUSChatMessage autoReplyMessage = new KUSChatMessage(json);
                    insertDelayedMessage(autoReplyMessage);
                } catch (KUSInvalidJsonException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void insertDelayedMessage(final KUSChatMessage chatMessage){

        //Sanity Check
        if(chatMessage.getId().length() == 0)
            return;

        //Only insert the message if it doesn't exist already
        if(findById(chatMessage.getId()) != null)
            return;

        long delay = chatMessage.getCreatedAt().getTime() - Calendar.getInstance().getTime().getTime();
        if(delay <= 0){
            upsertAll(new ArrayList<KUSModel>(){{add(chatMessage);}});
            return;
        }

        delayedChatMessageIds.add(chatMessage.getId());
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                delayedChatMessageIds.remove(chatMessage.getId());
                boolean doesNotAlreadyContainMessage = findById(chatMessage.getId()) == null ;
                upsertAll(new ArrayList<KUSModel>(){{add(chatMessage);}});
                if(doesNotAlreadyContainMessage)
                    KUSAudio.playMessageReceivedSound();
            }
        };
        mainHandler.postDelayed(myRunnable,delay);

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

    @Override
    public boolean isFetched() {
        return createdLocally || super.isFetched();
    }

    @Override
    public boolean isFetchedAll() {
        return createdLocally || super.isFetchedAll();
    }

    //endregion

    //region Listener
    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        //TODO

        insertAutoReplyIfNecessary();
    }

    @Override
    public void objectDataSourceOnError(final KUSObjectDataSource dataSource, Error error) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                dataSource.fetch();
            }
        };
        mainHandler.postDelayed(myRunnable,1000);

    }

    @Override
    public void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId) {
        insertAutoReplyIfNecessary();
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
        insertAutoReplyIfNecessary();
        //TODO:
    }

    //endregion

    //region Interface
    public interface onCreateSessionListener{
        void onComplete(boolean success, Error error);
    }
    //endregion
}
