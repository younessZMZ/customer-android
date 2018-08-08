package com.kustomer.kustomersdk.DataSources;

import android.os.Handler;
import android.os.Looper;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSPaginatedDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Models.KUSPaginatedResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */


public class KUSPaginatedDataSource {

    //region Properties
    private List<KUSModel> fetchedModels;
    private List<KUSModel> tempFetchModels;

    private HashMap<String, KUSModel> fetchedModelsById;

    private KUSPaginatedResponse mostRecentPaginatedResponse;
    private KUSPaginatedResponse lastPaginatedResponse;

    private WeakReference<KUSUserSession> userSession;
    protected List<KUSPaginatedDataSourceListener> listeners;

    private boolean fetching;
    private boolean fetched;
    private boolean fetchedAll;

    private Error error;

    private Object requestMarker;
    //endregion

    //region LifeCycle
    public KUSPaginatedDataSource(KUSUserSession userSession) {
        this.userSession = new WeakReference<>(userSession);
        listeners = new ArrayList<>();
        fetchedModels = new ArrayList<>();
        tempFetchModels = new ArrayList<>();
        fetchedModelsById = new HashMap<>();
    }
    //endregion

    //region Methods
    public int getSize() {
        return fetchedModels.size();
    }

    public List<KUSModel> getList() {
        return tempFetchModels;
    }

    public KUSModel findById(String oid) {
        return fetchedModelsById.get(oid);
    }

    public KUSModel get(int index) {
        return fetchedModels.get(index);
    }

    private int indexOf(KUSModel obj) {
        return indexOfObjectId(obj.getId());
    }

    public KUSModel getFirst() {
        if (getSize() > 0) {
            return fetchedModels.get(0);
        }
        return null;
    }

    private int indexOfObjectId(String objectId) {
        if (objectId == null) {
            return -1;
        }

        KUSModel internalObj = findById(objectId);
        if (internalObj == null)
            return -1;
        for (int i = 0; i < fetchedModels.size(); i++) {
            if (fetchedModels.get(i).getId().equals(internalObj.getId())) {
                return i;
            }
        }
        return -1;
    }

    public void addListener(KUSPaginatedDataSourceListener listener) {
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(KUSPaginatedDataSourceListener listener) {
        listeners.remove(listener);
    }

    public void removeAllListeners(){
        listeners.clear();
    }


    public void fetchLatest() {
        URL url = getFirstUrl();
        if (mostRecentPaginatedResponse != null && mostRecentPaginatedResponse.getFirstPath() != null) {
            url = userSession.get().getRequestManager().urlForEndpoint(mostRecentPaginatedResponse.getFirstPath());
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

        final WeakReference<KUSPaginatedDataSource> weakInstance = new WeakReference<>(this);

        final KUSPaginatedDataSource dataSource = this;

        userSession.get().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_GET,
                url,
                null,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(final Error error, final JSONObject response) {

                        try {
                            final KUSPaginatedResponse pageResponse = new KUSPaginatedResponse(response, dataSource);

                            if(requestMarker != KUSPaginatedDataSource.this.requestMarker  )
                                return;

                            KUSPaginatedDataSource.this.requestMarker = null;
                            KUSPaginatedDataSource.this.prependResponse(pageResponse, error);
                        }
                        catch (JSONException | KUSInvalidJsonException ignore) {}
                    }
                });

    }

