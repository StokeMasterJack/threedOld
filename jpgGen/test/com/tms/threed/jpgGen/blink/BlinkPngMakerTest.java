package com.tms.threed.jpgGen.blink;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedCore.shared.*;
import com.tms.threed.jpgGen.singleJpgGen.ImageMagicHomes;
import junit.framework.TestCase;

import java.io.File;

public class BlinkPngMakerTest extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();


    public void test() throws Exception {
        File imHome = ImageMagicHomes.DAVE_MAC;
        BlinkPngMaker blinkPngMaker = new BlinkPngMaker(threedConfig, imHome);
        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(SeriesKey.CAMRY_2011);
        blinkPngMaker.generateBlinkPngs(seriesInfo.getSeriesKey());
    }

}
