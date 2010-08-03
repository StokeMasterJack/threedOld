package com.tms.threed.util.gwt.client.logging;

public class ScriptConsole {

    public native void log(String msg) /*-{
        console.log(msg);
    }-*/;

    public native void time(String msg) /*-{
    }-*/;

    public native void timeEnd(String msg) /*-{
    }-*/;

    public native void profile(String msg) /*-{
    }-*/;

    public native void profileEnd(String msg) /*-{
    }-*/;
}
