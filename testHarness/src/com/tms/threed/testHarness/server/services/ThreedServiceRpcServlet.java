package com.tms.threed.testHarness.server.services;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModelJavaBdd.BddBuilder;
import com.tms.threed.testHarness.client.services.ThreedServiceRpc;
import com.tms.threed.testHarness.server.ThreedUtil;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.server.JsonMarshaller;
import com.tms.threed.threedModel.server.SThreedModels;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;

import java.util.List;

public class ThreedServiceRpcServlet extends RemoteServiceServlet implements ThreedServiceRpc {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    @Override public List<SeriesKey> fetchSeriesKeys() {
        Path pngRoot = threedConfig.getPngRootFileSystem();
        return ThreedUtil.getSeriesKeys(pngRoot);
    }

    @Override public long satCount(SeriesKey seriesKey) {
        ThreedModel threedModel = SThreedModels.get().getModel(seriesKey);
        FeatureModel featureModel = threedModel.getFeatureModel();
        BddBuilder bddBuilder = new BddBuilder(featureModel);
        return bddBuilder.satCount();
    }


}