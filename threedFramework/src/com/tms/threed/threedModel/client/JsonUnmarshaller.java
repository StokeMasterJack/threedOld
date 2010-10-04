package com.tms.threed.threedModel.client;

import com.google.gwt.json.client.JSONArray;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.featureModel.client.JsFeatureModel;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedModel.shared.ThreedModel;
import com.tms.threed.util.gwt.client.Console;
import com.tms.threed.util.lang.shared.Path;

public class JsonUnmarshaller {

    public JsonUnmarshaller() {}

    public ThreedModel createModelFromJsInPage() {
        return createModelFromJs(getJsModelFromJsInPage());
    }

    public ThreedModel createModelFromJsonText(String jsonText) {
        return createModelFromJs(getJsModelFromJsonText(jsonText));
    }

    public static native JsThreedModel getJsModelFromJsInPage() /*-{
        return $wnd.threedModel;
    }-*/;

    public static native JsThreedModel getJsModelFromJsonText(String jsonText) /*-{
        eval("var xTmp = " + jsonText );
        return xTmp;
       }-*/;

    public void test() throws Exception {

    }

    private SeriesKey getSeriesKey(JsSeriesId jsSeriesId) {
        int y = jsSeriesId.getYear();
        String on = jsSeriesId.getName();
        return new SeriesKey(y, on);
    }

    private SeriesId getSeriesId(JsSeriesId jsSeriesId) {
        SeriesKey sk = getSeriesKey(jsSeriesId);
        int v = jsSeriesId.getVersion();
        return new SeriesId(sk, v);
    }

    public ThreedModel createModelFromJs(JsThreedModel jsThreedModel) {

//        ThreedConfig threedConfig = createThreedConfigFromJs(jsThreedModel.getJsThreedConfig());
        JsSeriesId jsSeriesId = jsThreedModel.getJsSeriesId();

        SeriesId seriesId = getSeriesId(jsSeriesId);

        Console.log("SeriesId[" + seriesId + "]");

        com.tms.threed.featureModel.client.JsonUnmarshaller jsonFeatureModelBuilder = new com.tms.threed.featureModel.client.JsonUnmarshaller(seriesId);

        JsFeatureModel jsFeatureModel = jsThreedModel.getJsFeatureModel();
        FeatureModel featureModel = jsonFeatureModelBuilder.createFeatureModelFromJson(jsFeatureModel);

        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(featureModel.getSeriesKey());

        com.tms.threed.imageModel.client.JsonUnmarshaller imJsonParser = new com.tms.threed.imageModel.client.JsonUnmarshaller(featureModel, seriesInfo);
//        JsonImageModelBuilder jsonImageModelBuilder = new JsonImageModelBuilder(featureModel, seriesInfo);

        JSONArray jsViews = jsThreedModel.getJsViews();
        ImSeries imSeries = imJsonParser.parseSeries(jsViews);

//        JsImNode jsImSeries = jsThreedModel.getJsImageModel();
//        ImSeries imSeries = jsonImageModelBuilder.createImSeries(jsImSeries);

        return new ThreedModel(featureModel, imSeries,new Path(jsThreedModel.getHttpImageRoot()));
    }

//    private ThreedConfig createThreedConfigFromJs(JsThreedConfig jsThreedConfig) {
//
//        Path rootHttp = new Path(jsThreedConfig.getThreedRootHttp());
//        Path rootFs = new Path(jsThreedConfig.getThreedRootFileSystem());
//        Path pngRootHttp = new Path(jsThreedConfig.getPngRootHttp());
//        Path jpgRootHttp = new Path(jsThreedConfig.getJpgRootHttp());
//
//        return new ThreedConfig(rootHttp, rootFs, pngRootHttp, jpgRootHttp);
//    }



}