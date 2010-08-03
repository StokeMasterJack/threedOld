package com.tms.threed.testHarness.server;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

import java.util.List;

public class ThreedUtilTest extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    public void testGetSeriesKeys() throws Exception {
        Path pngRoot = threedConfig.getPngRootFileSystem();
        List<SeriesKey> seriesKeyList = ThreedUtil.getSeriesKeys(pngRoot);
        for (SeriesKey seriesKey : seriesKeyList) {
            System.out.println(seriesKey);
        }

    }
}
