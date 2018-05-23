package com.kustomer.kustomersdk.DataSources;

import com.kustomer.kustomersdk.API.KUSUserSession;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Interfaces.KUSRequestCompletionListener;
import com.kustomer.kustomersdk.Models.KUSChatSettings;
import com.kustomer.kustomersdk.Models.KUSModel;
import com.kustomer.kustomersdk.Utils.KUSConstants;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSChatSettingsDataSource extends KUSObjectDataSource implements Serializable {


    //region Initializer
    public KUSChatSettingsDataSource(KUSUserSession userSession) {
        super(userSession);
    }
    //endregion

    //region public Methods
    @Override
    void performRequest(KUSRequestCompletionListener completionListener) {
        getUserSession().getRequestManager().getEndpoint(KUSConstants.URL.SETTINGS_ENDPOINT,
                true,
                completionListener);
    }

    @Override
    KUSModel objectFromJson(JSONObject jsonObject) throws KUSInvalidJsonException {
        return new KUSChatSettings(jsonObject);
    }

    public boolean isChatAvailable(){
        KUSChatSettings settings =  (KUSChatSettings) this.getObject();
        return settings.getEnabled();
    }
    //endregion
}
