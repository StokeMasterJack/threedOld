package com.tms.threed.threedModel.client;

import com.google.gwt.json.client.JSONArray;
import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.featureModel.client.JsFeatureModel;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.imageModel.shared.ImSeries;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.threedModel.shared.ThreedModel;
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

    public ThreedModel createModelFromJs(JsThreedModel jsThreedModel) {

        ThreedConfig threedConfig = createThreedConfigFromJs(jsThreedModel.getJsThreedConfig());
        SeriesKey seriesKey = createSeriesKeyFromJs(jsThreedModel.getJsSeriesKey());

        com.tms.threed.featureModel.client.JsonUnmarshaller jsonFeatureModelBuilder = new com.tms.threed.featureModel.client.JsonUnmarshaller();

        JsFeatureModel jsFeatureModel = jsThreedModel.getJsFeatureModel();
        FeatureModel featureModel = jsonFeatureModelBuilder.createFeatureModelFromJson(seriesKey, jsFeatureModel);
        featureModel.setSeriesKey(seriesKey);

        SeriesInfo seriesInfo = SeriesInfoBuilder.createSeriesInfo(featureModel.getSeriesKey());

        com.tms.threed.imageModel.builders.client.JsonUnmarshaller imJsonParser = new com.tms.threed.imageModel.builders.client.JsonUnmarshaller(featureModel, seriesInfo);
//        JsonImageModelBuilder jsonImageModelBuilder = new JsonImageModelBuilder(featureModel, seriesInfo);

        JSONArray jsViews = jsThreedModel.getJsViews();
        ImSeries imSeries = imJsonParser.parseSeries(jsViews);

//        JsImNode jsImSeries = jsThreedModel.getJsImageModel();
//        ImSeries imSeries = jsonImageModelBuilder.createImSeries(jsImSeries);

        return new ThreedModel(threedConfig, featureModel, imSeries);
    }

    private ThreedConfig createThreedConfigFromJs(JsThreedConfig jsThreedConfig) {

        Path rootHttp = new Path(jsThreedConfig.getThreedRootHttp());
        Path rootFs = new Path(jsThreedConfig.getThreedRootFileSystem());
        Path pngRootHttp = new Path(jsThreedConfig.getPngRootHttp());
        Path jpgRootHttp = new Path(jsThreedConfig.getJpgRootHttp());

        return new ThreedConfig(rootHttp, rootFs, pngRootHttp, jpgRootHttp);
    }

    private SeriesKey createSeriesKeyFromJs(JsSeriesKey jsSeriesKey) {
        return jsSeriesKey.getSeriesKey();
    }


}