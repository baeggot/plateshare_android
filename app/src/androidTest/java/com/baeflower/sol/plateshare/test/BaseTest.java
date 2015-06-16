package com.baeflower.sol.plateshare.test;

import junit.framework.TestCase;

/**
 * Created by sol on 2015-06-09.
 */
public class BaseTest extends TestCase {

    public void testSimple(){
        Adder adder = new Adder();
        assertEquals(5, adder.add(2, 3));
    }

}
