package com.tms.threed.servletUtil.http;

import com.google.common.io.Files;
import com.tms.threed.servletUtil.http.headers.CacheControl;
import com.tms.threed.servletUtil.http.headers.Expires;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResponseDecorator {

    private final ThreedConfig threedConfig;
    private final Path gwtRoot = new Path("configurator/previewpanel");
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String requestUri;
    private final String contextPath;
    private final Path threedRootFileSystem;
    private final String waRelativeRequestUri;
    public static final Map<String, String> mimeMap = new HashMap<String, String>();

    private boolean gc;
    private boolean gn;
    private boolean vj;
    private boolean vp;
    private boolean vb;

    private boolean cacheForever;
    private boolean cacheNever;
    private boolean threedImage;
    private boolean gwtAppFile;

    public ResponseDecorator(HttpServletRequest request, HttpServletResponse response, ThreedConfig threedConfig) {
        this.threedConfig = threedConfig;
        this.request = request;
        this.response = response;
        this.requestUri = request.getRequestURI();

        this.contextPath = request.getContextPath();

        this.waRelativeRequestUri = requestUri.substring(contextPath.length());


        threedRootFileSystem = threedConfig.getThreedRootFileSystem();


        gc = isGwtDotCacheFile();
        gn = isGwtDotNocacheFile();

        gwtAppFile = requestUri.contains(gwtRoot.toString());

        vj = isThreedJpg();
        vp = isThreedPng();
        vb = isButtonImage();

        cacheForever = gc || vj || vp || vb;

        cacheNever = gn;

        threedImage = vj || vp;


//        printRequestSummary();
    }

    boolean isStaticFileExternalToWar() {
        return vj || vp || vb;
    }

    boolean isStaticFileInternalToWar() {
        return gc || gn;
    }

    public String getExternalOrInternal() {
        if (isStaticFileExternalToWar()) {
            return "external";
        } else if (isStaticFileInternalToWar()) {
            return "internal";
        } else {
            return "unknown";
        }
    }

    public void printRequestSummary() {
        System.out.println("START: ==============");
        System.out.println(requestUri);

        System.out.println("\t serverName: " + request.getServerName());
        System.out.println("\t threedRootFileSystem: " + threedRootFileSystem);
        System.out.println("\t contextPath: " + contextPath);
        System.out.println("\t waRelativeRequestUriX: " + waRelativeRequestUri);
        System.out.println("\t RealFileForVersionedThreedImage: " + getRealFileForVersionedThreedImage());
        System.out.println("\t ExternalInternalOther: " + getExternalOrInternal());
        System.out.println("\t isThreedJpg: " + isThreedJpg());
        System.out.println("\t isThreedPng: " + isThreedPng());
        System.out.println("\t isButtonImage: " + isButtonImage());
        System.out.println("\t isGwtDotCacheFile: " + isGwtDotCacheFile());
        System.out.println("\t isGwtDotNocacheFile: " + isGwtDotNocacheFile());
        System.out.println("\t getCacheCategory: " + getCacheCategory());
        System.out.println("\t getCacheCategory: " + getCacheCategory());
        System.out.println("END: ==============");
        System.out.println();
    }

    public String getCacheCategory() {
        if (gc || vj || vp || vb) {
            return "cacheForever";
        } else if (gn) {
            return "dontCacheAtAll";
        } else {
            return "other";
        }
    }

    public void addCacheHeadersToResponse() {
        if (cacheForever) {
            cacheForever();
        } else if (cacheNever) {
            cacheNever();
        }
    }

    public boolean isGwtDotCacheFile() {
        return requestUri != null && requestUri.contains(".cache.");
    }

    public boolean isGwtDotNocacheFile() {
        return requestUri != null && requestUri.contains(".nocache.");
    }

    public boolean isThreedJpg() {
        return requestUri != null && requestUri.endsWith(".jpg") && requestUri.contains("/vr_");
    }

    public boolean isThreedPng() {
        return requestUri != null && requestUri.endsWith(".png") && requestUri.contains("/vr_");
    }

    public boolean isButtonImage() {
        boolean aa = requestUri.endsWith(".png");
        boolean bb = requestUri.contains("-v");
        boolean cc = requestUri.contains("previewPanel");
        boolean dd = requestUri.contains("angleButtons");
        return requestUri != null && aa && bb && cc && dd;
    }

    public void serveFileAsAppropriate() throws IOException {
//        printRequestSummary();
        addContentTypeToResponse();
        addCacheHeadersToResponse();

        response.setHeader("X-Content-Type-Options", "nosniff");

        streamStaticFileToBrowser();

    }

    private void streamStaticFileToBrowser() throws IOException {
        File realFile = getRealFile();
        if (realFile.canRead()) {
            response.setContentLength((int) realFile.length());
            Files.copy(realFile, response.getOutputStream());
        } else {
            log.error("Could not find file[" + realFile + "]");
            response.sendError(404, "Not found[" + requestUri + "]");
        }
    }

    private static final Log log = LogFactory.getLog(ResponseDecorator.class);

    private void addContentTypeToResponse() {
        response.setContentType(getMimeType());
    }


    private File getRealFile() {
        File retVal;
        if (threedImage) {
            retVal = getRealFileForVersionedThreedImage();
        } else if (gwtAppFile) {
            retVal = getRealFileForWarResource();
        } else {
            retVal = getRealFileForWarResource();
        }
        return retVal;
    }

    private File getRealFileForVersionedThreedImage() {
        ServletContext application = request.getSession().getServletContext();
        String result = application.getRealPath(waRelativeRequestUri);

        if (request.getServerName().contains("ssdev.toyota.com")) {
            result = "c:/Data/tmsConfig" + waRelativeRequestUri;
        }

        return new File(result);
    }

    private File getRealFileForWarResource() {
        ServletContext application = request.getSession().getServletContext();
        String result = application.getRealPath(waRelativeRequestUri);
        return new File(result);
    }

    private void cacheForever() {
        CacheControl cc = new CacheControl();
        cc.setMaxAgeInMonths(12);
        cc.setPublic(true);
        cc.addToResponse(response);

//        LastModified lastModified = new LastModified(new Date().addMonths(-3));
//        lastModified.addToResponse(response);

        //no etag
//        response.setHeader("ETag",requestUri);
    }

    private void cacheNever() {
        Expires expires = Expires.expiresLastWeek();
        expires.addToResponse(response);

        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.addToResponse(response);
    }

    public static String getFileExtension(String requestUri) {
        if (requestUri == null) return null;
        int lastDot = requestUri.lastIndexOf(".");
        return requestUri.substring(lastDot + 1);
    }

    public String getFileExtension() {
        return getFileExtension(requestUri);
    }

    public String getMimeType() {
        String ext = getFileExtension();
        String mimeType = mimeMap.get(ext);
        if (mimeType == null) {
            throw new IllegalStateException("Unable to guess mime-type from extension[" + ext + "] of url[" + requestUri + "]");
        } else {
            return mimeType;
        }
    }

    static {
        mimeMap.put("gif", "image/gif");
        mimeMap.put("jsp", "text/html");
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("png", "image/png");
        mimeMap.put("html", "text/html");
        mimeMap.put("htm", "text/html");
        mimeMap.put("js", "application/x-javascript");
        mimeMap.put("css", "text/css");
    }


}
