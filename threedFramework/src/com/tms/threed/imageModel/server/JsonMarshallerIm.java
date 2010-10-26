package com.tms.threed.imageModel.server;

import com.tms.threed.imageModel.shared.ImFeature;
import com.tms.threed.imageModel.shared.ImFeatureOrPng;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.threedCore.shared.JsonMarshaller;
import com.tms.threed.threedModel.shared.ThreedModel;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Stateless
 */
public class JsonMarshallerIm  implements JsonMarshaller<ImSeries> {

    private static final JsonNodeFactory f = JsonNodeFactory.instance;

    public static ArrayNode toJsonArray(ImSeries imSeries) {
        return new JsonMarshallerIm().jsonForViews(imSeries.getViews());
    }

    public String toJsonString(ImSeries model) {
        return toJsonArray(model).toString();
    }

    public ArrayNode jsonForViews(List<ImView> imViews) {
        ArrayNode a = f.arrayNode();
        for (ImView imView : imViews) {
            a.add(jsonForView(imView));
        }
        return a;
    }

    private JsonNode jsonForView(ImView imView) {
        ObjectNode n = f.objectNode();
        n.put(imView.getName(), jsonForLayers(imView));
        return n;
    }

    private JsonNode jsonForLayers(ImView imView) {
        ArrayNode n = f.arrayNode();
        List<ImLayer> imLayers = imView.getLayers();
        for (ImLayer imLayer : imLayers) {
            n.add(jsonForLayer(imLayer));
        }
        return n;
    }

    private JsonNode jsonForLayer(ImLayer imLayer) {
        ObjectNode n = f.objectNode();
        n.put(imLayer.getName(), jsonForFeaturesOrPngs(imLayer.getChildNodes()));
        return n;
    }

    private JsonNode jsonForFeaturesOrPngs(List<ImFeatureOrPng> fps) {
        ArrayNode n = f.arrayNode();
        for (ImFeatureOrPng fp : fps) {
            n.add(jsonForFeatureOrPng(fp));
        }
        return n;
    }

    private JsonNode jsonForFeatureOrPng(ImFeatureOrPng fp) {
        if (fp.isFeature()) {
            return jsonForFeature((ImFeature) fp);
        } else if (fp.isPng()) {
            return jsonForPng((ImPng) fp);
        } else {
            throw new IllegalStateException();
        }
    }

    private JsonNode jsonForFeature(ImFeature imFeature) {
        ObjectNode n = f.objectNode();
        n.put(imFeature.getName(), jsonForFeaturesOrPngs(imFeature.getChildNodes()));
        return n;
    }

    private JsonNode jsonForPngOriginal(ImPng imPng) {
        return f.numberNode(imPng.getAngle());
    }

    private JsonNode jsonForPng(ImPng imPng) {
        ObjectNode n = f.objectNode();
        n.put(imPng.getAngle() + "", imPng.getSha());
//        n.put(imPng.getAngle() + "", "s"); //sha is pretty big, put the s as a place holder
        return n;
    }

    public static void prettyPrint(JsonNode jsonNode) throws IOException {
        PrintWriter out = new PrintWriter(System.out);
        JsonFactory f = new MappingJsonFactory();
        JsonGenerator g = f.createJsonGenerator(out);
        g.useDefaultPrettyPrinter();
        g.writeObject(jsonNode);
    }


}