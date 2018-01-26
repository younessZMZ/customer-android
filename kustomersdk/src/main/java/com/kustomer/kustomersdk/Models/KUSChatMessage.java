package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;

import com.kustomer.kustomersdk.Enums.KUSChatMessageDirection;
import com.kustomer.kustomersdk.Enums.KUSChatMessageState;
import com.kustomer.kustomersdk.Enums.KUSChatMessageType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static com.kustomer.kustomersdk.Utils.JsonHelper.arrayListFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.dateFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */



public class KUSChatMessage extends KUSModel {

    //region Properties
    private String trackingId;
    public String body;
    private URL imageUrl;
    private ArrayList attachmentIds;

    private Date createdAt;
    private Date importedAt;
    private KUSChatMessageDirection direction;
    private String sentById;

    public KUSChatMessageType type;
    public KUSChatMessageState state;
    public String value;
    //endregion

    public KUSChatMessage(){

    }

    private KUSChatMessage(JSONObject json) throws KUSInvalidJsonException {
        this(json, KUSChatMessageType.KUS_CHAT_MESSAGE_TYPE_TEXT, null);
    }

    private KUSChatMessage(JSONObject json, KUSChatMessageType type, URL imageUrl) throws KUSInvalidJsonException {
        super(json);

        trackingId = stringFromKeyPath(json,"attributes.trackingId");
        body = stringFromKeyPath(json,"attributes.body");
        this.type = type;
        this.imageUrl = imageUrl;
        this.attachmentIds = arrayListFromKeyPath(json,"relationships.attachments.data.@unionOfObjects.id");

        this.createdAt = dateFromKeyPath(json,"attributes.createdAt");
        this.importedAt = dateFromKeyPath(json,"attributes.importedAt");
        this.direction = KUSChatMessageDirectionFromString(stringFromKeyPath(json,"attributes.direction"));
        this.sentById = stringFromKeyPath(json, "relationships.sentBy.data.id");
    }


    public static boolean KUSChatMessageSentByUser(KUSChatMessage message) {
        return message.direction == KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_IN;
    }

    public static boolean KUSMessagesSameSender(KUSChatMessage message1, KUSChatMessage message2) {
        return message1 != null
                && message2 != null
                && message1.direction == message2.direction
                && message1.sentById.equalsIgnoreCase(message2.sentById);
    }

    public static KUSChatMessageDirection KUSChatMessageDirectionFromString(String str) {
        return str.equalsIgnoreCase("in")
                ? KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_IN
                : KUSChatMessageDirection.KUS_CHAT_MESSAGE_DIRECTION_OUT;
    }

    public static String KUSUnescapeBackslashesFromString (String string){
        String mutableString = "";

        int startingIndex = 0;
        for(int i = 0; i<string.length(); i++){
            String character = string.substring(i,i+1);
            if(character.equals("\\")){
                String lastString = string.substring(startingIndex, i);
                mutableString = mutableString.concat(lastString);

                i++;
                startingIndex = i;
            }
        }

        String endingString = string.substring(startingIndex);
        mutableString = mutableString.concat(endingString);

        return mutableString;
    }

    @Override
    public String modelType(){
        return "chat_message";
    }

    public static URL attachmentURLForMessageId(String messageId, String attachmentId){
        //Not Implemented yet
        return null;
    }

    public List<KUSModel> objectsWithJSON(JSONObject jsonObject)
    {

        if(jsonObject == null)
            return null;

        KUSChatMessage standardChatMessage = null;

        try {
            standardChatMessage = new KUSChatMessage(jsonObject);
        } catch (KUSInvalidJsonException e) {
            e.printStackTrace();
        }

        if(standardChatMessage == null)
            return new ArrayList<>();

        String body = KUSUnescapeBackslashesFromString(standardChatMessage.body);
        standardChatMessage.body = body;

        //The markdown url pattern we want to detect
        String imagePattern = "!\\[.*\\]\\(.*\\)";
        List<KUSModel> chatMessages = new ArrayList<>();

        Pattern regex = Pattern.compile(imagePattern);


        // TODO: Incomplete

        chatMessages.add(standardChatMessage);

        return chatMessages;
    }

    @Override
    public String toString() {
        //Missing %p (this)
        return String.format("<%s : oid: %s; body: %s>",this.getClass(),this.oid,this.body);
    }

    @Override
    public boolean equals(Object obj) {

        if(obj == this)
            return true;
        if(!obj.getClass().equals(this.getClass()))
            return false;

        KUSChatMessage chatMessage = (KUSChatMessage) obj;

        if(chatMessage.state != this.state)
            return false;
        if(chatMessage.direction != this.direction)
            return false;
        if(chatMessage.type != this.type)
            return false;
        if(chatMessage.attachmentIds != null && this.attachmentIds!=null && !chatMessage.attachmentIds.equals(this.attachmentIds))
            return false;
        if(chatMessage.attachmentIds == null || this.attachmentIds == null)
            return false;
        if(!chatMessage.oid.equals(this.oid))
            return false;
        if(!chatMessage.createdAt.equals(this.createdAt))
            return false;
        if(!chatMessage.importedAt.equals(this.importedAt))
            return false;
        if(!chatMessage.body.equals(this.body))
            return false;

        return true;
    }

    @Override
    public int compareTo(@NonNull KUSModel kusModel) {
        KUSChatMessage message = (KUSChatMessage) kusModel;
        int date = message.createdAt.compareTo(this.createdAt);
        int parent = super.compareTo(kusModel);
        return date == 0 ? parent : date;
    }
}
