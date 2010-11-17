package com.tms.threed.previewPanel.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.tms.threed.previewPanel.client.thumbsPanel.ThumbsPanel;

public class PreviewPanel extends AbstractPreviewPanel {

    public static final int PREFERRED_WIDTH_PX = TopImagePanel.PREFERRED_WIDTH_PX;
    public static final int PREFERRED_HEIGHT_PX = TopImagePanel.PREFERRED_HEIGHT_PX + ThumbsPanel.PREFERRED_HEIGHT_PX;

    public PreviewPanel(TopImagePanel topImagePanel, BottomPanel bottomPanel) {
//        assert previewPanelModel != null;
        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.PX);
        dock.setPixelSize(PREFERRED_WIDTH_PX, PREFERRED_HEIGHT_PX);

        bottomPanel.setSize("100%", "100%");
        topImagePanel.setSize("100%", "100%");

        dock.addSouth(bottomPanel, ThumbsPanel.PREFERRED_HEIGHT_PX);
        dock.add(topImagePanel);

        initWidget(dock);
        ensureDebugId("PreviewPanel");

        PreviewPanelStyles.set(this);

    }

    @Override public int getPreferredWidthPx(){
        return PREFERRED_WIDTH_PX;
    }

     @Override public int getPreferredHeightPx(){
        return PREFERRED_HEIGHT_PX;
    }

   
}
