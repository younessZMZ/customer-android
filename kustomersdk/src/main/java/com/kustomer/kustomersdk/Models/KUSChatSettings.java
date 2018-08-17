package com.kustomer.kustomersdk.Models;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
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

    private String waitMessage;
    private String customWaitMessage;
    private Integer timeOut;
    private Integer promptDelay;
    private Boolean hideWaitOption;
    private ArrayList<String> followUpChannels;
    private Boolean useDynamicWaitMessage;
    private Boolean markDoneAfterTimeout;
    private Boolean volumeControlEnabled;
    private Boolean closableChat;
    private Boolean singleSessionChat;
    private Boolean noHistory;
    //endregion

    //region Initializer
    public KUSChatSettings(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        teamName = JsonHelper.stringFromKeyPath(json, "attributes.teamName");
        teamIconURL = JsonHelper.urlFromKeyPath(json, "attributes.teamIconUrl");
        greeting = JsonHelper.stringFromKeyPath(json, "attributes.greeting");
        autoReply = stringSanitizedReply(JsonHelper.stringFromKeyPath(json, "attributes.autoreply"));
        activeFormId = JsonHelper.stringFromKeyPath(json, "attributes.activeForm");
        pusherAccessKey = JsonHelper.stringFromKeyPath(json, "attributes.pusherAccessKey");
        enabled = JsonHelper.boolFromKeyPath(json, "attributes.enabled");

        closableChat = JsonHelper.boolFromKeyPath(json, "attributes.closableChat");
        waitMessage = JsonHelper.stringFromKeyPath(json, "attributes.waitMessage");
        singleSessionChat = JsonHelper.boolFromKeyPath(json, "attributes.singleSessionChat");
        noHistory = JsonHelper.boolFromKeyPath(json, "attributes.noHistory");

        customWaitMessage = JsonHelper.stringFromKeyPath(json, "attributes.volumeControl.customWaitMessage");
        timeOut = JsonHelper.integerFromKeyPath(json, "attributes.volumeControl.timeout");
        promptDelay = JsonHelper.integerFromKeyPath(json, "attributes.volumeControl.promptDelay");
        hideWaitOption = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.hideWaitOption");
        followUpChannels = JsonHelper.arrayListFromKeyPath(json, "attributes.volumeControl.followUpChannels");
        useDynamicWaitMessage = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.useDynamicWaitMessage");
        markDoneAfterTimeout = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.markDoneAfterTimeout");
        volumeControlEnabled = JsonHelper.boolFromKeyPath(json, "attributes.volumeControl.enabled");
    }

    @Override
    public String modelType() {
        return "chat_settings";
    }
    //endregion

    //region Private Methods
    private String stringSanitizedReply(String autoReply) {
        if (autoReply != null)
            return autoReply.trim().length() > 0 ? autoReply.trim() : null;
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

    public String getCustomWaitMessage() {
        return customWaitMessage;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getPromptDelay() {
        return promptDelay;
    }

    public boolean isHideWaitOption() {
        return hideWaitOption;
    }

    public ArrayList<String> getFollowUpChannels() {
        return followUpChannels;
    }

    public boolean isUseDynamicWaitMessage() {
        return useDynamicWaitMessage;
    }

    public boolean isMarkDoneAfterTimeout() {
        return markDoneAfterTimeout;
    }

    public boolean isVolumeControlEnabled() {
        return volumeControlEnabled;
    }

    public String getWaitMessage() {
        return waitMessage;
    }

    public Boolean getHideWaitOption() {
        return hideWaitOption;
    }

    public Boolean getUseDynamicWaitMessage() {
        return useDynamicWaitMessage;
    }

    public Boolean getMarkDoneAfterTimeout() {
        return markDoneAfterTimeout;
    }

    public Boolean getVolumeControlEnabled() {
        return volumeControlEnabled;
    }

    public Boolean getClosableChat() {
        return closableChat;
    }

    public Boolean getSingleSessionChat() {
        return singleSessionChat;
    }

    public Boolean getNoHistory() {
        return noHistory;
    }

    //endregion
}
