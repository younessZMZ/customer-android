package com.kustomer.kustomersdk.DataSources;

import android.os.Handler;
import android.os.Looper;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Helpers.KUSLog;
import com.kustomer.kustomersdk.Interfaces.KUSChatSessionCompletionListener;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatSession;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.JsonHelper;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSessionsDataSource extends KUSPaginatedDataSource implements KUSPaginatedDataSourceListener {

    //region Properties
    private HashMap<String, Object> pendingCustomChatSessionAttributes;
    private HashMap<String, Date> localLastSeenAtBySessionId;
    //endregion

    //region Initializer
    public KUSChatSessionsDataSource(KUSUserSession userSession) {
        super(userSession);

        localLastSeenAtBySessionId = new HashMap<>();
        addListener(this);
    }

    @Override
    public List<KUSModel> objectsFromJSON(JSONObject jsonObject) {

        ArrayList<KUSModel> arrayList = null;

        KUSModel model = null;
        try {
            model = new KUSChatSession(jsonObject);
        } catch (KUSInvalidJsonException e) {
            e.printStackTrace();
        }

        if(model != null) {
            arrayList = new ArrayList<>();
            arrayList.add(model);
        }

        return arrayList;
    }
    //endregion

    //region KUSPaginatedDataSource methods
    public URL getFirstUrl(){
        return getUserSession().getRequestManager().urlForEndpoint(KUSConstants.URL.CHAT_SESSIONS_ENDPOINT);
    }
    //endregion

    //region Public Methods
    public void createSessionWithTitle(String title, final KUSChatSessionCompletionListener listener){
        HashMap <String,Object> params = new HashMap<>();
        params.put("title",title);

        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                KUSConstants.URL.CHAT_SESSIONS_ENDPOINT,
                params, true, new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if(error != null){
                            if(listener != null)
                                listener.onComplete(error,null);

                            return;
                        }

                        KUSChatSession session = null;
                        try {
                            session = new KUSChatSession(JsonHelper.jsonObjectFromKeyPath(response,"data"));
                        } catch (KUSInvalidJsonException e) {
                            e.printStackTrace();
                        }

                        if(session != null){
                            ArrayList<KUSModel> sessions = new ArrayList<>();
                            sessions.add(session);

                            upsertAll(sessions);
                        }

                        if(listener != null)
                            listener.onComplete(null,session);

                    }
                });
    }

    public void updateLastSeenAtForSessionId(String sessionId, final KUSChatSessionCompletionListener listener){
        if(sessionId.length() == 0){
            if(listener != null){
                Handler mainHandler = new Handler(Looper.getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete(new Error("Session id missing"),null);
                    }
                };
                mainHandler.post(myRunnable);
            }
        }

        Date lastSeenAtDate = Calendar.getInstance().getTime();;
        localLastSeenAtBySessionId.put(sessionId,lastSeenAtDate);
        final String lastSeenAtString = KUSDate.stringFromDate(lastSeenAtDate);

        String url = String.format(KUSConstants.URL.CHAT_SESSIONS_ENDPOINT + "/%s",sessionId);
        HashMap<String,Object> params = new HashMap<String,Object>(){{
            put("lastSeenAt",lastSeenAtString);
        }};

        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_PUT,
                url,
                params,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if(error != null && listener != null){
                            listener.onComplete(error,null);
                            return;
                        }

                        KUSChatSession session = null;
                        try {
                            session = new KUSChatSession(JsonHelper.jsonObjectFromKeyPath(response,"data"));
                        } catch (KUSInvalidJsonException e) {
                            e.printStackTrace();
                        }

                        if(session != null){
                            ArrayList<KUSModel> sessions = new ArrayList<>();
                            sessions.add(session);

                            upsertAll(sessions);
                        }

                        if(listener != null)
                            listener.onComplete(null,session);
                    }
                }

        );
    }

    public void submitFormMessages(){
        //TODO: incomplete
    }

    public void describeActiveConversation(HashMap<String,Object> customAttributes){
        KUSChatSession mostRecentChatSession = getMostRecentChatSession();
        String mostRecentChatSessionId = mostRecentChatSession.getId();

        if(mostRecentChatSessionId != null)
            flushCustomAttributes(customAttributes,mostRecentChatSessionId);
        else{
            // Merge previously queued custom attributes with the latest custom attributes
            HashMap<String,Object> pendingCustomChatSessionAttributes = new HashMap<>();

            if(this.pendingCustomChatSessionAttributes != null)
                pendingCustomChatSessionAttributes.putAll(this.pendingCustomChatSessionAttributes);
            pendingCustomChatSessionAttributes.putAll(customAttributes);
            this.pendingCustomChatSessionAttributes = pendingCustomChatSessionAttributes;

            fetchLatest();
        }
    }
    //endregion

    //region Private Methods
    private void flushCustomAttributes(final HashMap<String,Object> customAttributes, String chatSessionId){
        HashMap<String,Object>  formData = new HashMap<String,Object>(){{
            put("custom",customAttributes);
        }};

        String endpoint = String.format(KUSConstants.URL.CHAT_CONVERSATIONS_ENDPOINT + "/%s",chatSessionId);
        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_PATCH,
                endpoint,
                formData,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {
                        if(error != null){
                            //TODO: Log Error
                        }
                    }
                }
        );
    }
    //endregion

    //region Helper methods
    private KUSChatSession getMostRecentChatSession(){
        Date mostRecentMessageAt = null;
        KUSChatSession mostRecentChatSession = null;

        for(KUSModel model: getList()){
            KUSChatSession chatSession = (KUSChatSession) model;
            if(mostRecentMessageAt == null){
                mostRecentMessageAt = chatSession.getLastMessageAt();
                mostRecentChatSession = chatSession;
            }else if(mostRecentMessageAt.before(chatSession.getLastMessageAt())){
                    mostRecentMessageAt = chatSession.getLastMessageAt();
                    mostRecentChatSession = chatSession;
            }
        }

        return mostRecentChatSession;
    }

    public Date getLastMessageAt(){
        if(getMostRecentChatSession() != null)
            return getMostRecentChatSession().getLastMessageAt();
        return null;
    }

    public Date lastSeenAtForSessionId(String sessionId){
        KUSChatSession chatSession = (KUSChatSession) findById(sessionId);
        Date chatSessionDate = chatSession.getLastSeenAt();
        Date localDate = localLastSeenAtBySessionId.get(sessionId);

        if(chatSessionDate != null){
            if(localDate != null)
                return chatSessionDate.after(localDate) ? chatSessionDate : localDate;
            else
                return chatSessionDate;
        }
        else
            return localDate;
    }

    private int totalUnreadCountExcludingSessionId(String excludedSessionId){
        int count = 0;
        for(KUSModel model : getList()){
            KUSChatSession session = (KUSChatSession) model;

            String sessionId = session.getId();
            if(excludedSessionId != null && excludedSessionId.equals(sessionId)){
                continue;
            }

            KUSChatMessagesDataSource messagesDataSource = getUserSession().chatMessageDataSourceForSessionId(sessionId);
            Date sessionLastSeenAt = lastSeenAtForSessionId(sessionId);
            int unreadCountForSession = messagesDataSource.unreadCountAfterDate(sessionLastSeenAt);
            count += unreadCountForSession;
        }

        return count;
    }
    //endregion

    //region Listener
    @Override
    public void onLoad(KUSPaginatedDataSource dataSource) {

    }

    @Override
    public void onError(KUSPaginatedDataSource dataSource, Error error) {

    }

    @Override
    public void onContentChange(KUSPaginatedDataSource dataSource) {
        if(dataSource == this){
            if(pendingCustomChatSessionAttributes != null){
                KUSChatSession mostRecentChatSession = getMostRecentChatSession();
                String mostRecentChatSessionId = mostRecentChatSession.getId();
                if(mostRecentChatSessionId != null){
                    flushCustomAttributes(pendingCustomChatSessionAttributes,mostRecentChatSessionId);
                    pendingCustomChatSessionAttributes = null;
                }
            }

            for(KUSModel model : getList()){
                KUSChatSession chatSession = (KUSChatSession) model;
                KUSChatMessagesDataSource messagesDataSource = getUserSession()
                        .chatMessageDataSourceForSessionId(chatSession.getId());
                messagesDataSource.addListener(this);
            }

        }else if(dataSource.getClass().equals(KUSChatMessagesDataSource.class)){
            sort();
            notifyAnnouncersOnContentChange();
        }
    }
    //endregion
}
