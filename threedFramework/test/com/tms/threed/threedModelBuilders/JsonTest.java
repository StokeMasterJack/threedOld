package com.tms.threed.threedModelBuilders;

import com.tms.threed.threedModel.server.SThreedModel;
import com.tms.threed.threedModel.server.SThreedModels;
import junit.framework.TestCase;

public class JsonTest extends TestCase {

    public void test() throws Exception {
        SThreedModels models = SThreedModels.get();
        SThreedModel threedModel = models.getModel("2011", "avalon");
        String json = threedModel.toJsonString();

        System.out.println(json);


    }
}
