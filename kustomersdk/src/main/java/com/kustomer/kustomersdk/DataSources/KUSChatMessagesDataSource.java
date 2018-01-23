package com.kustomer.kustomersdk.DataSources;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Interfaces.KUSChatMessagesDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Models.KUSChatMessage;
import com.kustomer.kustomersdk.Models.KUSForm;
import com.kustomer.kustomersdk.Models.KUSFormQuestion;
import com.kustomer.kustomersdk.Models.KUSModel;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatMessagesDataSource extends KUSPaginatedDataSource implements KUSChatMessagesDataSourceListener {

    private String sessionId;
    private boolean createdLocally;
    private KUSForm form;
    private Set<String> delayedChatMessageIds;
    private int questionIndex;
    private KUSFormQuestion questions;
    private boolean submittingForm;
    private boolean creatingSession;


    public KUSChatMessagesDataSource(KUSUserSession userSession) {
        super(userSession);
        delayedChatMessageIds = new HashSet<>();


        addListener(this);
    }


    public KUSChatMessagesDataSource(KUSUserSession userSession, String sessionId){
        this(userSession);
        assert sessionId.length() > 0;

        this.sessionId = sessionId;
    }

    public void addListener(KUSChatMessagesDataSourceListener listener) {
        super.addListener(listener);
    }

    public URL firstUrl() {
        if (sessionId != null) {
            String endPoint = String.format("/c/v1/chat/sessions/%s/messages", sessionId);
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


    //region Public Methods

    //endregion


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
}
