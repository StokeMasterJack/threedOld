package com.tms.threed.previewPane.client;

/**
 * This class recieve events from various parts of the system and
 * dispatches them as needed
 */
public class NotificationCenter {


    public NotificationCenter() {


    }

    private native void subscribe() /*-{
        $wnd.NotificationCenter.addSubscriber('MSRP Updated', this, this.on_priceUpdate);
         $wnd.NotificationCenter;
    }-*/;
}
