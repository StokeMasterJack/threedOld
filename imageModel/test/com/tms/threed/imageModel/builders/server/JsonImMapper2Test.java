package com.tms.threed.imageModel.builders.server;

import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.featureModel.data.Camry2011;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.lang.shared.Path;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ArrayNode;

public class JsonImMapper2Test extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();

    public void test() throws Exception {
        ImSeries imSeries = getTestImageModel();

        JsonMarshaller m1 = new JsonMarshaller();
        JsonMarshaller m2 = new JsonMarshaller();

        ArrayNode jsonNode2 = m2.jsonForViews(imSeries.getViews());

        String s2 = jsonNode2.toString();

        System.out.println(s2.length());

        JsonMarshaller.prettyPrint(jsonNode2);


    }

    ImSeries getTestImageModel() {
        Camry2011 fm = new Camry2011();
        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(SeriesKey.CAMRY_2011);
        Path pngRoot = threedConfig.getPngRootFileSystem();
        ImageModelBuilder imBuilder = new ImageModelBuilder(fm, seriesInfo, pngRoot);
        return imBuilder.buildImageModel();
    }
}
