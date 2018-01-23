package com.kustomer.kustomer;

import android.app.Application;
import android.content.Context;

import com.kustomer.kustomersdk.Kustomer;

public class App extends Application {

    private static App app;
    private static final String K_KUSTOMER_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjVhNWY3MDFkMmI4ZmYwMDAxMDJkNTIxNyIsInVzZXIiOiI1YTVmNzAxY2ExNDczZjAwMDEyZjgyMDkiLCJvcmciOiI1YTVmNmNhM2I1NzNmZDAwMDFhZjczZGQiLCJvcmdOYW1lIjoienp6LWJ4LXRlY2hub2xvZ2llcyIsInVzZXJUeXBlIjoibWFjaGluZSIsInJvbGVzIjpbIm9yZy51c2VyIl0sImF1ZCI6InVybjpjb25zdW1lciIsImlzcyI6InVybjphcGkiLCJzdWIiOiI1YTVmNzAxY2ExNDczZjAwMDEyZjgyMDkifQ._a5_Lf6Xeve4ezPv-RUbcdGrWy6OvzFgkUhuKrGFFuU";

    public App() {
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Kustomer.initializeWithAPIKey(K_KUSTOMER_API_KEY);
    }

    public static App getApplication() {
        return app;
    }

    public static Context getAppContext() {
        return app.getApplicationContext();
    }
}
