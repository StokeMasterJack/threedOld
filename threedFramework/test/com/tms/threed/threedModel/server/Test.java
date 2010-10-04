package com.tms.threed.threedModel.server;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;

public class Test extends TestCase {

    Path httpImageRoot = new Path("http://images.toyota.com.edgesuite.net/threed_framework");

    public void testVersion() throws Exception {

        SeriesRepo aRepo = Repos.getSeriesRepo(SeriesKey.AVALON_2011);
        SeriesRepo cRepo = Repos.getSeriesRepo(SeriesKey.CAMRY_2011);

        System.out.println("AVALON_2011 = [" + aRepo.getCurrentVersion() + "]");
        System.out.println("CAMRY_2011 = [" + cRepo.getCurrentVersion() + "]");

        SeriesDir aSeries = aRepo.getVersion();
        SeriesDir cSeries = cRepo.getVersion();

        assertEquals(aSeries.getVersion(),aRepo.getCurrentVersion());
        assertEquals(cSeries.getVersion(),cRepo.getCurrentVersion());

        System.out.println("aSeries = [" + aSeries.getSeriesDir() + "]");
        System.out.println("cSeries = [" + cSeries.getSeriesDir() + "]");


        ThreedModel aThreedModel = aSeries.createThreedModel(httpImageRoot);
        ThreedModel cThreedModel = cSeries.createThreedModel(httpImageRoot);

        System.out.println("aPath = [" + aThreedModel.getImageModel().getPath() + "]");
        System.out.println("cPath = [" + cThreedModel.getImageModel().getPath() + "]");



    }

    

}
