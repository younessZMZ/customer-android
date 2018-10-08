package com.kustomer.kustomer;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.kustomer.kustomersdk.Kustomer;

import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private static final String K_KUSTOMER_API_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjViYjY1Y2NlMjM2N2Y2MDAyNDNjNWY0MCIsInVzZXIiOiI1YmI2NWNjZDg3MzIzNzAwMThjMGY0MTAiLCJvcmciOiI1YTVmNmNhM2I1NzNmZDAwMDFhZjczZGQiLCJvcmdOYW1lIjoienp6LWJ4LXRlY2hub2xvZ2llcyIsInVzZXJUeXBlIjoibWFjaGluZSIsInJvbGVzIjpbIm9yZy50cmFja2luZyJdLCJhdWQiOiJ1cm46Y29uc3VtZXIiLCJpc3MiOiJ1cm46YXBpIiwic3ViIjoiNWJiNjVjY2Q4NzMyMzcwMDE4YzBmNDEwIn0.zYrjSWZE5iJ6AgmVyBU7r6yqLCJvHHvsAnIj_0LuM3E";
    //[INSERT_API_KEY]

    @Override
    public void onCreate() {
        super.onCreate();

        Kustomer.init(this, K_KUSTOMER_API_KEY);
        Fabric.with(this, new Crashlytics());
    }

}
