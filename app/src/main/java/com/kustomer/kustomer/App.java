package com.kustomer.kustomer;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App app;

    public App() {
        app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static App getApplication() {
        return app;
    }

    public static Context getAppContext() {
        return app.getApplicationContext();
    }
}
