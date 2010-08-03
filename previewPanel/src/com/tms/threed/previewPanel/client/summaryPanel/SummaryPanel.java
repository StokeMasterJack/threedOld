package com.tms.threed.previewPanel.client.summaryPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.tms.threed.previewPanel.client.AbstractPreviewPanel;
import com.tms.threed.previewPanel.client.PreviewPanelStyles;

public class SummaryPanel extends AbstractPreviewPanel {

    private static final int PREFERRED_WIDTH_PX = 374;
    private static final int PREFERRED_HEIGHT_PX = 229 * 2;

    public SummaryPanel(TopImagePanel topPanel, BottomImagePanel bottomPanel) {
        FlowPanel flow = new FlowPanel();
        flow.add(topPanel);
        flow.add(bottomPanel);
        initWidget(flow);
        PreviewPanelStyles.set(this);
    }

    @Override public int getPreferredWidthPx() {
        return PREFERRED_WIDTH_PX;
    }

    @Override public int getPreferredHeightPx() {
        return PREFERRED_HEIGHT_PX;
    }
}