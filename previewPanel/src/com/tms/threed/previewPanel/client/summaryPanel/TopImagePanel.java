package com.tms.threed.previewPanel.client.summaryPanel;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.previewPanel.client.PreviewPanelStyles;
import com.tms.threed.previewPanel.client.ThreedImagePanel;
import com.tms.threed.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.previewPanel.client.dragToSpin.DragToSpin;

import static com.tms.threed.util.lang.shared.Strings.getSimpleName;

public class TopImagePanel extends AbsolutePanel {


    public static final int PREFERRED_WIDTH_PX = 374;
    public static final int PREFERRED_HEIGHT_PX = 229;

    public TopImagePanel(ThreedImagePanel threedImagePanel, DragToSpin<ClearGif> dragToSpin, FooterPanel footerPanel) {
       this.setPixelSize(PREFERRED_WIDTH_PX, PREFERRED_HEIGHT_PX);
        ensureDebugId(getSimpleName(this));

        ClearGif dragDiv = new ClearGif(PREFERRED_WIDTH_PX,PREFERRED_HEIGHT_PX);
        dragToSpin.attachToTarget(dragDiv);
        dragToSpin.setEnabled(true);

        footerPanel.setPixelSize(PREFERRED_WIDTH_PX, footerPanel.getPreferredHeightPx());
        int footerPanelTop = PREFERRED_HEIGHT_PX - footerPanel.getPreferredHeightPx();

        add(threedImagePanel, 0, 0);
        add(dragDiv, 0, 0);
        add(footerPanel, 0, footerPanelTop);


        PreviewPanelStyles.set(this);


    }


}