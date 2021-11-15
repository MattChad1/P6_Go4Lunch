package com;


import android.app.Application;

public class MyApplication extends Application {

    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        sApplication = this;
    }

    public static Application getInstance() {
        return sApplication;
    }
}