package com.go4lunch2.Utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.firebase.Timestamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class Utils {


    static public Double ratingToStars (Double rating) {
        return rating!=null? ((int) (rating * 2 + .5)) / 2.0 : null;
    }

    static public Boolean ValidForToday (Timestamp timestamp) {
        if (timestamp == null) return false;
        else {
            long valueDB = timestamp.getSeconds() * 1000;

            Calendar now = Calendar.getInstance();
            Calendar noon = Calendar.getInstance();
            noon.set(Calendar.HOUR_OF_DAY, 12);
            noon.set(Calendar.MINUTE, 15);
            noon.set(Calendar.SECOND, 0);

            // If we are after noon
            if (noon.getTimeInMillis() < now.getTimeInMillis()) {
                if (valueDB < noon.getTimeInMillis()) return false;
                else return true;
            }

            else {
                if (valueDB < (noon.getTimeInMillis() - 24 * 3600 * 1000)) return false;
                else return true;
            }
        }
    }





    }
