package com.go4lunch2;

import org.junit.Test;

import static org.junit.Assert.*;

import com.go4lunch2.Utils.Utils;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */



public class UtilsTests {

    @Test
    public void ratingToStarsTest() {
        Double[] testValues = {0.4, 1.2, 2.0, 2.8};
        Double[] expectedValues = {0.5, 1.0, 2.0, 3.0};
        assertEquals(Utils.ratingToStars(testValues[0]), expectedValues[0]);
        assertEquals(Utils.ratingToStars(testValues[1]), expectedValues[1]);
        assertEquals(Utils.ratingToStars(testValues[2]), expectedValues[2]);
        assertEquals(Utils.ratingToStars(testValues[3]), expectedValues[3]);
    }


}