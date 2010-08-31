package com.tms.threed.servletUtil.http.headers;

import com.tms.threed.util.lang.server.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

/*
*  max-age=[seconds]
*       specifies the maximum amount
*       of time that an representation will be
*       considered fresh. Similar to Expires, this directive
*       is relative to the time of the request, rather than absolute.
*       [seconds] is the number of seconds from the time of
*       the request you wish the representation to be fresh for.
*
* s-maxage=[seconds]
*       similar to max-age, except that
*       it only applies to shared (e.g., proxy) caches.
*
* public
*       marks authenticated responses as cacheable; normally,
*       if HTTP authentication is required, responses are automatically
*       uncacheable.
*
* no-cache
*       forces caches to submit the request to the origin server for
*       validation before releasing a cached copy, every time.
*       This is useful to assure that authentication is respected
*       (in combination with public), or to maintain rigid freshness,
*       without sacrificing all of the benefits of caching.
*
* no-store
*       instructs caches not to keep a copy of the representation
*       under any conditions.
*
* must-revalidate
*       tells caches that they must obey any freshness information
*       you give them about a representation. HTTP allows caches to
*       serve stale representations under special conditions;
*       by specifying this header, you�re telling the cache
*       that you want it to strictly follow your rules.
*
*
* proxy-revalidate
*       similar to must-revalidate, except that it only applies to
*       proxy caches.
*/

public class CacheControl {

    public static final String HEADER_NAME = "Cache-Control";

    public static final Integer ONE_MINUTE = new Integer(1);
    public static final Integer HALF_HOUR = new Integer(30);
    public static final Integer ONE_HOUR = new Integer(60);
    public static final Integer ONE_DAY = new Integer(60 * 24);
    public static final Integer ONE_MONTH = new Integer(60 * 24 * 30);

    private Boolean ccPrivate = null;
    private Boolean ccPublic = null;
    private Integer maxAge = null; //in minutes
    private Integer sMaxAge = null;
    private Boolean noCache = null;
    private Boolean noStore = null;
    private Boolean mustRevalidate = null;
    private Boolean proxyRevalidate = null;

    public CacheControl() {
    }

    public CacheControl(int maxAgeSeconds) {
        this.sMaxAge = maxAgeSeconds;
    }

    public Boolean getPrivate() {
        return ccPrivate;
    }

    public void setPrivate(Boolean ccPrivate) {
        this.ccPrivate = ccPrivate;
    }

    public Boolean getCcPublic() {
        return ccPublic;
    }

    public void setPublic(Boolean ccPublic) {
        this.ccPublic = ccPublic;
    }

    /**
     *
     * @return maxAge in minutes
     */
    public Integer getMaxAge() {
        return maxAge;
    }

    /**
     * @param maxAge in minutes
     */
    public void setMaxAge(Integer maxAgeInSeconds) {
        this.maxAge = maxAgeInSeconds;
    }

    public void setMaxAgeInMonths(Integer maxAgeInMonths) {
        this.maxAge = ONE_MONTH * maxAgeInMonths;
    }

    public Integer getsMaxAge() {
        return sMaxAge;
    }

    public void setsMaxAge(Integer sMaxAge) {
        this.sMaxAge = sMaxAge;
    }

    public Boolean getNoCache() {
        return noCache;
    }

    public void setNoCache(Boolean noCache) {
        this.noCache = noCache;
    }

    public Boolean getNoStore() {
        return noStore;
    }

    public void setNoStore(Boolean noStore) {
        this.noStore = noStore;
    }

    public Boolean getMustRevalidate() {
        return mustRevalidate;
    }

    public void setMustRevalidate(Boolean mustRevalidate) {
        this.mustRevalidate = mustRevalidate;
    }

    public Boolean getProxyRevalidate() {
        return proxyRevalidate;
    }

    public void setProxyRevalidate(Boolean proxyRevalidate) {
        this.proxyRevalidate = proxyRevalidate;
    }

    public String getHeaderValue() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        if (ccPrivate != null && ccPrivate)
            out.print("private, ");
        if (ccPublic != null && ccPublic)
            out.print("public, ");
        if (maxAge != null)
            out.print("max-age=" + (maxAge * 60) + ", ");
        if (noCache != null && noCache)
            out.print("no-cache, ");
        if (noStore != null && noStore)
            out.print("no-store, ");
        out.flush();
        String headerValue = stringWriter.toString().trim();
        headerValue = StringUtil.chompTrailingComma(headerValue);
        //System.out.println("headerValue = " + headerValue);
        return headerValue;
    }

    public String getHeaderName() {
        return HEADER_NAME;
    }

    public String getHeader() {
        return getHeaderName() + ": " + getHeaderValue();
    }

    public String toString() {
        return getHeader();
    }

    public void addToResponse(HttpServletResponse response) {
        response.setHeader(getHeaderName(), getHeaderValue());
    }
}
