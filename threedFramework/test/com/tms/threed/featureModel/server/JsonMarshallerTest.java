package com.tms.threed.featureModel.server;

import com.tms.threed.featureModel.data.Trim;
import com.tms.threed.featureModel.data.Camry2011;
import junit.framework.TestCase;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMarshallerTest extends TestCase {

    public void test() throws Exception {
        Trim fm = new Camry2011();
        JsonMarshaller mapper = new JsonMarshaller();
        ObjectNode jsFeatureModel = mapper.mapFeatureModel(fm);

        System.out.println(jsFeatureModel.toString());
        System.out.println();
        JsonMarshaller.prettyPrint(jsFeatureModel);
    }


}

