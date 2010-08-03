package com.tms.threed.servletUtil;

import com.tms.threed.servletUtil.http.ResponseDecorator;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**

 At present, the below servlet (ThreedServlet imageServer) is only being used
 in the smart soft local dev environment.

 1.  The servlet serves up:

     a.  threed jpgs
     b.  threed pngs
     c.  all gwt generated content

 2. It serves these files by directly reading the files from the web logic hard drive.
    Thus, all threed jpgs, threed pngs and gwt generated files must be on the web logic box
    for this to work. At present, the file paths are hard coded.

 3. The main purpose of this servlet is to add the appropriate caching headers to
    the http response. This serves to:

     a.  Locally test the performance gains resulting from the caching http response headers
     b.  Make the local dev environment faster

 4. In production, this servlet will not be used. Instead, these files will be served by:

    a.  EdgeSuite/NetStorage (for the threed pngs and jpgs)  or
    b.  EdgeSuite/ToyotaApache (for the gwt generated files)
 
 */
public class ThreedServlet extends HttpServlet {

    private ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        System.out.println("ThreedServlet.doGet: " + req.getRequestURI());
        try {
            ResponseDecorator sniffer = new ResponseDecorator(req, resp, threedConfig);

            sniffer.serveFileAsAppropriate();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
