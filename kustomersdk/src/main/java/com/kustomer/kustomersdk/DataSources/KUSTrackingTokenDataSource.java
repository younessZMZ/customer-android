package com.kustomer.kustomersdk.DataSources;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Utils.KUSConstants;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTrackingTokenDataSource extends KUSObjectDataSource {

    //region Properties
    private String currentTrackingToken = KUSConstants.MockedData.TRACKING_TOKEN;
    private String currentTrackingId = KUSConstants.MockedData.CHAT_SESSION_TRACKING_ID;
    //endregion

    //region LifeCycle
    public KUSTrackingTokenDataSource(KUSUserSession userSession){
        // TODO: Not Implemented
    }
    //endregion

    //region Public Methods
    public String getCurrentTrackingToken() {
        return currentTrackingToken;
    }

    public String getCurrentTrackingId() {
        return currentTrackingId;
    }
    //endregion

    //region Private Methods

    //endregion

}