    public void fetchNext() {
        URL url = null;
        if(lastPaginatedResponse != null){
            url = userSession.get().getRequestManager().urlForEndpoint(lastPaginatedResponse.getNextPath());
        }else if(mostRecentPaginatedResponse!= null){
            url  = userSession.get().getRequestManager().urlForEndpoint(mostRecentPaginatedResponse.getNextPath());
        }

        if(url == null)
            return;
        if(fetching)
            return;

        fetching = true;
        error = null;

        final Object requestMarker = new Object();
        this.requestMarker = requestMarker;
        final WeakReference<KUSPaginatedDataSource> weakInstance = new WeakReference<>(this);

        final KUSPaginatedDataSource model = this;

        userSession.get().getRequestManager().performRequestType(
                KUSRequestType.KUS_REQUEST_TYPE_GET,
                url,
                null,
                true,
                new KUSRequestCompletionListener() {
                    @Override
                    public void onCompletion(final Error error, JSONObject json) {
                        try {
                            final KUSPaginatedResponse response = new KUSPaginatedResponse(json, model);
                            if(requestMarker != KUSPaginatedDataSource.this.requestMarker  )
                                return;

                            KUSPaginatedDataSource.this.requestMarker = null;
                            KUSPaginatedDataSource.this.appendResponse(response,error);

                        } catch (JSONException | KUSInvalidJsonException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void cancel() {
        fetching = false;
        requestMarker = null;
    }

    public URL getFirstUrl() {
        return null;
    }

    private void appendResponse(KUSPaginatedResponse response, Error error) {
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
        fetchedAll = (fetchedAll || response.getNextPath() == null || response.getNextPath().equals("null"));

        upsertAll(response.getObjects());
        notifyAnnouncersOnLoad();
    }

    private void prependResponse(KUSPaginatedResponse response, Error error) {
        if (error != null || response == null) {
            fetching = false;
            this.error = error != null ? error : new Error();
            notifyAnnouncersOnError(error);
            return;
        }

        mostRecentPaginatedResponse = response;

        fetching = false;
        fetched = true;
        fetchedAll = (fetchedAll || response.getNextPath() == null || response.getNextPath().equals("null"));

        upsertAll(response.getObjects());
        notifyAnnouncersOnLoad();
    }

    public void sort() {
        ArrayList tempFetchModels = new ArrayList<>(fetchedModels);
        Collections.sort(tempFetchModels);
        fetchedModels = tempFetchModels;
    }

    public boolean isFetched(){
        return fetched;
    }

    public boolean isFetchedAll(){
        return fetchedAll;
    }

    public boolean isFetching(){
        return fetching;
    }

    public Error getError() {
        return error;
    }

    public void removeAll(List<KUSModel> objects) {
        if (objects == null || objects.size() == 0) {
            return;
        }

        boolean didChange = false;
        for (KUSModel obj : new ArrayList<>(objects)) {
            int index = indexOf(obj);
            if (index != -1) {
                didChange = true;
                fetchedModels.remove(index);
                fetchedModelsById.remove(obj.getId());
            }
        }

        tempFetchModels = new ArrayList<>(fetchedModels);
        if (didChange) {
            notifyAnnouncersOnContentChange();
        }
    }

    public void upsertAll(List<KUSModel> objects) {
        if (objects == null || objects.size() == 0) {
            return;
        }

        boolean didChange = false;
        for (KUSModel obj : objects) {
            int index = indexOf(obj);
            if (index != -1) {
                KUSModel curObj = findById(obj.getId());
                if (!obj.equals(curObj)) {
                    didChange = true;
                }

                int existingIndex = indexOf(curObj);
                if (existingIndex != -1) {
                    fetchedModels.remove(existingIndex);
                }
                fetchedModels.add(obj);
                fetchedModelsById.put(obj.getId(), obj);
            }
            else {
                didChange = true;
                fetchedModels.add(obj);
                fetchedModelsById.put(obj.getId(), obj);
            }
        }

        sort();
        tempFetchModels = new ArrayList<>(fetchedModels);

        if (didChange) {
            notifyAnnouncersOnContentChange();
        }
    }

    public List<KUSModel> objectsFromJSON(JSONObject jsonObject) {

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
    //endregion

    //region Accessors

    public KUSUserSession getUserSession() {
        return userSession.get();
    }

    public void setUserSession(KUSUserSession userSession) {
        this.userSession = new WeakReference<>(userSession);
    }

    //endregion

    // region Notifier
    public void notifyAnnouncersOnContentChange() {
        for (KUSPaginatedDataSourceListener listener : new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onContentChange(this);
        }
    }

    private void notifyAnnouncersOnError(Error error) {
        for (KUSPaginatedDataSourceListener listener :  new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onError(this, error);
        }
    }

    private void notifyAnnouncersOnLoad() {
        for (KUSPaginatedDataSourceListener listener :  new ArrayList<>(listeners)) {
            if(listener != null)
                listener.onLoad(this);
        }
    }
    //endregion

}
