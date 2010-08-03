package com.tms.threed.servletUtil.http.headers;

import com.tms.threed.servletUtil.http.HttpUtil;
import com.tms.threed.util.lang.server.date.Date;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public class LastModified {

    private String headerValue;

    public LastModified(Date d) {
        long lastModified = d.toUtilDate().getTime();
        java.util.Date utilDate = new java.util.Date(lastModified);
        headerValue = HttpUtil.getHttpGmtString(utilDate);
    }

    public LastModified(File file) {
        long lastModified = file.lastModified();
        java.util.Date utilDate = new java.util.Date(lastModified);
        headerValue = HttpUtil.getHttpGmtString(utilDate);
    }

    public String getHeaderName() {
        return "Last-Modified";
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public String getHeader() {
        return getHeaderName() + ": " + getHeaderValue();
    }

    @Override public String toString() {
        return getHeader();
    }

    public void addToResponse(HttpServletResponse response) {
        response.setHeader(getHeaderName(), getHeaderValue());
    }
}
