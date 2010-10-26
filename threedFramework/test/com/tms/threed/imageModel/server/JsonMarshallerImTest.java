package com.tms.threed.imageModel.server;

import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.server.ThreedConfigHelper;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.threedModel.server.Repos;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ArrayNode;

public class JsonMarshallerImTest extends TestCase {

    ThreedConfig threedConfig = ThreedConfigHelper.get().getThreedConfig();


    public void test() throws Exception {

        ImSeries imSeries = getTestImageModel(); //slow

        JsonMarshallerIm m2 = new JsonMarshallerIm();

        ArrayNode jsonNode2 = m2.jsonForViews(imSeries.getViews());


        String imJson = jsonNode2.toString();


        System.out.println(imJson);
//        JsonMarshaller.prettyPrint(s2);


    }


    ImSeries getTestImageModel() {
//        return Repos.createModel(SeriesKey.TUNDRA_2011).getImageModel();
        return Repos.createModel(SeriesKey.TACOMA_2011).getImageModel();
    }
}
