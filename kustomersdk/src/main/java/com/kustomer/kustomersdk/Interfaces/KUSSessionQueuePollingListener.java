package com.kustomer.kustomersdk.Interfaces;

import com.kustomer.kustomersdk.API.KUSSessionQueuePollingManager;
import com.kustomer.kustomersdk.Models.KUSSessionQueue;

public interface KUSSessionQueuePollingListener {
    void onPollingStarted(KUSSessionQueuePollingManager manager);
    void onSessionQueueUpdated(KUSSessionQueuePollingManager manager, KUSSessionQueue sessionQueue);
    void onPollingEnd(KUSSessionQueuePollingManager manager);
    void onPollingCanceled(KUSSessionQueuePollingManager manager);
    void onFailure(Error error, KUSSessionQueuePollingManager manager);
}
