package com.tms.threed.util.gwt.client.logging;

public class ScriptConsoleIE extends ScriptConsole {

    public native void log(String msg) /*-{
        console.log(msg);
    }-*/;

    @Override public void time(String msg) {
    }

    @Override public void timeEnd(String msg) {
    }

    @Override public void profile(String msg) {
    }

    @Override public void profileEnd(String msg) {
    }
}
