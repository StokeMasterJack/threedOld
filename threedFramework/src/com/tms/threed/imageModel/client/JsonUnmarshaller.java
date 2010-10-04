package com.tms.threed.imageModel.client;

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
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.util.gwt.client.Console;

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


        SeriesId seriesId = featureModel.getSeriesId();
        return new ImSeries(0, seriesId, imViews);
    }

    public List<ImFeatureOrPng> parseFeaturesOrPngs(int depth, JSONArray jsFeaturesOrPngs) {
        List<ImFeatureOrPng> imFeatureOrPngs = new ArrayList<ImFeatureOrPng>();
        for (int i = 0; i < jsFeaturesOrPngs.size(); i++) {
            JSONObject jsFeatureOrPng = jsFeaturesOrPngs.get(i).isObject();
            ImFeatureOrPng featureOrPng = parseFeatureOrPng(depth, jsFeatureOrPng);
            imFeatureOrPngs.add(featureOrPng);
        }
        return imFeatureOrPngs;
    }

    public ImFeatureOrPng parseFeatureOrPng(int depth, JSONObject jsFeatureOrPng) {
        String key = jsFeatureOrPng.keySet().iterator().next();
        JSONValue value = jsFeatureOrPng.get(key);



        JSONArray array = value.isArray();
        if (array == null) {
            return parsePng(depth, jsFeatureOrPng);
        } else {
            return parseFeature(depth, jsFeatureOrPng);
        }

    }

    private ImFeatureOrPng parsePngOriginal(int depth, JSONNumber jsPng) {
        return new ImPng(depth, (int) jsPng.doubleValue());
    }

    private ImFeatureOrPng parsePng(int depth, JSONObject jsPng) {
        String sAngle = jsPng.keySet().iterator().next();
        int angle = Integer.parseInt(sAngle);
        String fingerprint = jsPng.get(sAngle).isString().stringValue();
        ImPng png = new ImPng(depth, angle);
        png.setFingerprint(fingerprint);
        return png;
    }

    private ImFeature parseFeature(int depth, JSONObject jsFeature) {
        String varCode = jsFeature.keySet().iterator().next();
        Var var = featureModel.getVar(varCode);
        JSONArray jsFeaturesOrPngs = jsFeature.get(varCode).isArray();
        List<ImFeatureOrPng> imFeatureOrPngs = parseFeaturesOrPngs(depth, jsFeaturesOrPngs);
        return new ImFeature(depth, var, imFeatureOrPngs);
    }

}
