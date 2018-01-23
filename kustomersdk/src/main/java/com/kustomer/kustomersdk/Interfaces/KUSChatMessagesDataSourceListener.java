package com.kustomer.kustomersdk.Interfaces;

import com.kustomer.kustomersdk.DataSources.KUSChatMessagesDataSource;

/**
 * Created by Junaid on 1/23/2018.
 */

public interface KUSChatMessagesDataSourceListener extends KUSPaginatedDataSourceListener {
    void onCreateSessionId(KUSChatMessagesDataSource source, String sessionId);
}
