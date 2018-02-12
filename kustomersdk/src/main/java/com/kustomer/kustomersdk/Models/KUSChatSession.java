package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Kustomer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import static com.kustomer.kustomersdk.Utils.JsonHelper.dateFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSession extends KUSModel implements Serializable {

    //region Properties
    private String preview;
    private String trackingId;

    private Date createdAt;
    private Date lastSeenAt;
    private Date lastMessageAt;
    //endregion

    //region Initializer
    public KUSChatSession(){}

    public KUSChatSession(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        preview = stringFromKeyPath(json, "attributes.preview");
        trackingId = stringFromKeyPath(json, "attributes.trackingId");

        createdAt = dateFromKeyPath(json, "attributes.createdAt");
        lastSeenAt = dateFromKeyPath(json, "attributes.lastSeenAt");
        lastMessageAt = dateFromKeyPath(json, "attributes.lastMessageAt");
    }
    //endregion

    //region Public Methods
    @Override
    public String toString() {
        //Missing %p (this)
        return String.format("<%s : oid: %s; preview: %s>",this.getClass(),this.getId(),this.preview);
    }

    @Override
    public boolean equals(Object obj) {
        if(!obj.getClass().equals(KUSChatSession.class))
            return false;

        KUSChatSession chatSession = (KUSChatSession)obj;

        if(!chatSession.getId().equals(this.getId()))
            return false;
        if (this.preview != null && chatSession.preview != null && !chatSession.preview.equals(this.preview))
            return false;
        if (this.preview != null || chatSession.preview != null)
            return false;
        if(!chatSession.lastSeenAt.equals(this.lastSeenAt))
            return false;
        if(!chatSession.lastMessageAt.equals(this.lastMessageAt))
            return false;
        if(!chatSession.createdAt.equals(this.createdAt))
            return false;



        return true;
    }

    private Date sortDate(){
        KUSUserSession userSession = Kustomer.getSharedInstance().getUserSession();
        KUSChatMessagesDataSource messagesDataSource = userSession.chatMessageDataSourceForSessionId(getId());
        KUSChatMessage chatMessage = null;

        if(messagesDataSource != null && messagesDataSource.getSize()>0)
            chatMessage = (KUSChatMessage) messagesDataSource.get(0);

        Date laterLastMessageAt = null;

        if(chatMessage != null && chatMessage.getCreatedAt() != null) {
            laterLastMessageAt = chatMessage.getCreatedAt().after(lastMessageAt) ?
                    chatMessage.getCreatedAt() : lastMessageAt;
        }
        else
            laterLastMessageAt = lastMessageAt;

        return laterLastMessageAt != null ? laterLastMessageAt : createdAt;
    }

    @Override
    public int compareTo(@NonNull KUSModel kusModel) {
        KUSChatSession chatSession = (KUSChatSession) kusModel;
        int date = chatSession.sortDate().compareTo(this.sortDate());
        int parent = super.compareTo(kusModel);
        return date == 0 ? parent : date;
    }

    @Override
    public String modelType() {
        return "chat_session";
    }
    //endregion

    //region Accessors
    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Date lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Date getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(Date lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
    //endregion
}
