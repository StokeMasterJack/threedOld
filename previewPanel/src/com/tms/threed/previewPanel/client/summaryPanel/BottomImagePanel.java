package com.tms.threed.previewPanel.client.summaryPanel;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.previewPanel.client.ThreedImagePanel;

public class BottomImagePanel extends AbsolutePanel {

    public static final int PREFERRED_WIDTH_PX = 374;
    public static final int PREFERRED_HEIGHT_PX = 229;

    public BottomImagePanel(ThreedImagePanel threedImagePanel) {
        this.setSize(PREFERRED_WIDTH_PX + "px", PREFERRED_HEIGHT_PX + "px");

        add(threedImagePanel, 0, 0);
    }

}