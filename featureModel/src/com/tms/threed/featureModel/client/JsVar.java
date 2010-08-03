package com.tms.threed.featureModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class JsVar extends JavaScriptObject {

    protected JsVar() {}

    public native String getCode() /*-{
       return this.code;
    }-*/;

    public native JsArray<JsVar> getChildNodes() /*-{
       return this.childNodes;
    }-*/;

    public native String getName() /*-{
       if(this.name) return this.name;
       return null;
    }-*/;

    public native String getCardinality() /*-{
       if(this.cardinality) return this.cardinality;
        else return null;
    }-*/;

    public native String getDerivedS() /*-{
        if(this.derived) return this.derived + "";
        else return null;
    }-*/;

    public native String getDefaultValueS() /*-{
       if(this.defaultValue) return this.defaultValue;
       else return null;
    }-*/;

    public native String getManditoryS() /*-{
       if(this.manditory) return this.manditory + "";
       else return null;
    }-*/;

    public Boolean getDerived() {
        String s = getDerivedS();
        if (s!=null) return new Boolean(s);
        else return null;
    }

    public Boolean getDefaultValue() {
        String s = getDefaultValueS();
        if (s!=null) return new Boolean(s);
        else return null;
    }

    public Boolean getManditory() {
        String s = getManditoryS();
        if (s!=null) return new Boolean(s);
        else return null;
    }


}
