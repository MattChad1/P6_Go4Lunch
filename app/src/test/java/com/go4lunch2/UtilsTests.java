package com.go4lunch2;

import static org.junit.Assert.assertEquals;

import com.go4lunch2.Utils.Utils;

import org.junit.Test;



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