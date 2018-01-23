package com.kustomer.kustomersdk.Interfaces;

import com.kustomer.kustomersdk.DataSources.KUSPaginatedDataSource;

/**
 * Created by Junaid on 1/23/2018.
 */

public interface KUSPaginatedDataSourceListener {
    void onLoad(KUSPaginatedDataSource dataSource);
    void onError(KUSPaginatedDataSource dataSource, Error error);
    void onContentChange(KUSPaginatedDataSource dataSource);
}
