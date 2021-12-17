package com.go4lunch2;

import android.app.Application;

public class MyApplication extends Application {

    private static Application sApplication;
    private static Boolean debug;


    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        if (BuildConfig.DEBUG) debug = true;
        else debug = false;
//        debug = false;
    }

    public static Application getInstance() {
        return sApplication;
    }

    public static Boolean getDebug() {
        return debug;
    }
}