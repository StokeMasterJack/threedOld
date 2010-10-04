package com.tms.threed.threedModelBuilders;

import com.tms.threed.threedModel.server.JsonMarshaller;
import com.tms.threed.threedModel.server.SThreedModels;
import com.tms.threed.threedModel.shared.ThreedModel;
import junit.framework.TestCase;

public class JsonTest extends TestCase {

    public void test() throws Exception {
        SThreedModels models = SThreedModels.get();
        ThreedModel threedModel = models.getModel("2011", "avalon");

        JsonMarshaller marshaller = new JsonMarshaller();

        String json = marshaller.toJsonString(threedModel);

        System.out.println(json);


    }
}
