package com.tms.threed.imageModel.builders.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.imageModel.shared.ImFeature;
import com.tms.threed.imageModel.shared.ImFeatureOrPng;
import com.tms.threed.imageModel.shared.ImLayer;
import com.tms.threed.imageModel.shared.ImPng;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.imageModel.shared.ImView;
import com.tms.threed.threedCore.shared.SeriesInfo;

import java.util.ArrayList;
import java.util.List;

public class JsonUnmarshaller {

    private final FeatureModel featureModel;
    private final SeriesInfo seriesInfo;

    public JsonUnmarshaller(FeatureModel featureModel, SeriesInfo seriesInfo) {
        this.featureModel = featureModel;
        this.seriesInfo = seriesInfo;
    }

    public ImSeries parseSeries(String viewsAsText) {
        JSONArray jsViews = JSONParser.parse(viewsAsText).isArray();
        return parseSeries(jsViews);
    }

    public ImSeries parseSeries(JSONArray jsViews) {
        ArrayList<ImView> imViews = new ArrayList<ImView>();
        for (int iv = 0; iv < jsViews.size(); iv++) {
            JSONObject jsView = jsViews.get(iv).isObject();
            String viewName = jsView.keySet().iterator().next();
            ArrayList<ImLayer> imLayers = new ArrayList<ImLayer>();
            JSONArray jsLayers = jsView.get(viewName).isArray();
            for (int il = 0; il < jsLayers.size(); il++) {
                JSONObject jsLayer = jsLayers.get(il).isObject();
                String layerName = jsLayer.keySet().iterator().next();
                JSONArray jsFeaturesOrPngs = jsLayer.get(layerName).isArray();

                List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(3, jsFeaturesOrPngs);


                ImLayer imLayer = new ImLayer(2, layerName, imFeatureOrPngs);
                imLayers.add(imLayer);
            }

            ImView imView = new ImView(1, viewName, imLayers);
            imViews.add(imView);
        }


        return new ImSeries(0, featureModel, seriesInfo, imViews);
    }

    public List<ImFeatureOrPng> parseFeaturesOrPngs(int depth, JSONArray jsFeaturesOrPngs) {
        List<ImFeatureOrPng> imFeatureOrPngs = new ArrayList<ImFeatureOrPng>();
        for (int i = 0; i < jsFeaturesOrPngs.size(); i++) {
            JSONValue jsFeatureOrPng = jsFeaturesOrPngs.get(i);
            JSONObject jsFeature = jsFeatureOrPng.isObject();
            if (jsFeature != null) {
                imFeatureOrPngs.add(parseFeature(depth + 1, jsFeature));
                continue;
            }

            JSONNumber jsPng = jsFeatureOrPng.isNumber();
            if (jsPng != null) {
                imFeatureOrPngs.add(parsePng(depth + 1, jsPng));
                continue;
            }

            throw new IllegalStateException();
        }

        return imFeatureOrPngs;
    }

    private ImFeatureOrPng parsePng(int depth, JSONNumber jsPng) {
        return new ImPng(depth, (int) jsPng.doubleValue());
    }

    private ImFeature parseFeature(int depth, JSONObject jsFeature) {
        String varCode = jsFeature.keySet().iterator().next();
        Var var = featureModel.getVar(varCode);
        JSONArray jsFeaturesOrPngs = jsFeature.get(varCode).isArray();
        List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(depth, jsFeaturesOrPngs);
        return new ImFeature(depth, var, imFeatureOrPngs);
    }

}
