package com.go4lunch2.Utils;


public class Utils {


    static public Double ratingToStars (Double rating) {
        return rating!=null? ((int) (rating * 2 + .5)) / 2.0 : null;
    }

}
