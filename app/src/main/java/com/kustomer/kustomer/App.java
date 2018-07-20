package com.kustomer.kustomer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.kustomer.kustomersdk.Kustomer;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static final String K_KUSTOMER_API_KEY =
            "[INSERT_API_KEY]";
    

    @Override
    public void onCreate() {
        super.onCreate();

        Kustomer.init(this,K_KUSTOMER_API_KEY);
        Fabric.with(this, new Crashlytics());
    }

}
