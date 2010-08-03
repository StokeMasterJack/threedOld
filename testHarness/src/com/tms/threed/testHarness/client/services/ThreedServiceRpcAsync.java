package com.tms.threed.testHarness.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tms.threed.threedCore.shared.SeriesKey;

import java.util.List;

public interface ThreedServiceRpcAsync {
    void fetchSeriesKeys(AsyncCallback<List<SeriesKey>> async);

    void satCount(SeriesKey seriesKey, AsyncCallback<Long> async);
}
