package com.tms.threed.previewPanel.client.dragToSpin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.tms.threed.previewPanel.client.TopImagePanel;

public class ClearGif extends Image {

    public ClearGif() {
        super(GWT.getModuleBaseURL() + "clear.cache.gif");
        setPixelSize(TopImagePanel.PREFERRED_WIDTH_PX, TopImagePanel.PREFERRED_HEIGHT_PX);
    }

    public ClearGif(int widthPx,int heightPx) {
        super(GWT.getModuleBaseURL() + "clear.cache.gif");
        setPixelSize(widthPx, heightPx);
    }


}
