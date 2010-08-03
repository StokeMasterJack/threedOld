package com.tms.threed.testHarness.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.tms.threed.threedCore.shared.SeriesKey;

import java.util.List;

@RemoteServiceRelativePath("threedServiceRpc")
public interface ThreedServiceRpc extends RemoteService {

    List<SeriesKey> fetchSeriesKeys();
    long satCount(SeriesKey seriesKey);


    /**
     * Utility/Convenience class.
     * Use ThreedService.App.getInstance() to access static instance of ThreedServiceAsync
     */
    public static class App {
        private static final ThreedServiceRpcAsync ourInstance = (ThreedServiceRpcAsync) GWT.create(ThreedServiceRpc.class);

        public static ThreedServiceRpcAsync getInstance() {
            return ourInstance;
        }
    }


}
