package com.kustomer.kustomersdk.Interfaces;

import com.kustomer.kustomersdk.DataSources.KUSObjectDataSource;

/**
 * Created by Junaid on 1/29/2018.
 */

public interface KUSObjectDataSourceListener {
    void objectDataSourceOnLoad(KUSObjectDataSource dataSource);
    void objectDataSourceOnError(KUSObjectDataSource dataSource, Error error);
}
