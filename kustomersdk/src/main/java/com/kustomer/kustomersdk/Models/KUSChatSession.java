package com.kustomer.kustomersdk.Models;

import com.google.gson.annotations.SerializedName;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;

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
    public String preview;
    public String trackingId;

    public Date createdAt;
    public Date lastSeenAt;
    public Date lastMessageAt;
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
        return String.format("<%s : oid: %s; preview: %s>",this.getClass(),this.oid,this.preview);
    }


    @Override
    public boolean equals(Object obj) {
        if(!obj.getClass().equals(KUSChatSession.class))
            return false;

        KUSChatSession chatSession = (KUSChatSession)obj;

        if(!chatSession.oid.equals(this.oid))
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

    @Override
    public String modelType() {
        return "chat_session";
    }
    //endregion
}
