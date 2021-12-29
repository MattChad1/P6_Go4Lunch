package com.go4lunch2;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApplication extends Application {

    public static SharedPreferences settings;
    public static final String PREFS_NAME = "Preferences";
    public static final String PREFS_NOTIFS = "Notifications Prefs";
    public static final String PREFS_CENTER = "Center Prefs";
    public static final String PREFS_CENTER_COMPANY = "Center Prefs company";
    public static final String PREFS_CENTER_GPS = "Center Prefs GPS";

    private static Application sApplication;
    private static Boolean debug;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        if (BuildConfig.DEBUG) debug = true;
        else debug = false;
        debug = false;

        settings = getSharedPreferences(PREFS_NAME, 0);

        if (!settings.contains(PREFS_NOTIFS)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PREFS_NOTIFS, true);
            editor.putString(PREFS_CENTER, PREFS_CENTER_COMPANY);
            editor.commit();
        }
    }

    public static Application getInstance() {
        return sApplication;
    }

    public static Boolean getDebug() {
        return debug;
    }
}