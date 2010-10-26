package com.tms.threed.threedModel.server;

import com.tms.threed.featureModel.server.JsonMarshallerFm;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.server.JsonMarshallerIm;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.JsonMarshaller;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.lang.shared.Path;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMarshallerTm implements JsonMarshaller<ThreedModel> {

    private JsonMarshallerFm fmMarshaller = new JsonMarshallerFm();
    private JsonMarshallerIm imMarshaller = new JsonMarshallerIm();

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public static String marshal(ThreedModel threedModel) {
        return new JsonMarshallerTm().toJsonString(threedModel);
    }

    public ObjectNode toJsonObject(ThreedModel threedModel) {
        return new JsonMarshallerTm().toJsonNode(threedModel);
    }

    @Override public String toJsonString(ThreedModel model) {
        return toJsonObject(model).toString();
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


//    public String toJsonString(ThreedModel model) {
//        return toJsonNode(model).toString();
//    }

}
