package com.tms.threed.featureModel.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class JsFeatureModel extends JavaScriptObject {

    protected JsFeatureModel() {}

    public native JsVar getRootVar() /*-{
       return this.rootVar;
    }-*/;

    public native String getDisplayName() /*-{
       return this.displayName;
    }-*/;

    public native JsBoolExpr getConstraints() /*-{
       return this.constraints;
    }-*/;


}