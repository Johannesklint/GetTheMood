package com.example.johannesklint.spotifyapitest.main;

import junit.framework.TestCase;

/**
 * Created by johannesklint on 16-02-19.
 */
public class UnitTestTest extends TestCase {

    UnitTest unitTest;

    public void setUp() throws Exception {
        super.setUp();
        unitTest = new UnitTest();
    }

    public void testCountTracks() throws Exception {
        //5 items in darkside arraylist and 5 in main2activity arraylist
        assertEquals(10, unitTest.countTracks(5, 5), 0);
    }
}