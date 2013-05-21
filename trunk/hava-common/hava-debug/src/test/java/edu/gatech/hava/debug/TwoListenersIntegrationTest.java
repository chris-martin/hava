package edu.gatech.hava.debug;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.gatech.hava.engine.HEngine;

public class TwoListenersIntegrationTest {

    private HEngine engine;

    @Before public void setUp() throws Exception {

        engine = new HEngine();

        engine.load("PRIMES = (2, 3, 5, 7, 11, 13, 17, 19, 23);");
        engine.load("function randPrime = random(PRIMES);");
        engine.load("product = sum(i=1 to 20) {randPrime};");

    }

    @Test public void run() throws Exception {

        final HDebugBase listener1 = new HDebugBase();
        final HDebugBase listener2 = new HDebugBase();

        engine.addDebugListener(listener1);
        engine.addDebugListener(listener2);

        engine.run();

        final List<HDebugObject> topVars1 = listener1.getTopLevelVariables();
        final List<HDebugObject> topVars2 = listener2.getTopLevelVariables();

        Assert.assertFalse(topVars1.size() == 0);

        Assert.assertEquals(topVars1.size(), topVars2.size());

    }

    @After public void tearDown() throws Exception {

    }

}
