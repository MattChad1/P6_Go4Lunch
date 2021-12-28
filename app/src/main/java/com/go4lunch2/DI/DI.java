package com.go4lunch2.DI;

import android.content.Context;
import android.content.res.AssetManager;

import com.go4lunch2.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DI {
    FirebaseFirestore db;

    public FirebaseFirestore getDatabase() {
        return FirebaseFirestore.getInstance();
    }

    public static Context getAppContext() {return MyApplication.getInstance(); }

    static public BufferedReader getReader(Context ctx, String file) throws IOException {
        AssetManager am = ctx.getAssets();
        InputStream is = am.open(file);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader;
    }



}
