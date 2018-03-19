package com.kustomer.kustomersdk.DataSources;

import android.app.Activity;

import com.kustomer.kustomersdk.Interfaces.KUSKustomerListener;
import com.kustomer.kustomersdk.Kustomer;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSDelegateProxy {
    //region Properties
    private KUSKustomerListener listener;
    //endregion

    //region Methods
    public boolean shouldDisplayInAppNotification(){
        if(listener != null)
            return listener.kustomerShouldDisplayInAppNotification();

        return true;
    }

    public void inAppNotificationOnTapped(Activity activity){
        if(listener != null)
            listener.kustomerInAppNotificationOnTapped();
        else
            Kustomer.showSupport(activity);
    }
    //endregion

    //region Accessors & Mutators

    public KUSKustomerListener getListener() {
        return listener;
    }

    public void setListener(KUSKustomerListener listener) {
        this.listener = listener;
    }

    //endregion
}
