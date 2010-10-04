package com.tms.threed.imageModel.server;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import junit.framework.TestCase;

public class EmptyPngFinderTest extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    public void test() throws Exception {
        EmptyPngFinder emptyPngFinder = new EmptyPngFinder(threedConfig);

//        emptyPngFinder.showEmptyPngFiles(SeriesKey.CAMRY_2011);
        emptyPngFinder.deleteEmptyPngFiles(SeriesKey.CAMRY_2011);

    }
}
