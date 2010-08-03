/**
 * Copyright (C) 2003, Toyota Motor Sales, USA Inc.
 *
 * $Id $
 */
package com.tms.threed.threedModel.server;

import junit.framework.TestCase;

import java.util.Set;

public class EBrochureRequestTest extends TestCase {

    public void test_getPackageCodes() throws Exception {
        EBrochureRequest r = new EBrochureRequest();
        r._setPackageCodes("ABCDEF");
        Set<String> set = r.getPackageCodes();
        assertEquals(3,set.size());
        assertTrue(set.contains("AB"));
        assertTrue(set.contains("CD"));
        assertTrue(set.contains("EF"));
    }

    public void test_getAccessoryCodes() throws Exception {
        EBrochureRequest r = new EBrochureRequest();
        r._setAccessoryCodes("EF,WL,DI04,DI05,");
        Set<String> set = r.getAccessoryCodes();
        assertEquals(4,set.size());
        assertTrue(set.contains("EF"));
        assertTrue(set.contains("WL"));
        assertTrue(set.contains("DI04"));
        assertTrue(set.contains("DI05"));
    }

    public void test_getFeatureCodes() throws Exception {
        EBrochureRequest r = new EBrochureRequest();
        System.out.println(r.getFeatureCodes());
    }

}
