package com.kustomer.kustomersdk.DataSources;

import com.kustomer.kustomersdk.API.KUSUserSession;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSTrackingTokenDataSource extends KUSObjectDataSource {

    //region Properties
    private String currentTrackingToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVhNjJlNTU5YTY0YTFkMDAxMGIyY2VkNSIsIm9yZyI6IjVhNWY2Y2EzYjU3M2ZkMDAwMWFmNzNkZCIsImV4cCI6MTUxOTAyMjY4MSwiYXVkIjoidXJuOmNvbnN1bWVyIiwiaXNzIjoidXJuOmFwaSJ9.qjTqfCcAFwk8OlK0rWbalcp1i667-vFLPcgudnIRcK4";
    boolean wantsReset;
    //endregion

    //region LifeCycle
    public KUSTrackingTokenDataSource(KUSUserSession userSession){
        // To be implemented
    }
    //endregion

    //region Public Methods

    public String getCurrentTrackingToken() {
        return currentTrackingToken;
    }

    //endregion

    //region Private Methods

    //endregion

}
