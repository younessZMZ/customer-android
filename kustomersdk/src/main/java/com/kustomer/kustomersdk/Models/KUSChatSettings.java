package com.kustomer.kustomersdk.Models;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSettings extends KUSModel implements Serializable {
    //region Properties
    private String teamName;
    private URL teamIconURL;
    private String greeting;
    private String autoReply;
    private String activeFormId;
    private String pusherAccessKey;
    private Boolean enabled;
    //endregion

    //region Initializer
    public KUSChatSettings(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        teamName = JsonHelper.stringFromKeyPath(json,"attributes.teamName");
        teamIconURL = JsonHelper.urlFromKeyPath(json,"attributes.teamIconUrl");
        greeting = JsonHelper.stringFromKeyPath(json,"attributes.greeting");
        autoReply = stringSanitizedReply(JsonHelper.stringFromKeyPath(json,"attributes.autoreply"));
        activeFormId = JsonHelper.stringFromKeyPath(json,"attributes.activeForm");
        pusherAccessKey = JsonHelper.stringFromKeyPath(json,"attributes.pusherAccessKey");
        enabled = JsonHelper.boolFromKeyPath(json,"attributes.enabled");
    }



    @Override
    public String modelType(){
        return "chat_settings";
    }
    //endregion

    //region Private Methods
    private String stringSanitizedReply(String autoReply){
        if(autoReply != null)
            return autoReply.trim();
        else
            return null;
    }
    //endregion

    //region Accessors

    public String getTeamName() {
        return teamName;
    }

    public URL getTeamIconURL() {
        return teamIconURL;
    }

    public String getGreeting() {
        return greeting;
    }

    public String getAutoReply() {
        return autoReply;
    }

    public String getActiveFormId() {
        return activeFormId;
    }

    public String getPusherAccessKey() {
        return pusherAccessKey;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    //endregion
}
