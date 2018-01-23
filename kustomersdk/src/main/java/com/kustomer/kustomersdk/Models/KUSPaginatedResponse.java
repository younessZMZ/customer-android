package com.kustomer.kustomersdk.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSPaginatedResponse {

    public List<KUSModel> objects;
    public int page;
    public int pageSize;

    public String selfPath;
    public String firstPath;
    public String prevPath;
    public String nextPath;

    public KUSPaginatedResponse() {

    }

    public void initWithJSON(JSONObject json, KUSModel model) throws JSONException {

        Object data = json.get("data");
        boolean dataIsArray = data.getClass().equals(JSONArray.class);
        boolean dataIsJsonObject = data.getClass().equals(JSONObject.class);

        if(!dataIsArray && !dataIsJsonObject)
            return;

        ArrayList<KUSModel> objects = new ArrayList<>();

        if(dataIsArray){
            JSONArray array = (JSONArray) json.get("data");
            for (int i = 0; i<array.length();i++){
                JSONObject jsonObject = array.getJSONObject(0);

                List<KUSModel> models = model.objectsWithJSON(jsonObject);
                if(models != null) {
                    for (int j = models.size() - 1; j >= 0; j--) {
                        objects.add(models.get(j));
                    }
                }
            }
        }else{
            List<KUSModel> models = model.objectsWithJSON(json);

            if(models != null) {
                for (int j = models.size() - 1; j >= 0; j--) {
                    objects.add(models.get(j));
                }
            }
        }

        this.objects = objects;
        page = json.getInt("meta.page");
        pageSize = Math.max(json.getInt("meta.pageSize"),objects.size());

        selfPath = json.getString("links.self");
        firstPath = json.getString("links.first");
        prevPath = json.getString("links.prev");
        nextPath = json.getString("links.next");
    }
}
