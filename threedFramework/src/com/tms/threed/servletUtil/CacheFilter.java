package com.tms.threed.servletUtil;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.servletUtil.http.RespDec;
import com.tms.threed.servletUtil.http.ResponseDecorator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheFilter implements Filter {

    private ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    @Override public void init(FilterConfig config) throws ServletException {

    }

    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RespDec respDec = new RespDec((HttpServletResponse) response, (HttpServletRequest) request);

//        ResponseDecorator sniffer =
                new ResponseDecorator(
                        (HttpServletRequest) request,
                        (HttpServletResponse) response,
                        threedConfig);


        //before



        chain.doFilter(request, respDec);

//        sniffer.setCacheHeaders();
    }

    @Override public void destroy() {

    }
}
