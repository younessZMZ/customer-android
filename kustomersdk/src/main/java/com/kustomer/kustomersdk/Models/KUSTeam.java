package com.kustomer.kustomersdk.Models;

import android.content.Intent;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTeam extends KUSModel {

    //region Properties
    private String emoji;
    public String displayName;
    public String icon;
    //endregion

    //region LifeCycleMethods
    public KUSTeam(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        displayName = JsonHelper.stringFromKeyPath(json,"attributes.displayName");
        icon = JsonHelper.stringFromKeyPath(json, "attributes.icon");

        try {

            int emojiInt = Integer.decode(icon);
            ByteBuffer b = ByteBuffer.allocate(4);
            b.order(ByteOrder.LITTLE_ENDIAN);
            b.putInt(emojiInt);

            emoji = new String(b.array(),"UTF-32");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String fullDisplay(){
        if(emoji != null){
            return String.format("%s %s",emoji,displayName);
        }

        return displayName;
    }
    //endregion

    //region Class methods
    public String modelType(){
        return null;
    }
    public boolean enforcesModelType(){
        return false;
    }
    //endregion
}
