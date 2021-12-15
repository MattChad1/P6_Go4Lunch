package com.go4lunch2.Utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {


    static public Double ratingToStars (Double rating) {
        return rating!=null? ((int) (rating * 2 + .5)) / 2.0 : null;
    }



}
