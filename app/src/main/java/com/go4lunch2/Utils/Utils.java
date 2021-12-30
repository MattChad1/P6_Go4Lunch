package com.go4lunch2.Utils;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.google.firebase.Timestamp;

import java.util.Calendar;

public class Utils {

    static public String distanceConversion(Integer meters) {
        if (meters < 1000) return MyApplication.getInstance().getString(R.string.distance_meters, meters);
        else return MyApplication.getInstance().getString(R.string.distance_kms, ((float) meters) / 1000);
    }

    static public Double ratingToStars(Double rating) {
        return rating != null ? ((int) (rating * 2 + .5)) / 2.0 : null;
    }

    static public Boolean ValidForToday(Timestamp timestamp) {
        if (timestamp == null) return false;
        else {
            long valueDB = timestamp.getSeconds() * 1000 * 1000;

            Calendar now = Calendar.getInstance();
            Calendar noon = Calendar.getInstance();
            noon.set(Calendar.HOUR_OF_DAY, 12);
            noon.set(Calendar.MINUTE, 15);
            noon.set(Calendar.SECOND, 0);

            // If we are after noon
            if (noon.getTimeInMillis() < now.getTimeInMillis()) {
                if (valueDB < (noon.getTimeInMillis()-900)) return false;
                else return true;
            }

            else {
                if (valueDB < ((noon.getTimeInMillis()-900) - 24 * 3600 * 1000 )) return false;
                else return true;
            }
        }
    }
}
