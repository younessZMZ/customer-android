package com.kustomer.kustomersdk.Models;

import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class KUSMLFormValue extends KUSModel {

    //region Properties
    private String displayName;
    private Boolean lastNodeRequired;
    private ArrayList<KUSMLNode> mlNodes;
    //endregion

    //region Initializer
    KUSMLFormValue(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        displayName = JsonHelper.stringFromKeyPath(json,"displayName");
        lastNodeRequired = JsonHelper.boolFromKeyPath(json,"lastNodeRequired");
        mlNodes = KUSMLNode.objectsFromJSONs(JsonHelper.arrayFromKeyPath(json,"mlNodes.children"));
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

    //region Accessors

    public String getDisplayName() {
        return displayName;
    }

    public Boolean getLastNodeRequired() {
        return lastNodeRequired != null ? lastNodeRequired : false;
    }

    public ArrayList<KUSMLNode> getMlNodes() {
        return mlNodes;
    }

    //endregion
}
