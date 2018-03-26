package com.kustomer.kustomer;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.kustomer.kustomersdk.Kustomer;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static final String K_KUSTOMER_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVhOGFlYzdiODQyMGM5MDAxYjc0MjJiYiIsInVzZXIiOiI1YThhZWM3YjdmZjBlOTAwMDE0YWNiOTkiLCJvcmciOiI1YThhZWM3YjdmZjBlOTAwMDE0YWNiOTQiLCJvcmdOYW1lIjoienp6LWJ4LXRlY2hub2xvZ2llcy1vcmcyIiwidXNlclR5cGUiOiJtYWNoaW5lIiwicm9sZXMiOlsib3JnLnRyYWNraW5nIl0sImF1ZCI6InVybjpjb25zdW1lciIsImlzcyI6InVybjphcGkiLCJzdWIiOiI1YThhZWM3YjdmZjBlOTAwMDE0YWNiOTkifQ._UldGeFhP6Yv0QhpEMNNfS8MuZleOXNRfgqFvNAF6ds";

    @Override
    public void onCreate() {
        super.onCreate();

        Kustomer.init(this,K_KUSTOMER_API_KEY);
        Fabric.with(this, new Crashlytics());
    }

}
