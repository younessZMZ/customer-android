package com.kustomer.kustomersdk.Models;

import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.kustomer.kustomersdk.Utils.JsonHelper.integerFromKeyPath;
import static com.kustomer.kustomersdk.Utils.JsonHelper.stringFromKeyPath;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSPaginatedResponse {

    public List<KUSModel> objects;
    public Integer page;
    public Integer pageSize;

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
                JSONObject jsonObject = array.getJSONObject(i);

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

        page = integerFromKeyPath(json,"meta.page");

        Integer a= integerFromKeyPath(json,"meta.pageSize");
        if(a!= null)
            pageSize = Math.max(a,objects.size());

        selfPath = stringFromKeyPath(json,"links.self");
        firstPath = stringFromKeyPath(json,"links.first");
        prevPath = stringFromKeyPath(json,"links.prev");
        nextPath = stringFromKeyPath(json,"links.next");
    }
}
