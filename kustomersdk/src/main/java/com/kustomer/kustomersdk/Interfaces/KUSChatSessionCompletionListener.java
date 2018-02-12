package com.kustomer.kustomersdk.Interfaces;

import com.kustomer.kustomersdk.Models.KUSChatSession;

/**
 * Created by Junaid on 2/12/2018.
 */

public interface KUSChatSessionCompletionListener {
    void onComplete(Error error, KUSChatSession session);
}
