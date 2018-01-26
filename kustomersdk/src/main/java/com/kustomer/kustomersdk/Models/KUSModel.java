package com.kustomer.kustomersdk.Models;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.kustomer.kustomersdk.Helpers.KUSDate;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSModel implements Comparable<KUSModel>, Serializable {

    //region Properties
    public String oid;
    public String orgId;
    public String customerId;
    public String sessionId;
    //endregion

    //region Initializer
    public KUSModel(JSONObject json) throws KUSInvalidJsonException {
        //Reject any objects  where the model type doesn't match, if enforced
        String type = stringFromKeyPath(json,"type");
        String classType = modelType();

        if (enforcesModelType() && type != null && !type.equals(classType))
            throw new KUSInvalidJsonException("Model Type not matched.");

        //Make sure there is an object id
        String objectId = stringFromKeyPath(json,"id");
        if (objectId == null)
            throw new KUSInvalidJsonException("Object Id not found.");

        oid = objectId;

        this.orgId = stringFromKeyPath(json, "relationships.org.data.id");
        this.customerId = stringFromKeyPath(json, "relationships.customer.data.id");
        this.sessionId = stringFromKeyPath(json, "relationships.session.data.id");
    }
    //endregion

    //region Methods
    public String modelType() {
        return null;
    }

    protected boolean enforcesModelType() {
        return true;
    }

    public KUSModel(){

    }

    public List<KUSModel> objectsWithJSON(JSONObject jsonObject) {

        ArrayList<KUSModel> arrayList = null;

        KUSModel model = null;
        try {
            model = new KUSModel(jsonObject);
        } catch (KUSInvalidJsonException e) {
            e.printStackTrace();
        }

        if(model != null) {
            arrayList = new ArrayList<>();
            arrayList.add(model);
        }

        return arrayList;
    }

    public ArrayList<KUSModel> objectsWithJSONs(JSONArray jsonArray) {

        ArrayList<KUSModel> objects = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                KUSModel object = new KUSModel(jsonObject);
                objects.add(object);
            } catch (JSONException | KUSInvalidJsonException e) {
                e.printStackTrace();
            }
        }
        return objects;
    }

    @Override
    public String toString() {
        //Missing %p (this)
        return String.format("<%s : oid: %s>", this.getClass(), this.oid);
    }

    public int hash() {
        return this.oid.hashCode();
    }

    // Helper Methods

    @Override
    public int compareTo(@NonNull KUSModel kusModel) {
        return kusModel.oid.compareTo(this.oid);
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(KUSModel.class)) {
            return false;
        }

        KUSModel kus = (KUSModel) obj;
        return kus.oid.equals(this.oid)
                && kus.orgId.equals(this.orgId)
                && kus.customerId.equals(this.customerId)
                && kus.sessionId.equals(this.sessionId);
    }
    //endregion
}

