package com.kustomer.kustomer;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.kustomer.kustomersdk.Kustomer;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static App app;
    private static final String K_KUSTOMER_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVhNWY2Y2EzOTZmNTc1MDAxMGI0YTkyZCIsInVzZXIiOiI1YTVmNmNhMzI1OTZiNzAwMDE2MWNkMmIiLCJvcmciOiI1YTVmNmNhM2I1NzNmZDAwMDFhZjczZGQiLCJvcmdOYW1lIjoienp6LWJ4LXRlY2hub2xvZ2llcyIsInVzZXJUeXBlIjoibWFjaGluZSIsInJvbGVzIjpbIm9yZy50cmFja2luZyJdLCJhdWQiOiJ1cm46Y29uc3VtZXIiLCJpc3MiOiJ1cm46YXBpIiwic3ViIjoiNWE1ZjZjYTMyNTk2YjcwMDAxNjFjZDJiIn0.jfkemSKNyc2pazfmfrIxvVs1SmB3F1U6i8S9efooiPo";

    public App() {
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Kustomer.init(getAppContext(),K_KUSTOMER_API_KEY);
        Fabric.with(this, new Crashlytics());
    }

    public static App getApplication() {
        return app;
    }

    public static Context getAppContext() {
        return app.getApplicationContext();
    }
}
