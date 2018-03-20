package com.kustomer.kustomersdk.Interfaces;

import android.app.PendingIntent;
import android.content.Context;

/**
 * Created by Junaid on 3/19/2018.
 */

public interface KUSKustomerListener {
    boolean kustomerShouldDisplayInAppNotification();
    PendingIntent getPendingIntent(Context context);
}
