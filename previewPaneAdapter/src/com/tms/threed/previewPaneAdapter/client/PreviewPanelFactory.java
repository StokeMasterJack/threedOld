package com.tms.threed.previewPaneAdapter.client;

import com.tms.threed.previewPane.client.PreviewPane;
import com.tms.threed.previewPane.client.PreviewPaneImpl;
import com.tms.threed.previewPane.client.PreviewPaneSummary;


public class PreviewPanelFactory {

    public static PreviewPane makePreviewPanel(boolean summaryMode) {
        if (summaryMode) return new PreviewPaneSummary();
        else return new PreviewPaneImpl();
    }

}
