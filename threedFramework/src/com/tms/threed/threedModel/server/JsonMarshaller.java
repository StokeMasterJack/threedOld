package com.tms.threed.threedModel.server;

import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMarshaller {

    private com.tms.threed.featureModel.server.JsonMarshaller fmMarshaller = new com.tms.threed.featureModel.server.JsonMarshaller();
    private com.tms.threed.imageModel.server.JsonMarshaller imMarshaller = new com.tms.threed.imageModel.server.JsonMarshaller();

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public static String marshal(ThreedModel threedModel){
        return new JsonMarshaller().toJsonString(threedModel);
    }

    public ObjectNode toJsonNode(ThreedModel model) {
        ObjectNode jsThreedModel = f.objectNode();
        Path httpImageRoot = model.getHttpImageRoot();
        jsThreedModel.put("httpImageRoot", httpImageRoot.toString());
        jsThreedModel.put("seriesId", toJsonNode(model.getSeriesId()));
        jsThreedModel.put("featureModel", toJsonNode(model.getFeatureModel()));
        jsThreedModel.put("views", toJsonNode(model.getImageModel()));
        return jsThreedModel;
    }

    private ObjectNode toJsonNode(FeatureModel fm) {
        return fmMarshaller.mapFeatureModel(fm);
    }

    private ArrayNode toJsonNode(ImSeries im) {
        return imMarshaller.jsonForViews(im.getViews());
    }

    private ObjectNode toJsonNode(SeriesId seriesId) {
        ObjectNode jsSeriesId = f.objectNode();
        jsSeriesId.put("year", seriesId.getYear());
        jsSeriesId.put("name", seriesId.getName());
        jsSeriesId.put("version", seriesId.getVersion());
        return jsSeriesId;
    }


    public String toJsonString(ThreedModel model) {
        return toJsonNode(model).toString();
    }

}
