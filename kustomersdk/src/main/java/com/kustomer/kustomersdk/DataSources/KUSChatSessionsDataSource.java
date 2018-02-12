package com.kustomer.kustomersdk.DataSources;

import android.os.Handler;
import android.os.Looper;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
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
import java.util.Date;
import java.util.HashMap;

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
    //endregion

    //region KUSPaginatedDataSource methods
    public URL getFirstUrl(){
        return getUserSession().getRequestManager().urlForEndpoint(KUSConstants.URL.CHAT_SESSION_LIST_ENDPOINT);
    }
    //endregion

    //region Public Methods
    public void createSessionWithTitle(String title, final KUSChatSessionCompletionListener listener){
        HashMap <String,Object> params = new HashMap<>();
        params.put("title",title);

        getUserSession().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_POST,
                KUSConstants.URL.CHAT_SESSION_LIST_ENDPOINT,
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

    }
    //endregion
}
