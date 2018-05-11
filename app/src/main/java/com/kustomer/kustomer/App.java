package com.kustomer.kustomer;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.kustomer.kustomersdk.Kustomer;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static final String K_KUSTOMER_API_KEY =
            "[INSERT_API_KEY_HERE]";
    @Override
    public void onCreate() {
        super.onCreate();

        Kustomer.init(this,K_KUSTOMER_API_KEY);
        Fabric.with(this, new Crashlytics());
    }

}
