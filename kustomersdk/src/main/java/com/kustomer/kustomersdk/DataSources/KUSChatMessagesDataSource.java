package com.kustomer.kustomersdk.DataSources;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSChatMessageState;
import com.kustomer.kustomersdk.Enums.KUSFormQuestionType;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSAudio;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSUpload;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSChatSessionCompletionListener;
import com.kustomer.kustomersdk.Interfaces.KUSFormCompletionListener;
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

import org.json.JSONException;
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

import static com.kustomer.kustomersdk.Models.KUSChatMessage.KUSChatMessageSentByUser;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatMessagesDataSource extends KUSPaginatedDataSource implements KUSChatMessagesDataSourceListener, KUSObjectDataSourceListener {

    //region Properties
    private static final int KUS_CHAT_AUTO_REPLY_DELAY = 2 * 1000;

    private String sessionId;
    private boolean createdLocally;
    private KUSForm form;
    private Set<String> delayedChatMessageIds;
    private int questionIndex;
    private KUSFormQuestion formQuestion;
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
            userSession.getFormDataSource().addListener(this);
            if(!userSession.getFormDataSource().isFetched())
                userSession.getFormDataSource().fetch();

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

    public int unreadCountAfterDate(Date date){
        int count = 0;

        for(KUSModel model : getList()){
            KUSChatMessage message = (KUSChatMessage) model;

            if(KUSChatMessageSentByUser(message)){
                return count;
            }

            if(message.getCreatedAt() != null){
                if(date != null && message.getCreatedAt().before(date)){
                    return count;
                }

                count ++;
            }

        }

        return count;
    }

    public boolean shouldPreventSendingMessage(){
        // If we haven't loaded the chat settings data source, prevent input
        if (!getUserSession().getChatSettingsDataSource().isFetched()) {
            return true;
        }

        // If we are about to insert an artificial message, prevent input
        if (delayedChatMessageIds.size() > 0) {
            return true;
        }

        // When submitting the form or creating session, prevent sending more responses
        if (submittingForm || creatingSession) {
            return true;
        }

        // If the user sent their first message and it is not yet sent, prevent input

        if(getList().size() > 0) {
            KUSChatMessage firstMessage = (KUSChatMessage) getList().get(getList().size() - 1);
            if(sessionId == null
                    && getSize() == 1
                    && firstMessage.getState() != KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_SENT)
                return true;
        }

        return false;
    }

    public KUSFormQuestion currentQuestion(){
        if(sessionId != null)
            return null;

        KUSChatMessage latestMessage = getSize() > 0 ? (KUSChatMessage) get(0) : null;
        if(KUSChatMessageSentByUser(latestMessage))
            return null;

        return formQuestion;
    }
    //endregion

    //region Private Methods
    private void upsertNewMessages(List<KUSModel> chatMessages){
        if(chatMessages.size()>1)
            Collections.reverse(chatMessages);

        upsertAll(chatMessages);
    }

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
                Date createdAt = new Date(firstMessage.getCreatedAt().getTime() + KUS_CHAT_AUTO_REPLY_DELAY);
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

    private void insertFormMessageIfNecessary(){
        KUSChatSettings chatSettings = (KUSChatSettings) getUserSession().getChatSettingsDataSource().getObject();
        if(chatSettings != null && chatSettings.getActiveFormId() == null)
            return;

        if(getSize() == 0)
            return;

        if(form == null)
            return;

        // Make sure we submit the form if we just inserted a non-response question
        if(!submittingForm && ! KUSFormQuestion.KUSFormQuestionRequiresResponse(formQuestion)
                && questionIndex == form.getQuestions().size() -1)
            submitFormResponses();

        KUSChatMessage lastMessage = (KUSChatMessage) get(0);
        if(!KUSChatMessageSentByUser(lastMessage))
            return;

        if(shouldPreventSendingMessage())
            return;

        long additionalInsertDelay = 0;
        int latestQuestionIndex = questionIndex;
        int startingOffset = formQuestion != null ? 1 : 0;
        for(int i = questionIndex + startingOffset; i > form.getQuestions().size(); i++){
            KUSFormQuestion question = form.getQuestions().get(i);
            if(question.getType() == KUSFormQuestionType.KUS_FORM_QUESTION_TYPE_UNKNOWN)
                continue;

            Date createdAt = new Date(lastMessage.getCreatedAt().getTime()
                    + KUS_CHAT_AUTO_REPLY_DELAY + additionalInsertDelay);

            String questionId = String.format("question_%s",question.getId());
            String jsonString = "{" +
                    "\"type\":\"chat_message\"," +
                    "\"id\":\"" + questionId + "\"," +
                    "\"attributes\":{" +
                    "\"body\":\"" + question.getPrompt() + "\"," +
                    "\"direction\":\"out\"," +
                    "\"createdAt\":\"" + KUSDate.stringFromDate(createdAt) + "\"" +
                    "}" +
                    "}";

            JSONObject json = JsonHelper.stringToJson(jsonString);

            try {
                KUSChatMessage formMessage = new KUSChatMessage(json);
                insertDelayedMessage(formMessage);
                additionalInsertDelay += KUS_CHAT_AUTO_REPLY_DELAY;

            } catch (KUSInvalidJsonException e) {
                e.printStackTrace();
            }

            latestQuestionIndex = i;
            if(KUSFormQuestion.KUSFormQuestionRequiresResponse(question))
                break;
        }

        if(latestQuestionIndex == questionIndex)
            submitFormResponses();
        else
            questionIndex = latestQuestionIndex;
        formQuestion = form.getQuestions().get(questionIndex);
    }

    private void submitFormResponses(){
        List<JSONObject> messagesJSON = new ArrayList<>();

        int currentMessageIndex = getSize() - 1;
        KUSChatMessage firstUserMessage = (KUSChatMessage) get(currentMessageIndex);
        currentMessageIndex --;

        JSONObject message = new JSONObject();
        try {
            message.put("input",firstUserMessage.getBody());
            message.put("inputAt",KUSDate.stringFromDate(firstUserMessage.getCreatedAt()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        messagesJSON.add(message);

        for(KUSFormQuestion question : form.getQuestions()){
            KUSChatMessage questionMessage = (KUSChatMessage) get(currentMessageIndex);
            currentMessageIndex --;


            JSONObject formMessage = new JSONObject();

            try {
                formMessage.put("id",question.getId());
                formMessage.put("prompt",question.getPrompt());
                formMessage.put("promptAt",KUSDate.stringFromDate(questionMessage.getCreatedAt()));


                if(KUSFormQuestion.KUSFormQuestionRequiresResponse(question)){

                    KUSChatMessage responseMessage = (KUSChatMessage) get(currentMessageIndex);
                    currentMessageIndex--;

                    formMessage.put("input",responseMessage.getBody());
                    formMessage.put("inputAt",KUSDate.stringFromDate(responseMessage.getCreatedAt()));
                    if(responseMessage.getValue() != null)
                        formMessage.put("value",responseMessage.getValue());
                }
            }
            catch (JSONException ignore) {}

            messagesJSON.add(formMessage);
        }

        submittingForm = true;
        KUSChatMessage lastUserChatMessage = null;
        for(KUSModel model : getList()){
            KUSChatMessage chatMessage = (KUSChatMessage) model;
            if(KUSChatMessageSentByUser(chatMessage)){
                lastUserChatMessage = chatMessage;
                break;
            }
        }

        //TODO: retrySubmitting form

        actuallySubmitForm(messagesJSON,form.getId(),lastUserChatMessage);
    }

    private void actuallySubmitForm(final List<JSONObject> messagesJSON, String formId,
                                    final KUSChatMessage lastUserChatMessage){
        getUserSession().getChatSessionsDataSource().submitFormMessages(
                messagesJSON,
                formId,
                new KUSFormCompletionListener() {
                    @Override
                    public void onComplete(Error error, KUSChatSession chatSession, List<KUSModel> chatMessages) {

                        if(error != null){
                            handleError(lastUserChatMessage);
                            return;
                        }

                        // If the form contained an email prompt, mark the local session as having submitted email
                        if(form.containsEmailQuestion())
                            getUserSession().getSharedPreferences().setDidCaptureEmail(true);

                        // Grab the session id
                        sessionId = chatSession.getId();
                        form = null;
                        questionIndex = 0;
                        formQuestion = null;
                        submittingForm = false;

                        //Replace all of the local messages with the new ones
                        removeAll(getList());
                        upsertNewMessages(chatMessages);
                        //TODO: retry

                        // Insert the current messages data source into the userSession's lookup table
                        getUserSession().getChatMessagesDataSources().put(sessionId, KUSChatMessagesDataSource.this);

                        //Notify Listeners
                        for(KUSPaginatedDataSourceListener listener1: new ArrayList<>(listeners)){
                            KUSChatMessagesDataSourceListener chatListener = (KUSChatMessagesDataSourceListener) listener1;
                            chatListener.onCreateSessionId(KUSChatMessagesDataSource.this,sessionId);
                        }

                    }
                }

        );
    }

    private void handleError(final KUSChatMessage lastUserChatMessage){
        if(lastUserChatMessage != null){
            removeAll(new ArrayList<KUSModel>(){{add(lastUserChatMessage);}});
            lastUserChatMessage.setState(KUSChatMessageState.KUS_CHAT_MESSAGE_STATE_FAILED);
            upsertAll(new ArrayList<KUSModel>(){{add(lastUserChatMessage);}});
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
        return null;
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

        if(form == null && dataSource.getClass().equals(KUSFormDataSource.class))
            form = (KUSForm) dataSource.getObject();

        insertAutoReplyIfNecessary();
        insertFormMessageIfNecessary();
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

    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {
    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {
        insertAutoReplyIfNecessary();
        insertFormMessageIfNecessary();
    }

    //endregion

    //region Interface
    public interface onCreateSessionListener{
        void onComplete(boolean success, Error error);
    }
    //endregion
}
