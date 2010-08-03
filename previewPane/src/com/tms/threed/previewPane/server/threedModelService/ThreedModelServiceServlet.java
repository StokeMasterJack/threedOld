package com.tms.threed.previewPane.server.threedModelService;

import com.tms.threed.threedModel.server.SThreedModel;
import com.tms.threed.threedModel.server.SThreedModels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ThreedModelServiceServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String seriesName = request.getParameter("seriesName");
            String seriesYear = request.getParameter("seriesYear");


            SThreedModels threedModels = SThreedModels.get();
            SThreedModel model = threedModels.getModel(seriesYear, seriesName);

            String contentType = "application/json;charset=UTF-8";

            String s = model.toJsonString();

            int contentLength = s.length();

            response.setContentType(contentType);
            response.setContentLength(contentLength);

            PrintWriter out = response.getWriter();
            out.print(s);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
