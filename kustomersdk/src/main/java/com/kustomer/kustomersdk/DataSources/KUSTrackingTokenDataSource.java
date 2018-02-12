package com.kustomer.kustomersdk.DataSources;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Enums.KUSRequestType;
import com.kustomer.kustomersdk.Interfaces.KUSObjectDataSourceListener;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Kustomer;
import com.kustomer.kustomersdk.Models.KUSTrackingToken;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTrackingTokenDataSource extends KUSObjectDataSource implements KUSObjectDataSourceListener {

    //region Properties
    private boolean wantsReset;
    //endregion

    //region Initializer
    public KUSTrackingTokenDataSource(KUSUserSession userSession){
        super(userSession);
        addListener(this);
    }
    //endregion

    //region Public Methods
    public void performRequest(KUSRequestCompletionListener listener){
        String endPoint = wantsReset ? KUSConstants.URL.TRACKING_TOKEN_ENDPOINT :
                KUSConstants.URL.CURRENT_TRACKING_TOKEN_ENDPOINT;

        URL url = getUserSession().getRequestManager().urlForEndpoint(endPoint);
        KUSRequestType requestType = wantsReset ? KUSRequestType.KUS_REQUEST_TYPE_POST :
                KUSRequestType.KUS_REQUEST_TYPE_GET;

        getUserSession().getRequestManager().performRequestType(
                requestType,
                url,
                null,
                false,
                getAdditionalHeaders(),
                listener );
    }

    public void reset(){
        wantsReset = true;
        cancel();
        fetch();
    }
    //endregion

    //region Private Methods
    private HashMap<Object, Object> getAdditionalHeaders(){

        String currentTrackingToken = getCurrentTrackingToken();
        HashMap<Object, Object> headers = new HashMap<>();
        if(currentTrackingToken != null) {
            headers.put(KUSConstants.Keys.K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY, currentTrackingToken);
            return headers;
        }
        else {
            String cachedTrackingToken = getUserSession().getSharedPreferences().getTrackingToken(Kustomer.getContext());

            if(cachedTrackingToken != null) {
                headers.put(KUSConstants.Keys.K_KUSTOMER_TRACKING_TOKEN_HEADER_KEY, cachedTrackingToken);
                return headers;
            }
        }

        return null;
    }
    //endregion

    //region Accessors
    public String getCurrentTrackingToken() {
        KUSTrackingToken trackingTokenObj = (KUSTrackingToken) getObject();
        return trackingTokenObj.getToken();
    }

    //endregion

    //region Listener
    @Override
    public void objectDataSourceOnLoad(KUSObjectDataSource dataSource) {
        wantsReset = false;

        String currentTrackingToken = getCurrentTrackingToken();
        if(currentTrackingToken != null)
            getUserSession().getSharedPreferences().setTrackingToken(Kustomer.getContext(),
                    currentTrackingToken);
    }

    @Override
    public void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error) {

    }
    //endregion

}
