package com.tms.threed.servletUtil.http.headers;

import com.tms.threed.util.lang.server.date.Date;
import junit.framework.TestCase;

public class Test extends TestCase {

    public void test_CacheControl() throws Exception {

        CacheControl cacheControl1 = new CacheControl();
        cacheControl1.setPublic(true);
        cacheControl1.setMaxAge(CacheControl.ONE_HOUR);

        assertEquals("Cache-Control", cacheControl1.getHeaderName());
        assertEquals("public, max-age=3600", cacheControl1.getHeaderValue());
        assertEquals("Cache-Control: public, max-age=3600", cacheControl1.getHeader());

        CacheControl cacheControl2 = new CacheControl();
        cacheControl2.setMaxAge(CacheControl.ONE_MONTH);
        cacheControl2.setPublic(true);
        assertEquals("Cache-Control", cacheControl2.getHeaderName());
        assertEquals("public, max-age=2592000", cacheControl2.getHeaderValue());
        assertEquals("Cache-Control: public, max-age=2592000", cacheControl2.getHeader());

        CacheControl cacheControl3 = new CacheControl();
        cacheControl3.setMaxAge(null);
        cacheControl3.setPublic(null);
        cacheControl3.setNoCache(true);
        cacheControl3.setPrivate(true);

        System.out.println(cacheControl3.getHeader());


    }

    public void test_Expires() throws Exception {

        Date exp = new Date(2020, 10, 14);
        Expires expires1 = new Expires(exp);

        assertEquals("Expires", expires1.getHeaderName());
        assertEquals("Wed, 14 Oct 2020 07:00:00 GMT", expires1.getHeaderValue());
        assertEquals("Expires: Wed, 14 Oct 2020 07:00:00 GMT", expires1.getHeader());


        Expires expires2 = new Expires(2010, 12, 25);

        assertEquals("Sat, 25 Dec 2010 08:00:00 GMT", expires2.getHeaderValue());

    }


}
