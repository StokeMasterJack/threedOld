package com.tms.threed.previewPanel.client.summaryPanel;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tms.threed.previewPanel.client.buttonBars.AngleButtonPanel;
import com.tms.threed.previewPanel.client.buttonBars.exterior.ExteriorButtonPanel;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class FooterPanel extends AbsolutePanel {

    private static int PREFERRED_HEIGHT_PX = 34;

    public FooterPanel(ExteriorButtonPanel exteriorButtonPanel) {
        exteriorButtonPanel.setVisible(true);
        ensureDebugId(getSimpleName(this));
        addButtonPanel(exteriorButtonPanel);
    }

    void addButtonPanel(AngleButtonPanel angleButtonPanel) {

        FlowPanel fp = new FlowPanel();
        fp.setWidth("100%");
        fp.add(angleButtonPanel);

        int gap = 4;
        int top = PREFERRED_HEIGHT_PX - angleButtonPanel.getPreferredHeightPx() - gap;
        add(fp, 0, top);
    }

    public int getPreferredHeightPx() {
        return PREFERRED_HEIGHT_PX;
    }


}
