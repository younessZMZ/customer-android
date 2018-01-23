package com.kustomer.kustomersdk.DataSources;

import com.kustomer.kustomersdk.API.KUSRequestManager;
import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSPaginatedResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */


public class KUSPaginatedDataSource {
    private List<KUSModel> fetchedModels;
    private HashMap<String, KUSModel> fetchedModelsById;

    private KUSPaginatedResponse mostRecentPaginatedResponse;
    private KUSPaginatedResponse lastPaginatedResponse;

    protected KUSUserSession userSession;
    private List<KUSPaginatedDataSourceListener> listeners;

    protected boolean fetching;
    protected boolean fetched;
    protected boolean fetchedAll;

    public Error error;

    private Object requestMarker;

    public boolean isFetched(){
        return fetched;
    }

    public boolean isFetchedAll(){
        return fetchedAll;
    }

    public boolean isFetching(){
        return fetching;
    }



    public KUSPaginatedDataSource(KUSUserSession userSession) {
        this.userSession = userSession;
        listeners = new ArrayList<>();
        fetchedModels = new ArrayList<>();
        fetchedModelsById = new HashMap<>();
    }

    public int count() {
        return fetchedModels.size();
    }

    public List<KUSModel> allObjects() {
        return fetchedModels;
    }

    public KUSModel objectWithID(String oid) {
        return fetchedModelsById.get(oid);
    }

    public KUSModel objectAtIndex(int index) {
        return fetchedModels.get(index);
    }

    public int indexOfObject(KUSModel obj) {
        return indexOfObjectId(obj.oid);
    }

    public KUSModel firstObject() {
        if (count() > 0) {
            return fetchedModels.get(0);
        }
        return null;
    }

    public int indexOfObjectId(String objectId) {
        if (objectId == null) {
            return -1;
        }

        KUSModel internalObj = objectWithID(objectId);
        if (internalObj == null)
            return -1;
        for (int i = 0; i < fetchedModels.size(); i++) {
            if (fetchedModels.get(i).oid.equals(internalObj.oid)) {
                return i;
            }
        }
        return -1;
    }

    public void addListener(KUSPaginatedDataSourceListener listener) {
        listeners.add(listener);
    }

    public void removeListener(KUSPaginatedDataSourceListener listener) {
        listeners.remove(listener);
    }

    // TODO: completion Listener should be in background
    public void fetchLatest() {
        URL url = firstUrl();
        if (mostRecentPaginatedResponse != null && mostRecentPaginatedResponse.firstPath != null) {
            url = userSession.getRequestManager().urlForEndpoint(mostRecentPaginatedResponse.firstPath);
        }

        if (url == null) {
            return;
        }

        if (fetching) {
            return;
        }

        fetching = true;
        error = null;

        final Object requestMarker = new Object();
        this.requestMarker = requestMarker;
        final KUSPaginatedDataSource instance = this;

        final KUSModel model = modelClass();

        userSession.getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_GET,
                url,
                null,
                true,
                new KUSRequestManager.KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject response) {

                        try {
                            KUSPaginatedResponse pageResponse = new KUSPaginatedResponse();
                            pageResponse.initWithJSON(response, model);


                            if(requestMarker != instance.requestMarker  )
                                return;

                            instance.requestMarker = null;
                            prependResponse(pageResponse, error);
                        }
                        catch (JSONException e) {

                        }
                    }
                });

    }

    // TODO: completion Listener should be in background
    public void fetchNext() {
        URL url = null;
        if(lastPaginatedResponse != null){
            url = userSession.getRequestManager().urlForEndpoint(lastPaginatedResponse.nextPath);
        }else if(mostRecentPaginatedResponse!= null){
            url  = userSession.getRequestManager().urlForEndpoint(mostRecentPaginatedResponse.nextPath);
        }

        if(url == null)
            return;
        if(fetching)
            return;

        fetching = true;
        error = null;

        final Object requestMarker = new Object();
        this.requestMarker = requestMarker;
        final KUSPaginatedDataSource instance = this;

        final KUSModel model = modelClass();

        userSession.getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_GET,
                url,
                null,
                true,
                new KUSRequestManager.KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(Error error, JSONObject json) {
                        try {
                            KUSPaginatedResponse response = new KUSPaginatedResponse();
                            response.initWithJSON(json, model);

                            if(requestMarker != instance.requestMarker  )
                                return;

                            instance.requestMarker = null;
                            instance.appendResponse(response,error);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void cancel() {
        fetching = false;
        requestMarker = null;
    }

    public URL firstUrl() {
        return null;
    }

    public KUSModel modelClass() {
        return new KUSModel();
    }

    public void appendResponse(KUSPaginatedResponse response, Error error) {
        if (error != null || response == null) {
            fetching = false;
            this.error = error != null ? error : new Error();
            notifyAnnouncersOnError(error);
            return;
        }

        lastPaginatedResponse = response;
        mostRecentPaginatedResponse = response;

        fetching = false;
        fetched = true;
        fetchedAll = (fetchedAll || response.nextPath == null);

        upsetObjects(response.objects);
        notifyAnnouncersOnLoad();
    }

    public void prependResponse(KUSPaginatedResponse response, Error error) {
        if (error != null || response == null) {
            fetching = false;
            this.error = error != null ? error : new Error();
            notifyAnnouncersOnError(error);
            return;
        }


        mostRecentPaginatedResponse = response;

        fetching = false;
        fetched = true;
        fetchedAll = (fetchedAll || response.nextPath == null);

        upsetObjects(response.objects);
        notifyAnnouncersOnLoad();
    }

    public void sortObjects() {
        Collections.sort(fetchedModels);
    }

    public void removeObject(List<KUSModel> objects) {
        if (objects.size() == 0) {
            return;
        }

        boolean didChange = false;
        for (KUSModel obj : objects) {
            int index = indexOfObject(obj);
            if (index != -1) {
                didChange = true;
                fetchedModels.remove(index);
                fetchedModelsById.remove(obj.oid);
            }
        }

        if (didChange) {
            notifyAnnouncersOnContentChange();
        }
    }

    public void upsetObjects(List<KUSModel> objects) {
        if (objects.size() == 0) {
            return;
        }

        boolean didChange = false;
        for (KUSModel obj : objects) {
            int index = indexOfObject(obj);
            if (index != -1) {
                KUSModel curObj = objectWithID(obj.oid);
                if (!obj.equals(curObj)) {
                    didChange = true;
                }

                if (didChange) {
                    int existingIndex = indexOfObject(curObj);
                    if (existingIndex != -1) {
                        fetchedModels.remove(existingIndex);
                    }
                    fetchedModels.add(obj);
                    fetchedModelsById.put(obj.oid, obj);
                }
            }
            else {
                didChange = true;
                fetchedModels.add(obj);
                fetchedModelsById.put(obj.oid, obj);
            }
        }

        sortObjects();
        if (didChange) {
            notifyAnnouncersOnContentChange();
        }
    }

    // region Notifier
    public void notifyAnnouncersOnContentChange() {
        for (KUSPaginatedDataSourceListener listener : listeners) {
            listener.onContentChange(this);
        }
    }

    public void notifyAnnouncersOnError(Error error) {
        for (KUSPaginatedDataSourceListener listener : listeners) {
            listener.onError(this, error);
        }
    }

    public void notifyAnnouncersOnLoad() {
        for (KUSPaginatedDataSourceListener listener : listeners) {
            listener.onLoad(this);
        }
    }
    //endregion
}
