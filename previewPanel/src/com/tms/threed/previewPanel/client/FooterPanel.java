package com.tms.threed.previewPanel.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tms.threed.previewPanel.client.buttonBars.AngleButtonPanel;
import com.tms.threed.previewPanel.client.buttonBars.exterior.ExteriorButtonPanel;
import com.tms.threed.previewPanel.client.buttonBars.interior.InteriorButtonPanel;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class FooterPanel extends AbsolutePanel {

    private static int PREFERRED_HEIGHT_PX = 34;

    public FooterPanel(InteriorButtonPanel interiorButtonPanel, ExteriorButtonPanel exteriorButtonPanel) {
        ensureDebugId(getSimpleName(this));
        addButtonPanel(exteriorButtonPanel);
        addButtonPanel(interiorButtonPanel);
    }

    void addButtonPanel(AngleButtonPanel angleButtonPanel) {
        FlowPanel fp = new FlowPanel();
        fp.setWidth("100%");
        fp.add(angleButtonPanel);

//        fp.getElement().getStyle().setZIndex(-1000);

//        angleButtonPanel.getElement().getStyle().setZIndex(2000);
//        getElement().getStyle().setZIndex(300);

        int gap = 4;
        int top = PREFERRED_HEIGHT_PX - angleButtonPanel.getPreferredHeightPx() - gap;
        add(fp, 0, top);
    }

    public int getPreferredHeightPx() {
        return PREFERRED_HEIGHT_PX;
    }


}
