package com.tms.threed.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.tms.threed.featureModel.client.JsFeatureModel;

public final class JsThreedModel extends JavaScriptObject {

    protected JsThreedModel() {}

    public native JsThreedConfig getJsThreedConfig() /*-{
        return this.threedConfig;
    }-*/;

    public native JsSeriesKey getJsSeriesKey() /*-{
        return this.seriesKey;
    }-*/;

    public native JsFeatureModel getJsFeatureModel() /*-{
        return this.featureModel;
    }-*/;

    public native JSONArray getJsViews() /*-{
        var v = this.views
        var func = @com.google.gwt.json.client.JSONParser::typeMap[typeof v];
        return func ? func(v) : @com.google.gwt.json.client.JSONParser::throwUnknownTypeException(Ljava/lang/String;)(typeof v);
    }-*/;


}
