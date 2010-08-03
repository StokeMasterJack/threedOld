package com.tms.threed.threedModel.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.tms.threed.threedCore.shared.*;

public final class JsSeriesKey extends JavaScriptObject {

    protected JsSeriesKey() {}

    private native String getName() /*-{
        return this.name;
    }-*/;

    private native int getYear() /*-{
        return this.year;
    }-*/;

    public SeriesKey getSeriesKey() {
        int y = getYear();
        String n = getName();
        if(n == null) throw new IllegalStateException();
        return new SeriesKey(y, n);
    }


}