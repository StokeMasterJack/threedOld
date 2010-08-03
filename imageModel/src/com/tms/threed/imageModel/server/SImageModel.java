package com.tms.threed.imageModel.server;

import com.tms.threed.imageModel.builders.server.JsonMarshaller;
import com.tms.threed.imageModel.shared.ImSeries;
import org.codehaus.jackson.node.ArrayNode;

import java.io.IOException;

public class SImageModel {

    private static final JsonMarshaller jsonMapper = new JsonMarshaller();

    private ImSeries imageModel;

    public SImageModel(ImSeries imageModel) {
        this.imageModel = imageModel;
    }

    public ArrayNode toJsonArray() {
        return jsonMapper.jsonForViews(imageModel.getViews());
    }

    public void prettyPrintJson() throws IOException {
//        JsonMapper.prettyPrint(toJsonNode());
    }

    public ImSeries getImageModel() {
        return imageModel;
    }
}